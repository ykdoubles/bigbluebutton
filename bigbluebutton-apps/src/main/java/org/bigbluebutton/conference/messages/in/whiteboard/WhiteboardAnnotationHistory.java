package org.bigbluebutton.conference.messages.in.whiteboard;

import java.util.Map;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class WhiteboardAnnotationHistory extends AbstractMessageIn{
	public final Map<String, Object> message;
	
	public WhiteboardAnnotationHistory(String meetingID, Map<String,Object> msg) {
		super(meetingID);
		this.message = msg;
	}

}
