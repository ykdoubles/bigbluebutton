package org.bigbluebutton.conference.service.messaging;

import java.util.HashMap;
import java.util.Map;

public interface IMessageSubscriber {
	void endMeetingRequest(String meetingId);
	void presentationUpdates(HashMap<String,String> map);
	
	void receive(Map<String, String> message);
}
