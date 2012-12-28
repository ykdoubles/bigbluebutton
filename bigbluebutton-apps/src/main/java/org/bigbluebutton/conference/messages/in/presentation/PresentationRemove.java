package org.bigbluebutton.conference.messages.in.presentation;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class PresentationRemove extends AbstractMessageIn {
	
	public final String presentationName;
	
	public PresentationRemove(String meetingID, String presentationName) {
		super(meetingID);
		this.presentationName = presentationName;
	}

}