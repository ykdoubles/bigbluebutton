package org.bigbluebutton.conference.messages;

public class RemoveMeetingScope implements IMessage {

	private final String meetingID;
	
	public RemoveMeetingScope(String meetingID) {
		this.meetingID = meetingID;
	}
	
	public String getMeetingID() {
		return meetingID;
	}
}
