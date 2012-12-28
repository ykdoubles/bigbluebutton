package org.bigbluebutton.conference.messages.out.presentation;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;

public class PresentationSlideChanged extends AbstractMessageOut{
	public final int slideNum;
	
	public PresentationSlideChanged(String meetingID, int slideNum) {
		super(meetingID);
		this.slideNum = slideNum;
	}
}
