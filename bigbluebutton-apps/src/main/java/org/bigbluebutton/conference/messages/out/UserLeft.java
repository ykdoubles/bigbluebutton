package org.bigbluebutton.conference.messages.out;

public class UserLeft extends AbstractMessageOut {

	public final String userID;
	
	public UserLeft(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
