package org.bigbluebutton.conference.messages.in;

public abstract class AbstractMessageIn implements IMessageIn {

	public final String meetingID;
	
	public AbstractMessageIn(String meetingID) {
		this.meetingID = meetingID;
	}
}
