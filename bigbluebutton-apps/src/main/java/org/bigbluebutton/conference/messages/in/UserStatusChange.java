package org.bigbluebutton.conference.messages.in;


public class UserStatusChange extends AbstractMessageIn {

	public final String userID;
	public final String statusName;
	public final Object statusValue;
	
	public UserStatusChange(String meetingID, String userID, String statusName, Object statusValue) {
		super(meetingID);
		this.userID = userID;
		this.statusName = statusName;
		this.statusValue = statusValue;
	}
}
