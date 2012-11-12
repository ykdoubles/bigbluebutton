package org.bigbluebutton.conference.messages.in.users;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;


public class UserVoiceStatusChange extends AbstractMessageIn {

	public final String userID;
	public final boolean hasVoice;
	public final String streamName;
	
	public UserVoiceStatusChange(String meetingID, String userID, boolean hasVoice, String streamName) {
		super(meetingID);
		this.userID = userID;
		this.hasVoice = hasVoice;
		this.streamName = streamName;
	}
}
