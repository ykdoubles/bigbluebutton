package org.bigbluebutton.conference.service.recorder.users;

public class UserJoinRecordEvent extends AbstractUserRecordEvent {

	public UserJoinRecordEvent() {
		super();
		setEvent("ParticipantJoinEvent");
	}
		
	public void setUserId(String userId) {
		eventMap.put("userId", userId);
	}
	
	public void setName(String name){
		eventMap.put("name",name);
	}
	
	/**
	 * Sets the role of the user as MODERATOR or VIEWER
	 * @param role
	 */
	public void setRole(String role) {
		eventMap.put("role", role);
	}
	
	public void setStatus(String status) {
		eventMap.put("status", status);
	}
}
