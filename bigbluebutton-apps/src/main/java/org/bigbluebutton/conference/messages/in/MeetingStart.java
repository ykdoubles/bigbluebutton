package org.bigbluebutton.conference.messages.in;

public class MeetingStart implements IMessageIn {

	public final String meetingID;
	
	public MeetingStart(String meetingID) {
		this.meetingID = meetingID;
	}
}
