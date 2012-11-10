package org.bigbluebutton.conference.messages.in;

public class UsersQuery extends AbstractMessageIn {

	public final String userID;
	
	public UsersQuery(String meetingID, String userID) {
		super(meetingID);
		this.userID = userID;
	}
}
