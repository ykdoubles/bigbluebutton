package org.bigbluebutton.conference.messages.out.presentation;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;

public class PresentationShared extends AbstractMessageOut{
	
	public final String presentationName;
	public final Boolean share;
	
	public PresentationShared(String meetingID, String presentationName, Boolean share) {
		super(meetingID);
		this.presentationName = presentationName;
		this.share = share;
	}
}
