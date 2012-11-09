package org.bigbluebutton.conference.messages.out;

public class MeetingEnded implements IMessageOut {

	public final String meetingID;
	
	public MeetingEnded(String meetingID) {
		this.meetingID = meetingID;
	}
}
