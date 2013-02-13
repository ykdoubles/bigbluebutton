package org.bigbluebutton.conference.messages;

import org.bigbluebutton.conference.MeetingScope;

public class AddMeetingScope implements IMessage {

	private final MeetingScope meeting;

	
	public AddMeetingScope(MeetingScope meeting) {
		this.meeting = meeting;
	}
	
	public MeetingScope getMeeting() {
		return meeting;
	}
}
