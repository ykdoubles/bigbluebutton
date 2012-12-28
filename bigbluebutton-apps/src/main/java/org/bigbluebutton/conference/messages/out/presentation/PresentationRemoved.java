package org.bigbluebutton.conference.messages.out.presentation;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;

public class PresentationRemoved extends AbstractMessageOut{
	
	public final String presentationName;
	
	public PresentationRemoved(String meetingID, String presentationName) {
		super(meetingID);
		this.presentationName = presentationName;
	}
}

