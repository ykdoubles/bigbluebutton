package org.bigbluebutton.conference.service.presentation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import org.bigbluebutton.conference.RedisMessageMatcher;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PresentationMessagePublisherTest {
	private PresentationMessagePublisher presentationMessagePublisher;
	private IMessagePublisher publisher;
	final String meetingID = "0123456789";
	final String presentationName = "Default";
	
	@BeforeTest
	public void init(){
		presentationMessagePublisher = new PresentationMessagePublisher();
		publisher = createMock(IMessagePublisher.class);
		presentationMessagePublisher.setMessagePublisher(publisher);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void SetMessagePublisher_WhenParamIsNull_ShouldThrowException(){
		presentationMessagePublisher.setMessagePublisher(null);
	}
	
	@Test(enabled=false)
	public void Accept_WhenUserJoined_ShouldPublishMessage(){
		/*reset(publisher);
		
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
		verify(publisher);*/
	}
}
