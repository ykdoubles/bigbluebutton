package org.bigbluebutton.conference.messages.in.users;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;


public class UserVideoStatusChange extends AbstractMessageIn {

	public final String userID;
	public final boolean hasVideo;
	public final String streamName;
	
	public UserVideoStatusChange(String meetingID, String userID, boolean hasVideo, String streamName) {
		super(meetingID);
		this.userID = userID;
		this.hasVideo = hasVideo;
		this.streamName = streamName;
	}
}
