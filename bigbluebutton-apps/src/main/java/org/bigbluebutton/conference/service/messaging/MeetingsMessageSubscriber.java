package org.bigbluebutton.conference.service.messaging;

import org.bigbluebutton.conference.BigBlueButton;
import org.bigbluebutton.conference.messages.in.MeetingForceEnd;

public class MeetingsMessageSubscriber implements IMessageSubscriber {

	private BigBlueButton gw;
	
	@Override
	public void receive(RedisMessage rm) {
		if (rm.channel.equalsIgnoreCase(MessagingConstants.SYSTEM_CHANNEL)){
			String meetingId = rm.message.get("meetingId");
			String messageId = rm.message.get("messageId");
			
			if (messageId != null) {
				if (MessagingConstants.END_MEETING_REQUEST_EVENT.equalsIgnoreCase(messageId)){
						gw.accept(new MeetingForceEnd(meetingId));
				}
			}
		}
	}

	public void setBigBlueButton(BigBlueButton gw) {
		this.gw = gw;
	}
}
