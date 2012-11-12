package org.bigbluebutton.conference.messages.in.users;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;


public class UserHandStatusChange extends AbstractMessageIn {

	public final String userID;
	public final boolean raised;
	public final String setByUserID;
	
	public UserHandStatusChange(String meetingID, String userID, boolean raised, String setByUserID) {
		super(meetingID);
		this.userID = userID;
		this.raised = raised;
		this.setByUserID = setByUserID;
	}
}
