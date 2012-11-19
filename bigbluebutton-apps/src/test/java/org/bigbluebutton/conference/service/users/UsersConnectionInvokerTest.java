package org.bigbluebutton.conference.service.users;

import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.Role;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.vo.UserVO;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class UsersConnectionInvokerTest {
	//private IMessageOut messageOut;
	UsersConnectionInvoker usersConnectionInvoker;
	IConnectionInvokerService connInvokerService;
	String meetingID = "1234567890";
	
	
	@BeforeTest
	public void init(){
		usersConnectionInvoker = new UsersConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		usersConnectionInvoker.setConnInvokerService(connInvokerService);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void accept_IllegalArgumentException() {
		usersConnectionInvoker.accept(null);
	}
	
	@Test(enabled=false)
	public void handleMeetingEnded() {
		throw new RuntimeException("Test not implemented");
	}
	  
	@Test
	public void accept_UserJoined() {
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
	public void handleUserLeft() {
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
	public void handleUserKicked() {
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
	
	  @Test(enabled=false)
	  public void handleUserPresenterChanged() {
	    throw new RuntimeException("Test not implemented");
	  }
	
	  @Test(enabled=false)
	  public void handleUsersQueryReply() {
	    throw new RuntimeException("Test not implemented");
	  }
	
	  @Test(enabled=false)
	  public void setConnInvokerService() {
	    throw new RuntimeException("Test not implemented");
	  }
}
