package org.bigbluebutton.conference.messages.in;

public class UserLeave implements IMessageIn {

	public final String meetingID;
	public final String userID;
	
	public UserLeave(String meetingID, String userID) {
		this.meetingID = meetingID;
		this.userID = userID;
	}
}
