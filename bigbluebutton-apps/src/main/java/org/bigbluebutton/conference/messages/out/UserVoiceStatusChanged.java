package org.bigbluebutton.conference.messages.out;


public class UserVoiceStatusChanged extends AbstractMessageOut {

	public final String userID;
	public final boolean hasVoice;
	public final String streamName;
	
	public UserVoiceStatusChanged(String meetingID, String userID, boolean hasVoice, String streamName) {
		super(meetingID);
		this.userID = userID;
		this.hasVoice = hasVoice;
		this.streamName = streamName;
	}
}
