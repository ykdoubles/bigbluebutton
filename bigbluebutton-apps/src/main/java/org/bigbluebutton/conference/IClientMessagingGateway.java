package org.bigbluebutton.conference;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

public interface IClientMessagingGateway {
	public void addMeetingScope(MeetingScope meeting);
	public void addUserConnection(String meetingID, IConnection conn, String userID);	
	void addScope(String meetingID, IScope scope)
}
