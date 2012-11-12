package org.bigbluebutton.conference.service.recorder.users;

public class AssignPresenterRecordEvent extends AbstractUserRecordEvent {

	public AssignPresenterRecordEvent() {
		super();
		setEvent("AssignPresenterEvent");
	}
		
	public void setUserId(String userid) {
		eventMap.put("userid", userid);
	}
	
	public void setName(String name) {
		eventMap.put("name", name);
	}
	
	public void setAssignedBy(String by) {
		eventMap.put("assignedBy", by);
	}
}
