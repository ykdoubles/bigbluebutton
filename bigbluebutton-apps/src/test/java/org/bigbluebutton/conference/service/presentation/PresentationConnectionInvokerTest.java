package org.bigbluebutton.conference.service.presentation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ClientMessageMatcher;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class PresentationConnectionInvokerTest {

	PresentationConnectionInvoker presConnectionInvoker;
	IConnectionInvokerService connInvokerService;
	String meetingID = "0123456789";
	String presentationName = "Default";
	
	@BeforeTest
	public void init(){
		presConnectionInvoker = new PresentationConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		presConnectionInvoker.setConnInvokerService(connInvokerService);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void setConnInvokerService_WhenParamIsNull_ShouldThrowException(){
		presConnectionInvoker.setConnInvokerService(null);
	}
	
	@Test
	public void Accept_WhenPresentationShared_ShouldSendMessage() {
		reset(connInvokerService);
		
		PresentationShared ps = new PresentationShared(meetingID, presentationName, true);
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", ps.meetingID);
		msg.put("presentationName", ps.presentationName);
		msg.put("share", ps.share);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, ps.meetingID, "PresentationSharedCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		
		replay(connInvokerService);
		
		presConnectionInvoker.accept(ps);
		verify(connInvokerService);
	}
	
	@Test
	public void Accept_WhenPresentationRemoved_ShouldSendMessage() {
		reset(connInvokerService);
		
		PresentationRemoved pr = new PresentationRemoved(this.meetingID, this.presentationName);
		
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", pr.meetingID);
		msg.put("presentationName", pr.presentationName);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, pr.meetingID, "PresentationRemovedCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		presConnectionInvoker.accept(pr);
		verify(connInvokerService);	
	}
	
	@Test
	public void Accept_WhenPresentationSlideChanged_ShouldSendMessage() {
		reset(connInvokerService);
		
		PresentationSlideChanged psc = new PresentationSlideChanged(this.meetingID, 1);
		
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", psc.meetingID);
		msg.put("slideNum", psc.slideNum);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, psc.meetingID, "PresentationSlideChangedCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		presConnectionInvoker.accept(psc);
		verify(connInvokerService);	
	}
}
