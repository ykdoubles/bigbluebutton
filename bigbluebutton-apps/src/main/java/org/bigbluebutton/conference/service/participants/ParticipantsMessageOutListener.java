package org.bigbluebutton.conference.service.participants;

import java.util.HashMap;
import java.util.Map;
import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.MeetingEnded;
import org.bigbluebutton.conference.messages.out.UserStatusChanged;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ParticipantsMessageOutListener implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(ParticipantsMessageOutListener.class, "bigbluebutton");
	
	private ConnectionInvokerService connInvokerService;
	
	@Override
	public void accept(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			
		} else if (message instanceof MeetingEnded) {
			handleMeetingStopped((MeetingEnded) message);
		}
	}

	private void handleUserStatusChanged(UserStatusChanged message) {
		Map<String, Object> msg = new HashMap<String, Object>();	
		message.put("userID", userid);
		message.put("statusName", status);
		message.put("statusValue", value);
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "UserStatusChangeCommand", message);
		connInvokerService.sendMessage(m);	
	}
	
	private void handleMeetingStopped(MeetingEnded m) {
		log.debug("Meeting stopped message [" + m.meetingID + "]");		
		Map<String, Object> msg = new HashMap<String, Object>();	
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, m.meetingID, "UserLogoutCommand", msg);
		connInvokerService.sendMessage(cm);		
	}
	
	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}
}
