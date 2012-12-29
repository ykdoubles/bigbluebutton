package org.bigbluebutton.conference.service.presentation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;

import org.bigbluebutton.conference.RedisMessageMatcher;
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;
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
	
	@Test
	public void Accept_WhenPresentationShared_ShouldPublishMessage(){
		reset(publisher);
		
		PresentationShared msg = new PresentationShared(meetingID,presentationName,true);
		
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", this.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_SHARED);
		map.put("presentationName", this.presentationName);
		map.put("share", new Boolean(true).toString());
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		presentationMessagePublisher.accept(msg);
		verify(publisher);
	}
	
	@Test
	public void Accept_WhenPresentationRemoved_ShouldPublishMessage(){
		reset(publisher);
		
		PresentationRemoved msg = new PresentationRemoved(meetingID,presentationName);
		
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", this.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_REMOVED);
		map.put("presentationName", this.presentationName);
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		presentationMessagePublisher.accept(msg);
		verify(publisher);
	}
	
	@Test
	public void Accept_WhenPresentationSlideChange_ShouldPublishMessage(){
		reset(publisher);
		
		PresentationSlideChanged msg = new PresentationSlideChanged(meetingID,1);
		
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", this.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_SLIDE_CHANGED);
		map.put("slideNum", new Integer(1).toString());
		
		expect(publisher.send(RedisMessageMatcher.eqRedisMessage( new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map)))).andReturn(true);
		
		replay(publisher);
		
		presentationMessagePublisher.accept(msg);
		verify(publisher);
	}
}
