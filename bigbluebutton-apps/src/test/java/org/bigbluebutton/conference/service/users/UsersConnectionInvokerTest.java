package org.bigbluebutton.conference.service.users;

import static org.easymock.EasyMock.createMock;

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
import org.testng.annotations.Test;


public class UsersConnectionInvokerTest {
	//private IMessageOut messageOut;
	IConnectionInvokerService connInvokerService;
	UserJoined msg;

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void accept_IllegalArgumentException() {
		new UsersConnectionInvoker().accept(null);
	}
	
	@Test(enabled=false)
	public void handleMeetingEnded() {
		throw new RuntimeException("Test not implemented");
	}
	  
	@BeforeMethod
	public void setupMocks(){
		connInvokerService = createMock(IConnectionInvokerService.class);
		UserVO user = new UserVO("1111", "ext1111", Role.VIEWER, "John Doe");
		
		msg = new UserJoined("1234567890", user);
	}
	  
	@Test
	public void shouldBeValidUserJoined() {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		UserVO.toMap(msg.user, message);
		
		Set<String> actualKeys = message.keySet();
		Set<String> expectedKeys = new HashSet<String>();
		expectedKeys.add("meetingID");
		expectedKeys.add("intUserID");
		expectedKeys.add("extUserID");
		expectedKeys.add("name");
		expectedKeys.add("role");
		expectedKeys.add("presenter");
		expectedKeys.add("hasHandRaised");
		expectedKeys.add("hasVideo");
		expectedKeys.add("hasAudio");
		expectedKeys.add("audioStreamName");
		expectedKeys.add("videoStreamName");
		
		Assert.assertEquals(actualKeys,expectedKeys);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "UserJoinedCommand", message);
		connInvokerService.sendMessage(m);
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
