package org.bigbluebutton.conference.service.users;

import static org.easymock.EasyMock.*;
import java.util.HashMap;

import org.bigbluebutton.conference.Role;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.bigbluebutton.conference.vo.UserVO;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class UsersMessagePublisherTest {
	private UsersMessagePublisher usersMessagePublisher;
	private IMessagePublisher publisher; 
	final String meetingID = "0123456789";
	
	@BeforeTest
	public void init(){
		usersMessagePublisher = new UsersMessagePublisher();
		publisher = createMock(IMessagePublisher.class);
		usersMessagePublisher.setMessagePublisher(publisher);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void SetMessagePublisher_WhenParamIsNull_ShouldThrowException(){
		usersMessagePublisher.setMessagePublisher(null);
	}
	
	@Test
	public void Accept_WhenUserJoined_ShouldPublishMessage(){
		reset(publisher);
		
		UserJoined msg = new UserJoined(meetingID, new UserVO("1111", "ext1111", Role.VIEWER, "Jhon Doe"));
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
		map.put("internalUserId", msg.user.intUserID);
		map.put("externalUserId", msg.user.extUserID);
		map.put("fullname", msg.user.name);
		map.put("role", msg.user.role);
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.PARTICIPANTS_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		usersMessagePublisher.accept(msg);
		verify(publisher);
	}
	
	@Test
	public void Accept_WhenUserLeft_ShouldPublishMessage(){
		reset(publisher);
		
		UserLeft msg = new UserLeft(meetingID, "1111");
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_LEFT_EVENT);
		map.put("internalUserId", msg.userID);
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.PARTICIPANTS_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		usersMessagePublisher.accept(msg);
		verify(publisher);
	}
	
}
