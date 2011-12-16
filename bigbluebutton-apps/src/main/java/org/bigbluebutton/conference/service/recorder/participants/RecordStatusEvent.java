package org.bigbluebutton.conference.service.recorder.participants;

public class RecordStatusEvent extends AbstractParticipantRecordEvent {
	
	public RecordStatusEvent() {
		super();
		setEvent("RecordStatusEvent");
	}
	
	public void setIsRecording(Boolean isRecording){
		eventMap.put("isRecording", isRecording.toString());
	}
}
