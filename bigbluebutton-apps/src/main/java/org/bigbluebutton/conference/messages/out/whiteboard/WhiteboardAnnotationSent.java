package org.bigbluebutton.conference.messages.out.whiteboard;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation;

public class WhiteboardAnnotationSent extends AbstractMessageOut {

	public final Annotation annotation;
	
	public WhiteboardAnnotationSent(String meetingID, Annotation an) {
		super(meetingID);
		this.annotation = an;
	}

}
