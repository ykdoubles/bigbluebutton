package org.bigbluebutton.conference.messages.out.whiteboard;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;

public class WhiteboardAnnotationHistoryReply extends AbstractMessageOut {

	public final String presentationID;
	public final int pageNum;
	
	public WhiteboardAnnotationHistoryReply(String meetingID, String pres, int num) {
		super(meetingID);
		this.presentationID = pres;
		this.pageNum = num;
	}

}
