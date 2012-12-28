package org.bigbluebutton.conference;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collection;

import org.bigbluebutton.conference.Meeting;
import org.bigbluebutton.conference.messages.in.chat.PublicChatHistoryQuery;
import org.bigbluebutton.conference.messages.in.chat.PublicChatMessageSend;
import org.bigbluebutton.conference.messages.in.meetings.MeetingForceEnd;
import org.bigbluebutton.conference.messages.in.meetings.MeetingStart;
import org.bigbluebutton.conference.messages.in.presentation.PresentationRemove;
import org.bigbluebutton.conference.messages.in.presentation.PresentationShare;
import org.bigbluebutton.conference.messages.in.presentation.PresentationSlideChange;
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
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;
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
	private UserVO testModerator;
	private UserVO testViewer;
	private ChatMessageVO testChatMsg;
	private String presentationName;
	final String meetingID = "0123456789";
	
	@BeforeTest
	public void init(){
		msgOutGW = createMock(IMessageOutGateway.class);
		
		meeting = new Meeting(this.meetingID, "Test Meeting", msgOutGW);
		
		testModerator = new UserVO("1111", "ext1111", Role.MODERATOR, "John Doe");
		testViewer = new UserVO("1112", "ext1112", Role.VIEWER, "Janet Doe");
		
		testChatMsg =  new ChatMessageVO();
		testChatMsg.chatType = ChatMessageVO.PUBLIC_TYPE; 
		testChatMsg.fromUserID = testModerator.intUserID;
		testChatMsg.fromUsername = testModerator.name;
		testChatMsg.fromColor = "0";
		testChatMsg.fromTime = Double.valueOf(System.currentTimeMillis());   
		testChatMsg.fromTimezoneOffset = Long.valueOf(0);
		testChatMsg.fromLang = "en"; 	 
		testChatMsg.message = "This is a test message";
		
		presentationName = "Default";
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
		UserJoin msg = new UserJoin(meetingID, testModerator);
		
		//Then, like it's moderator, this is the new presenter...
		NewPresenterVO newPresenter = new NewPresenterVO(this.testModerator.intUserID, this.testModerator.name, false, this.testModerator.intUserID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserJoined(meetingID, testModerator),new UserPresenterChanged(meetingID,newPresenter)));
		expectLastCall().times(2);
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.viewer.join"},dependsOnGroups={"meeting.start","user.moderator.join"})
	public void ProcessMessage_WhenViewerJoin_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserJoin msg = new UserJoin(meetingID, testViewer);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserJoined(meetingID, testViewer)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUsersQuery_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		UsersQuery msg = new UsersQuery(this.meetingID, this.testModerator.intUserID);
		
		Collection<UserVO> pm = new ArrayList<UserVO>();
		pm.add(testModerator);
		pm.add(testViewer);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UsersQueryReply(meetingID, this.testModerator.intUserID, pm)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenModeratorAssignsPresenterViewer_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		//Create a Message with the Assign Presenter procedure...
		UserAssignPresenter msg = new UserAssignPresenter(meetingID, this.testViewer.intUserID, this.testModerator.intUserID);
		
		//Create a NewPresenterVO which is the value sent by the messageOutGateway
		NewPresenterVO npv = new NewPresenterVO(this.testViewer.intUserID, this.testViewer.name, true, this.testModerator.intUserID);
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserPresenterChanged(meetingID, npv)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserRaiseHand_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserHandStatusChange msg = new UserHandStatusChange(meetingID, this.testViewer.intUserID, true, "");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserHandStatusChanged(meetingID,this.testViewer.intUserID,true,"")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserJoinsAudio_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserVoiceStatusChange msg = new UserVoiceStatusChange(this.meetingID,this.testViewer.intUserID,true,"streamTest");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserVoiceStatusChanged(meetingID,this.testViewer.intUserID,true,"streamTest")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenUserShareWebcam_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserVideoStatusChange msg = new UserVideoStatusChange(this.meetingID,this.testViewer.intUserID,true,"streamTest");
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserVideoStatusChanged(meetingID,this.testViewer.intUserID,true,"streamTest")));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"user.leave"},dependsOnGroups={"user.tests"})
	public void ProcessMessage_WhenUserLeave_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		UserLeave msg = new UserLeave(this.meetingID,this.testViewer.intUserID);
		
		//like test_viewer is the presenter, the new presenter is the moderator
		NewPresenterVO newPresenter = new NewPresenterVO(this.testModerator.intUserID, this.testModerator.name, false, this.testModerator.intUserID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserLeft(meetingID,this.testViewer.intUserID),new UserPresenterChanged(meetingID,newPresenter)));
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
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserKicked(meetingID,testModerator.intUserID)));
		
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
		
		
		
		PublicChatMessageSend msg = new PublicChatMessageSend(meetingID, testChatMsg);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PublicChatMessageSent(meetingID, testChatMsg)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"chat.tests"},dependsOnGroups={"chat.tests.firstmessage"})
	public void ProcessMessage_WhenPublicChatHistoryRequested_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		PublicChatHistoryQuery msg = new PublicChatHistoryQuery(meetingID,this.testViewer.intUserID);
		Collection<ChatMessageVO> all_messages = new ArrayList<ChatMessageVO>();
		all_messages.add(testChatMsg);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PublicChatHistoryQueryReply(meetingID, this.testViewer.intUserID,all_messages)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	/*
	 * 
	 * MODULE: Presentation
	 * 
	 * */
	@Test(groups={"presentation.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenPresentationShare_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		PresentationShare msg = new PresentationShare(meetingID, presentationName, true); 
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PresentationShared(meetingID, presentationName, true)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"presentation.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenPresentationRemove_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		PresentationRemove msg = new PresentationRemove(meetingID, presentationName); 
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PresentationRemoved(meetingID, presentationName)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	@Test(groups={"presentation.tests"},dependsOnGroups={"user.moderator.join","user.viewer.join"})
	public void ProcessMessage_WhenPresentationSlideChange_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		PresentationSlideChange msg = new PresentationSlideChange(meetingID, 1); 
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new PresentationSlideChanged(meetingID, msg.slideNum)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
}
