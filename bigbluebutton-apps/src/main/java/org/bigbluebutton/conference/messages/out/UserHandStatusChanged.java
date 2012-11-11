package org.bigbluebutton.conference.messages.out;

public class UserHandStatusChanged extends AbstractMessageOut {
	
	public final String userID;
	public final boolean raised;
	public final String setByUserID;
	
	public UserHandStatusChanged(String meetingID, String userID, boolean raised, String setByUserID) {
		super(meetingID);
		this.userID = userID;
		this.raised = raised;
		this.setByUserID = setByUserID;
	}
}
