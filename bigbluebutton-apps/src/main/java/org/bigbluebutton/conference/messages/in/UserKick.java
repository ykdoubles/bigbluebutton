package org.bigbluebutton.conference.messages.in;

public class UserKick extends AbstractMessageIn {

	public final String userID;
	
	public UserKick(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
