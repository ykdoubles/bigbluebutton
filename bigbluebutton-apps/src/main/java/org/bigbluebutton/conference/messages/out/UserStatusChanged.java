package org.bigbluebutton.conference.messages.out;

public class UserStatusChanged implements IMessageOut {

	public final String meetingID;
	public final String userID;
	public final String statusName;
	public final Object statusValue;
	
	public UserStatusChanged(String meetingID, String userID, String statusName, Object statusValue) {
		this.meetingID = meetingID;
		this.userID = userID;
		this.statusName = statusName;
		this.statusValue = statusValue;
	}
}
