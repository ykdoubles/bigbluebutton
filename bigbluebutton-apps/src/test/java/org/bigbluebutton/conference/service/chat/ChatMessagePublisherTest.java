package org.bigbluebutton.conference.service.chat;

import static org.easymock.EasyMock.*;

import java.util.HashMap;

import org.bigbluebutton.conference.RedisMessageMatcher;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ChatMessagePublisherTest {
	
	private ChatMessagePublisher chatMessagePublisher;
	private IMessagePublisher publisher;
	final String meetingID = "0123456789";
	ChatMessageVO testChatMsg;
	
	@BeforeTest
	public void init(){
		chatMessagePublisher = new ChatMessagePublisher();
		publisher = createMock(IMessagePublisher.class);
		chatMessagePublisher.setMessagePublisher(publisher);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void SetMessagePublisher_WhenParamIsNull_ShouldThrowException(){
		chatMessagePublisher.setMessagePublisher(null);
	}
	
	@Test
	public void Accept_WhenUserJoined_ShouldPublishMessage(){
		reset(publisher);
		
		testChatMsg =  new ChatMessageVO();
		testChatMsg.chatType = ChatMessageVO.PUBLIC_TYPE; 
		testChatMsg.fromUserID = "1111";
		testChatMsg.fromUsername = "John Doe";
		testChatMsg.fromColor = "0";
		testChatMsg.fromTime = Double.valueOf(System.currentTimeMillis());   
		testChatMsg.fromTimezoneOffset = Long.valueOf(0);
		testChatMsg.fromLang = "en"; 	 
		testChatMsg.message = "This is a test message";
		
		PublicChatMessageSent msg = new PublicChatMessageSent(meetingID,testChatMsg);
		
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.PUBLIC_CHAT_MESSAGE_SENT_EVENT);
		map.put("chatType", msg.chatVO.chatType);
		map.put("fromUserID", msg.chatVO.fromUserID);
		map.put("fromUsername", msg.chatVO.fromUsername);
		map.put("fromColor", msg.chatVO.fromColor);
		map.put("fromTime", Double.toString(msg.chatVO.fromTime));
		map.put("fromTimezoneOffset", Long.toString(msg.chatVO.fromTimezoneOffset));
		map.put("fromLang", msg.chatVO.fromLang);
		map.put("message", msg.chatVO.message);
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		chatMessagePublisher.accept(msg);
		verify(publisher);
	}
	
	/*private UsersMessagePublisher usersMessagePublisher;
	private IMessagePublisher publisher; 
	final String meetingID = "0123456789";
	
	private UserVO testUser;
	
	@BeforeTest
	public void init(){
		usersMessagePublisher = new UsersMessagePublisher();
		publisher = createMock(IMessagePublisher.class);
		usersMessagePublisher.setMessagePublisher(publisher);
		
		testUser = new UserVO("1111", "ext1111", Role.VIEWER, "Jhon Doe");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void SetMessagePublisher_WhenParamIsNull_ShouldThrowException(){
		usersMessagePublisher.setMessagePublisher(null);
	}
	
	@Test
	public void Accept_WhenUserJoined_ShouldPublishMessage(){
		reset(publisher);
		
		UserJoined msg = new UserJoined(meetingID, testUser);
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
		
		UserLeft msg = new UserLeft(meetingID, this.testUser.intUserID);
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_LEFT_EVENT);
		map.put("internalUserId", msg.userID);
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.PARTICIPANTS_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		usersMessagePublisher.accept(msg);
		verify(publisher);
	}*/

}
