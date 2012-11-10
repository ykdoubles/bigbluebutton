package org.bigbluebutton.conference.messages.out;

public abstract class AbstractMessageOut implements IMessageOut {
	public final String meetingID;
	
	public AbstractMessageOut(String meetingID) {
		this.meetingID = meetingID;
	}
}
