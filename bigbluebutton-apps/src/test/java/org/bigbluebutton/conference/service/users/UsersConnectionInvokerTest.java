package org.bigbluebutton.conference.service.users;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ClientMessageMatcher;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.Role;
import org.bigbluebutton.conference.messages.out.meetings.MeetingEnded;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class UsersConnectionInvokerTest {
	UsersConnectionInvoker usersConnectionInvoker;
	IConnectionInvokerService connInvokerService;
	final String meetingID = "1234567890";
	
	
	@BeforeTest
	public void init(){
		usersConnectionInvoker = new UsersConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		usersConnectionInvoker.setConnInvokerService(connInvokerService);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void Accept_WhenParamIsNull_ShouldThrowException() {
		usersConnectionInvoker.accept(null);
	}
	
	@Test
	public void Accept_WhenMeetingEnded_ShouldSendMessage() {
		reset(connInvokerService);
		MeetingEnded me = new MeetingEnded(meetingID);
		
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", me.meetingID);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, me.meetingID, "UserLogoutCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(me);
		verify(connInvokerService);
	}
	  
	@Test
	public void Accept_WhenUserJoined_ShouldSendMessage() {
		UserVO user = new UserVO("1111", "ext1111", Role.VIEWER, "John Doe");
		UserJoined userJoinedMsg = new UserJoined(meetingID, user);
		reset(connInvokerService);
		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("meetingID", userJoinedMsg.meetingID);
		UserVO.toMap(userJoinedMsg.user, message);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, userJoinedMsg.meetingID, "UserJoinedCommand", message);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(m));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(userJoinedMsg);
		verify(connInvokerService);
		
	}
	
	@Test
	public void Accept_WhenUserLeft_ShouldSendMessage() {
		reset(connInvokerService);
		
		UserLeft userLeftMsg = new UserLeft(meetingID,"1111");
		
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", userLeftMsg.meetingID);
		message.put("userID", userLeftMsg.userID);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, userLeftMsg.meetingID, "UserLeftCommand", message);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(m));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(userLeftMsg);
		verify(connInvokerService);
		
	}
	
	@Test
	public void Accept_WhenUserKicked_ShouldSendMessage() {
		reset(connInvokerService);
		UserKicked userKickedMsg = new UserKicked(meetingID, "1111");
		
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", userKickedMsg.meetingID);
		message.put("userID", userKickedMsg.userID);
		
		ClientMessage cm = new ClientMessage(ClientMessage.DIRECT, userKickedMsg.userID, "UserKickCommand", message);
		
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(userKickedMsg);
		verify(connInvokerService);
	}
	
	@Test
	public void Accept_WhenUserPresenterChanged_ShouldSendMessage() {
		reset(connInvokerService);
		
		NewPresenterVO npvo = new NewPresenterVO("1111", "John Doe", false, "");
		UserPresenterChanged userPresenterChangedMsg = new UserPresenterChanged(meetingID, npvo);
		
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", userPresenterChangedMsg.meetingID);
		message.put("newPresenterUserID", userPresenterChangedMsg.pres.newPresenterUserID);
		message.put("newPresenterName", userPresenterChangedMsg.pres.newPresenterName);
		message.put("assignedByUser", userPresenterChangedMsg.pres.assignedByUser);
		message.put("assignedByUserID", userPresenterChangedMsg.pres.assignedByUserID);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, userPresenterChangedMsg.meetingID, "AssignPresenterCommand", message);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(m));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(userPresenterChangedMsg);
		verify(connInvokerService);
	}
	
	@Test
	public void Accept_WhenUsersQueryReply_ShouldSendMessage() {
		reset(connInvokerService);
		
		UserVO uvo = new UserVO("1112", "ext1112", Role.VIEWER, "Jane Doe");
		ArrayList<UserVO> arr = new ArrayList<UserVO>();
		arr.add(uvo);
		
		UsersQueryReply usersQueryMsg = new UsersQueryReply(meetingID, "1111", arr);
		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("count", usersQueryMsg.users.size());
		
		if (usersQueryMsg.users.size() > 0) {
			Collection<UserVO> pc = usersQueryMsg.users;
	    	Map<String, Object> pm = new HashMap<String, Object>();
	    	for (Iterator<UserVO> it = pc.iterator(); it.hasNext();) {
	    		UserVO uv = (UserVO) it.next();
	    		
	    		Map<String, Object> m = new HashMap<String, Object>();
	    		UserVO.toMap(uv, m);
	    		
	    		pm.put(uv.intUserID, m); 
	    	}  
	    	message.put("users", pm);
		}		
		
		ClientMessage m = new ClientMessage(ClientMessage.DIRECT, usersQueryMsg.userID, "UsersListQueryReply", message);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(m));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(usersQueryMsg);
		verify(connInvokerService);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void SetConnInvokerService_WhenParamIsNull_ShouldThrowException() {
		usersConnectionInvoker.setConnInvokerService(null);
	}
}
