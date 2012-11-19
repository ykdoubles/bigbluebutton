package org.bigbluebutton.conference.service.users;

import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.Role;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.vo.UserVO;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class UsersConnectionInvokerTest {
	//private IMessageOut messageOut;
	UsersConnectionInvoker usersConnectionInvoker;
	UserJoined userJoinedMsg;
	
	@BeforeTest
	public void init(){
		usersConnectionInvoker = new UsersConnectionInvoker();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void accept_IllegalArgumentException() {
		usersConnectionInvoker.accept(null);
	}
	
	@Test(enabled=false)
	public void handleMeetingEnded() {
		throw new RuntimeException("Test not implemented");
	}
	  
	@BeforeMethod
	public void setupJoinedTestEnvironment(){
		String meetingID = "1234567890";
		UserVO user = new UserVO("1111", "ext1111", Role.VIEWER, "John Doe");
		userJoinedMsg = new UserJoined(meetingID, user);
	}
	  
	@Test
	public void accept_UserJoined() {
		IConnectionInvokerService connInvokerService = createMock(IConnectionInvokerService.class);
		usersConnectionInvoker.setConnInvokerService(connInvokerService);
		
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", userJoinedMsg.meetingID);
		UserVO.toMap(userJoinedMsg.user, message);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, userJoinedMsg.meetingID, "UserJoinedCommand", message);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(m));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(userJoinedMsg);
		verify(connInvokerService);
		
	}
	
	  @Test(enabled=false)
	  public void handleUserKicked() {
	    throw new RuntimeException("Test not implemented");
	  }
	
	  @Test(enabled=false)
	  public void handleUserLeft() {
	    throw new RuntimeException("Test not implemented");
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
