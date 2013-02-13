package org.bigbluebutton.conference.messages;

public class RemoveConnectionMessage implements IMessage {

	private final String meetingID;
	private final String userID;
	
	public RemoveConnectionMessage(String meetingID, String userID) {
		this.meetingID = meetingID;
		this.userID = userID;
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public String getUserID() {
		return userID;
	}
}
