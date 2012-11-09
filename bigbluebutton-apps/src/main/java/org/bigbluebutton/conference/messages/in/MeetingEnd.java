package org.bigbluebutton.conference.messages.in;

public class MeetingEnd implements IMessageIn {

	public final String meetingID;
	
	public MeetingEnd(String meetingID) {
		this.meetingID = meetingID;
	}
}
