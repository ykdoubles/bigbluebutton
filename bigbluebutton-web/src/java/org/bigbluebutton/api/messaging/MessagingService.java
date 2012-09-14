package org.bigbluebutton.api.messaging;

import java.util.Map;

public interface MessagingService {	
	public void start();
	public void stop();
	public void recordMeeting(String meetingId, Map<String, String> meeting, Map<String,String> metadata);
	public void removeMeeting(String meetingId);
	public void endMeeting(String meetingId);
	public void send(String channel, String message);
	public void addListener(MessageListener listener);
	public void removeListener(MessageListener listener);
	public long generateInternalUserID();
	public void recordUserSession(String meetingID, String internalUserID, Map<String, String> properties);
}
