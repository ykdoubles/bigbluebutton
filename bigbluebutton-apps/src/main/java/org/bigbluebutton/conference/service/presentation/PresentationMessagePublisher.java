package org.bigbluebutton.conference.service.presentation;

import java.util.HashMap;

import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PresentationMessagePublisher implements IMessageOutListener {

private static Logger log = Red5LoggerFactory.getLogger(PresentationMessagePublisher.class, "bigbluebutton");
	
	private IMessagePublisher publisher;
	
	@Override
	public void accept(IMessageOut message) {
		if(message == null){
			throw new IllegalArgumentException();
		}
		
		if(message instanceof PresentationShared){
			handlePresentationShared((PresentationShared)message);
		}else if(message instanceof PresentationRemoved){
			handlePresentationRemoved((PresentationRemoved)message);
		}else if(message instanceof PresentationSlideChanged){
			handlePresentationSlideChanged((PresentationSlideChanged) message);
		}
	}
	
	private void handlePresentationSlideChanged(PresentationSlideChanged message) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_SLIDE_CHANGED);
		map.put("slideNum", new Integer(message.slideNum).toString());
		publisher.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map));
	}

	private void handlePresentationRemoved(PresentationRemoved message) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_REMOVED);
		map.put("presentationName", message.presentationName);
		publisher.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map));
	}

	private void handlePresentationShared(PresentationShared message) {
		HashMap<String,String> map = new HashMap<String, String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.PRESENTATION_SHARED);
		map.put("presentationName", message.presentationName);
		map.put("share", message.share.toString());
		publisher.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map));
	}

	public void setMessagePublisher(IMessagePublisher pub) {
		if( pub == null )
			throw new IllegalArgumentException();
		publisher = pub;
	}
}
