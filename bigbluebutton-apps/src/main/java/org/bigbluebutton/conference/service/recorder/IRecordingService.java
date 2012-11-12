package org.bigbluebutton.conference.service.recorder;

public interface IRecordingService {
	public void record(String meetingID, RecordEvent message);
}
