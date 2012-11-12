package org.bigbluebutton.conference.service.recorder.users;

import org.bigbluebutton.conference.service.recorder.RecordEvent;

public abstract class AbstractUserRecordEvent extends RecordEvent {
	
	public AbstractUserRecordEvent() {
		setModule("PARTICIPANT");
	}
}
