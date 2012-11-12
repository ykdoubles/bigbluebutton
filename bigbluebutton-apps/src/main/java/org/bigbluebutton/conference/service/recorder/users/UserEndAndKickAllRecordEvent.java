package org.bigbluebutton.conference.service.recorder.users;

public class UserEndAndKickAllRecordEvent extends AbstractUserRecordEvent {

	public UserEndAndKickAllRecordEvent() {
		super();
		setEvent("EndAndKickAllEvent");
	}
}
