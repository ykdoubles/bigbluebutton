package org.bigbluebutton.conference.messages.out;

public class UserKicked extends AbstractMessageOut {

	public final String userID;
	
	public UserKicked(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
