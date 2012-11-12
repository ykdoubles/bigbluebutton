package org.bigbluebutton.conference.messages.in.users;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class UserKick extends AbstractMessageIn {

	public final String userID;
	
	public UserKick(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
