package org.bigbluebutton.conference.service.whiteboard;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.whiteboard.WhiteboardAnnotationSent;

public class WhiteboardConnectionInvoker {
	
	IConnectionInvokerService connInvokerService;
	
	public void setConnInvokerService(IConnectionInvokerService connInvokerService){
		if(connInvokerService==null)
			throw new IllegalArgumentException();
		this.connInvokerService = connInvokerService;
	}

	public void accept(IMessageOut was) {
		if(was == null){
			throw new IllegalArgumentException();
		}
		
		if(was instanceof WhiteboardAnnotationSent){
			handleWhiteboardAnnotationSent((WhiteboardAnnotationSent) was);
		}
	}

	private void handleWhiteboardAnnotationSent(WhiteboardAnnotationSent was) {
		Map<String,Object> message = new HashMap<String, Object>();
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, was.meetingID, "PresentationSharedCommand", message);
		connInvokerService.sendMessage(cm);
	}
}
