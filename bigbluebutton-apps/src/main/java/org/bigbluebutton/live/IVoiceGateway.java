package org.bigbluebutton.live;

public interface IVoiceGateway {
	void getVoiceUsers(String voiceMeetingID);
	void mute(String voiceMeetingID, int voiceUserID, boolean mute);	
	void eject(String voiceMeetingID, int voiceUserID);
	void record(String voiceMeetingID, String meetingID);
	void broadcast(String voiceMeetingID, String meetingID);
}
