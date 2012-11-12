package org.bigbluebutton.conference.service.recorder.users;

public class UserLeftRecordEvent extends AbstractUserRecordEvent {
	
	public UserLeftRecordEvent() {
		super();
		setEvent("ParticipantLeftEvent");
	}
	
	public void setUserId(String userId) {
		eventMap.put("userId", userId);
	}
	
}
