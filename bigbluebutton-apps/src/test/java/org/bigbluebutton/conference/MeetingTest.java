package org.bigbluebutton.conference;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collection;

import org.bigbluebutton.conference.Meeting;
import org.bigbluebutton.conference.messages.in.chat.PublicChatHistoryQuery;
import org.bigbluebutton.conference.messages.in.chat.PublicChatMessageSend;
import org.bigbluebutton.conference.messages.in.meetings.MeetingForceEnd;
import org.bigbluebutton.conference.messages.in.meetings.MeetingStart;
import org.bigbluebutton.conference.messages.in.users.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.users.UserHandStatusChange;
import org.bigbluebutton.conference.messages.in.users.UserJoin;
import org.bigbluebutton.conference.messages.in.users.UserLeave;
import org.bigbluebutton.conference.messages.in.users.UserVideoStatusChange;
import org.bigbluebutton.conference.messages.in.users.UserVoiceStatusChange;
import org.bigbluebutton.conference.messages.in.users.UsersQuery;
import org.bigbluebutton.conference.messages.out.chat.PublicChatHistoryQueryReply;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserHandStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UserVideoStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserVoiceStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.service.chat.ChatMessageVO;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MeetingTest {
	
	private Meeting meeting;
	private IMessageOutGateway msgOutGW;
	private UserVO test_moderator;
	private UserVO test_viewer;
	private ChatMessageVO test_chat_msg;
	final String meetingID = "0123456789";
	
	@BeforeTest
	public void init(){
		msgOutGW = createMock(IMessageOutGateway.class);
		
		meeting = new Meeting(this.meetingID, "Test Meeting", msgOutGW);
		
		test_moderator = new UserVO("1111", "ext1111", Role.MODERATOR, "John Doe");
		test_viewer = new UserVO("1112", "ext1112", Role.VIEWER, "Janet Doe");
		
		test_chat_msg =  new ChatMessageVO();
		test_chat_msg.chatType = ChatMessageVO.PUBLIC_TYPE; 
		test_chat_msg.fromUserID = test_moderator.intUserID;
		test_chat_msg.fromUsername = test_viewer.name;
		test_chat_msg.fromColor = "0";
		test_chat_msg.fromTime = Double.valueOf(System.currentTimeMillis());   
		test_chat_msg.fromTimezoneOffset = Long.valueOf(0);
		test_chat_msg.fromLang = "en"; 	 
		test_chat_msg.message = "This is a test message";
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void Meeting_WhenMessageOutGatewayIsNull_ShouldThrowException(){
		new Meeting(this.meetingID, "Test Meeting", null);
	}
	
	@Test(groups={"meeting.start"})
	public void ProcessMessage_WhenMeetingStart_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		MeetingStart msg = new MeetingStart(this.meetingID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new MeetingStarted(meetingID)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.moderator.join"},dependsOnGroups={"meeting.start"})
	public void ProcessMessage_WhenModeratorJoin_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		//First, it should join the moderator to the meeting...
		UserJoin msg = new UserJoin(meetingID, test_moderator);
		
		//Then, like it's moderator, this is the new presenter...
		NewPresenterVO newPresenter = new NewPresenterVO(this.test_moderator.intUserID, this.test_moderator.name, false, this.test_moderator.intUserID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserJoined(meetingID, test_moderator),new UserPresenterChanged(meetingID,newPresenter)));
		expectLastCall().times(2);
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.viewer.join"},dependsOnGroups={"meeting.start","user.moderator.join"})
	public void ProcessMessage_WhenViewerJoin_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserJoin msg = new UserJoin(meetingID, test_viewer);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserJoined(meetingID, test_viewer)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUsersQuery_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		UsersQuery msg = new UsersQuery(this.meetingID, this.test_moderator.intUserID);
		
		Collection<UserVO> pm = new ArrayList<UserVO>();
		pm.add(test_moderator);
		pm.add(test_viewer);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UsersQueryReply(meetingID, this.test_moderator.intUserID, pm)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenModeratorAssignsPresenterViewer_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		//Create a Message with the Assign Presenter procedure...
		UserAssignPresenter msg = new UserAssignPresenter(meetingID, this.test_viewer.intUserID, this.test_moderator.intUserID);
		
		//Create a NewPresenterVO which is the value sent by the messageOutGateway
		NewPresenterVO npv = new NewPresenterVO(this.test_viewer.intUserID, this.test_viewer.name, true, this.test_moderator.intUserID);
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserPresenterChanged(meetingID, npv)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserRaiseHand_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserHandStatusChange msg = new UserHandStatusChange(meetingID, this.test_viewer.intUserID, true, "");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserHandStatusChanged(meetingID,this.test_viewer.intUserID,true,"")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserJoinsAudio_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserVoiceStatusChange msg = new UserVoiceStatusChange(this.meetingID,this.test_viewer.intUserID,true,"streamTest");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserVoiceStatusChanged(meetingID,this.test_viewer.intUserID,true,"streamTest")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserShareWebcam_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserVideoStatusChange msg = new UserVideoStatusChange(this.meetingID,this.test_viewer.intUserID,true,"streamTest");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserVideoStatusChanged(meetingID,this.test_viewer.intUserID,true,"streamTest")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	
	/*
	 * MODULE: Chat
	 * 
	 * */
	
	@Test(groups={"chat.tests.firstmessage"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserSendPublicChat_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		
		
		PublicChatMessageSend msg = new PublicChatMessageSend(meetingID, test_chat_msg);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PublicChatMessageSent(meetingID, test_chat_msg)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"chat.tests"},dependsOnGroups={"chat.tests.firstmessage"})
	public void ProcessMessage_WhenPublicChatHistoryRequested_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		PublicChatHistoryQuery msg = new PublicChatHistoryQuery(meetingID,this.test_viewer.intUserID);
		Collection<ChatMessageVO> all_messages = new ArrayList<ChatMessageVO>();
		all_messages.add(test_chat_msg);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PublicChatHistoryQueryReply(meetingID, this.test_viewer.intUserID,all_messages)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	
	
	@Test(groups={"user.leave"},dependsOnGroups={"user.tests"})
	public void ProcessMessage_WhenUserLeave_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserLeave msg = new UserLeave(this.meetingID,this.test_viewer.intUserID);
		
		//like test_viewer is the presenter, the new presenter is the moderator
		NewPresenterVO newPresenter = new NewPresenterVO(this.test_moderator.intUserID, this.test_moderator.name, false, this.test_moderator.intUserID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserLeft(meetingID,this.test_viewer.intUserID),new UserPresenterChanged(meetingID,newPresenter)));
		expectLastCall().times(2);
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"meeting.end"},dependsOnGroups={"user.leave"})
	public void ProcessMessage_WhenMeetingForceEnd_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		MeetingForceEnd msg = new MeetingForceEnd(this.meetingID);
		
		//like we have only one user, it should kick just one user
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserKicked(meetingID,test_moderator.intUserID)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	
	
}
