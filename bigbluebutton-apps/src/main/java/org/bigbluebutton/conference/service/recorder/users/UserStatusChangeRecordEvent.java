package org.bigbluebutton.conference.service.recorder.users;

public class UserStatusChangeRecordEvent extends AbstractUserRecordEvent {
	
	public UserStatusChangeRecordEvent() {
		super();
		setEvent("ParticipantStatusChangeEvent");
	}

	public void setUserId(String userId) {
		eventMap.put("userId", userId);
	}
	
	public void setStatus(String status) {
		eventMap.put("status", status);
	}
	
	public void setValue(String value) {
		eventMap.put("value", value);
	}
}
