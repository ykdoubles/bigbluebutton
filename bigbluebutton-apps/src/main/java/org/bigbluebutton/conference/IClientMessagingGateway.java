package org.bigbluebutton.conference;

import org.red5.server.api.IConnection;

public interface IClientMessagingGateway {
	public void addMeetingScope(MeetingScope meeting);
	public void addUserConnection(String meetingID, IConnection conn, String userID);	
}
