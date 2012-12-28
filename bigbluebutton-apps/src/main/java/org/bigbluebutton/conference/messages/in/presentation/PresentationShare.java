package org.bigbluebutton.conference.messages.in.presentation;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class PresentationShare extends AbstractMessageIn {
	
	public final String presentationName;
	public final Boolean share;
	
	public PresentationShare(String meetingID, String presentationName, Boolean share) {
		super(meetingID);
		this.presentationName = presentationName;
		this.share = share;
	}

}
