package org.bigbluebutton.conference.messages.out.users;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;


public class UserVideoStatusChanged extends AbstractMessageOut {

	public final String userID;
	public final boolean hasVideo;
	public final String streamName;
	
	public UserVideoStatusChanged(String meetingID, String userID, boolean hasVideo, String streamName) {
		super(meetingID);
		this.userID = userID;
		this.hasVideo = hasVideo;
		this.streamName = streamName;
	}
}
