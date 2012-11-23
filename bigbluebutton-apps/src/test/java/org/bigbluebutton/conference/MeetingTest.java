package org.bigbluebutton.conference;

import static org.easymock.EasyMock.*;

import java.util.Collection;

import org.bigbluebutton.conference.Meeting;
import org.bigbluebutton.conference.messages.in.meetings.MeetingStart;
import org.bigbluebutton.conference.messages.in.users.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.users.UserJoin;
import org.bigbluebutton.conference.messages.in.users.UsersQuery;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.service.users.MessageOutMatcher;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.compatibility.flex.messaging.io.ArrayCollection;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MeetingTest {
	
	private Meeting meeting;
	private IMessageOutGateway msgOutGW;
	private UserVO test_moderator;
	private UserVO test_viewer;
	final String meetingID = "0123456789";
	
	@BeforeTest
	public void init(){
		msgOutGW = createMock(IMessageOutGateway.class);
		meeting = new Meeting(this.meetingID, "Test Meeting", msgOutGW);
		test_moderator = new UserVO("1111", "ext1111", Role.MODERATOR, "John Doe");
		test_viewer = new UserVO("1112", "ext1112", Role.VIEWER, "Janet Doe");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void Meeting_WhenMessageOutGatewayIsNull_ShouldThrowException(){
		new Meeting(this.meetingID, "Test Meeting", null);
	}
	
	/*@Test(groups={"user.moderator.query"},dependsOnGroups={"meeting.start","user.moderator.join"})
	public void ProcessMessage_WhenUsersQuery_ShouldMessageOutGatewayAccept(){
		
		//Insert a new user, so we can test with 2 users...
		UserJoin newUser = new UserJoin(meetingID, test_viewer);
		meeting.processMessage(newUser);
		
		reset(msgOutGW);
		UsersQuery msg = new UsersQuery(this.meetingID, this.test_moderator.intUserID);
		
		Collection<UserVO> pm = new ArrayCollection<UserVO>();
		pm.add(test_moderator);
		pm.add(test_viewer);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UsersQueryReply(meetingID, this.test_moderator.intUserID, pm)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}*/
	
	@Test//(groups={"user.moderator.join"},dependsOnGroups={"meeting.start"})
	public void ProcessMessage_WhenModeratorJoin_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		
		//First, it should join the moderator to the meeting...
		UserJoin msg = new UserJoin(meetingID, test_moderator);
		
		//Then, like it's moderator, this is the new presenter...
		NewPresenterVO newPresenter = new NewPresenterVO(this.test_moderator.intUserID, this.test_moderator.name, false, "");
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOutWithSecondParam(new UserJoined(meetingID, test_moderator),new UserPresenterChanged(meetingID,newPresenter)));
		expectLastCall().times(2);
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
	/*@Test
	public void ProcessMessage_WhenUserAssignPresenter_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		UserAssignPresenter msg = new UserAssignPresenter(meetingID, newPresenterUserID, assignedByUserID)
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new UserJoined(meetingID, test_user)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}*/
	
	@Test//(groups = {"meeting.start"})
	public void ProcessMessage_WhenMeetingStart_ShouldMessageOutGatewayAccept(){
		reset(msgOutGW);
		MeetingStart msg = new MeetingStart(this.meetingID);
		
		msgOutGW.accept(MessageOutMatcher.eqMessageOut(new MeetingStarted(meetingID)));
		
		replay(msgOutGW);
		
		meeting.processMessage(msg);
		verify(msgOutGW);
	}
	
}
