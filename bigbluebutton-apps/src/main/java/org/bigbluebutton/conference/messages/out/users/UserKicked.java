package org.bigbluebutton.conference.messages.out.users;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;

public class UserKicked extends AbstractMessageOut {

	public final String userID;
	
	public UserKicked(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
