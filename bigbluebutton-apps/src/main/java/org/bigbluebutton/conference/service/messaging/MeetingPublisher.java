package org.bigbluebutton.conference.service.messaging;

import java.util.HashMap;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.MeetingEnded;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class MeetingPublisher implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingPublisher.class, "bigbluebutton");
		
	private IMessagePublisher msgPub;
	
	@Override
	public void accept(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			handleMeetingStarted((MeetingStarted) message);
		} else if (message instanceof MeetingEnded)	{
			handleMeetingEnded((MeetingEnded) message);
		}
	}
		
	private void handleMeetingEnded(MeetingEnded message) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.MEETING_ENDED_EVENT);		
		
		if (! msgPub.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map))) {
			log.warn("Failed to send message to Redis PubSub.");
		}
	}
	
	private void handleMeetingStarted(MeetingStarted message) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.MEETING_STARTED_EVENT);
				
		if (! msgPub.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map))) {
			log.warn("Failed to send message to Redis PubSub.");
		}
	}
	
	public void setMessagePublisher(IMessagePublisher msgPub){
		this.msgPub = msgPub;
	}
}
