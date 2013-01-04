package org.bigbluebutton.conference.messages.in.whiteboard;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation;

public class WhiteboardAnnotationSend extends AbstractMessageIn {

	public final Annotation annotation;
	
	public WhiteboardAnnotationSend(String meetingID, Annotation an) {
		super(meetingID);
		this.annotation = an;
	}

}
