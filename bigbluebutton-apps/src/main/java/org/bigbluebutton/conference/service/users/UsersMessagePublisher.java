package org.bigbluebutton.conference.service.users;

import java.util.HashMap;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class UsersMessagePublisher implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(UsersMessagePublisher.class, "bigbluebutton");
	
	private IMessagePublisher publisher;
	
	@Override
	public void accept(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			
		} else if (message instanceof UserJoined) {
			handleUserJoined((UserJoined) message);
		} else if (message instanceof UserLeft) {
			handleUserLeft((UserLeft) message);
		}
	}

	private void handleUserJoined(UserJoined msg) {
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
		map.put("internalUserId", msg.user.intUserID);
		map.put("externalUserId", msg.user.extUserID);
		map.put("fullname", msg.user.name);
		map.put("role", msg.user.role);
		
		if (! publisher.send(new RedisMessage(MessagingConstants.PARTICIPANTS_CHANNEL, map))) {
			log.error("Failed to send to [{}] message [{}]", MessagingConstants.PARTICIPANTS_CHANNEL, MessagingConstants.USER_JOINED_EVENT);
		}
	}
	
	private void handleUserLeft(UserLeft msg) {
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_LEFT_EVENT);
		map.put("internalUserId", msg.userID);
		
		if (! publisher.send(new RedisMessage(MessagingConstants.PARTICIPANTS_CHANNEL, map))) {
			log.error("Failed to send to [{}] message [{}]", MessagingConstants.PARTICIPANTS_CHANNEL, MessagingConstants.USER_JOINED_EVENT);
		}
	}
		
	public void setMessagePublisher(IMessagePublisher pub) {
		publisher = pub;
	}
}
