package org.bigbluebutton.conference.service.presentation;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;

import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;

public class PresentationConnectionInvoker implements IMessageOutListener{
	IConnectionInvokerService connInvokerService;
	
	public void setConnInvokerService(IConnectionInvokerService connInvokerService){
		if(connInvokerService==null)
			throw new IllegalArgumentException();
		this.connInvokerService = connInvokerService;
	}
	
	@Override
	public void accept(IMessageOut msg) {
		if(msg == null)
			throw new IllegalArgumentException();
		
		if(msg instanceof PresentationShared){
			handlePresentationShared((PresentationShared) msg);
		}else if(msg instanceof PresentationRemoved){
			handlePresentationRemoved((PresentationRemoved) msg);
		}else if(msg instanceof PresentationSlideChanged){
			handlePresentationSlideChanged((PresentationSlideChanged) msg);
		}
	}

	private void handlePresentationShared(PresentationShared msg) {
		Map<String,Object> message = new HashMap<String, Object>();
		message.put("meetingID", msg.meetingID);
		message.put("presentationName", msg.presentationName);
		message.put("share", msg.share);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "PresentationSharedCommand", message);
		connInvokerService.sendMessage(cm);
	}
	
	private void handlePresentationRemoved(PresentationRemoved msg) {
		Map<String,Object> message = new HashMap<String, Object>();
		message.put("meetingID", msg.meetingID);
		message.put("presentationName", msg.presentationName);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "PresentationRemovedCommand", message);
		connInvokerService.sendMessage(cm);
	}
	
	private void handlePresentationSlideChanged(PresentationSlideChanged msg) {
		Map<String,Object> message = new HashMap<String, Object>();
		message.put("meetingID", msg.meetingID);
		message.put("slideNum", msg.slideNum);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "PresentationSlideChangedCommand", message);
		connInvokerService.sendMessage(cm);
	}
	
}
