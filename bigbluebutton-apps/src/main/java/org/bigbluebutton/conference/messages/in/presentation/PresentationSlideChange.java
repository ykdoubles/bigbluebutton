package org.bigbluebutton.conference.messages.in.presentation;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class PresentationSlideChange extends AbstractMessageIn {
	
	public final int slideNum;
	
	public PresentationSlideChange(String meetingID, int slideNum) {
		super(meetingID);
		this.slideNum = slideNum;
	}

}