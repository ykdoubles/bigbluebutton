package org.bigbluebutton.conference;

public interface IBigBlueButtonGateway {
	void createMeeting(String meetingID, String meetingName, String voiceBridge, Boolean recorded);
	void endMeeting(String meetingID);
	void joinUser(String meetingID, String userID, String username, String role, String externUserID, Boolean raiseHand, Boolean presenter, Boolean hasStream);
}
