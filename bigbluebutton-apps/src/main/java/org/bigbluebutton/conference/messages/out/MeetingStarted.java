package org.bigbluebutton.conference.messages.out;

public class MeetingStarted implements IMessageOut {

	public final String meetingID;
	
	public MeetingStarted(String meetingID) {
		this.meetingID = meetingID;
	}
}
