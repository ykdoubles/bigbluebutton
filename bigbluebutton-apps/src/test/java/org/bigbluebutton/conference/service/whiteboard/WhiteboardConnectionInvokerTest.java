package org.bigbluebutton.conference.service.whiteboard;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ClientMessageMatcher;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.whiteboard.WhiteboardAnnotationHistoryReply;
import org.bigbluebutton.conference.messages.out.whiteboard.WhiteboardAnnotationSent;
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class WhiteboardConnectionInvokerTest {
	
	WhiteboardConnectionInvoker whiteboardInvoker;
	IConnectionInvokerService connInvokerService;
	String meetingID = "0123456789";
	String presentationName = "Default";
	
	@BeforeTest
	public void init(){
		whiteboardInvoker = new WhiteboardConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		whiteboardInvoker.setConnInvokerService(connInvokerService);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void setConnInvokerService_WhenParamIsNull_ShouldThrowException(){
		whiteboardInvoker.setConnInvokerService(null);
	}
	@Test
	public void Accept_WhenWhiteboardAnnotationSent_ShouldSendMessage() {
		reset(connInvokerService);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		Annotation an = new Annotation(map);
		
		WhiteboardAnnotationSent was = new WhiteboardAnnotationSent(meetingID, an);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, meetingID, "PresentationSharedCommand", map);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		whiteboardInvoker.accept(was);
		verify(connInvokerService);
	}
	
	@Test(enabled=false)
	public void Accept_WhenWhiteboardAnnotationHistoryReply_ShouldSendMessage() {
		reset(connInvokerService);
		
		HashMap<String,Object> map = new HashMap<String, Object>();
		Annotation an = new Annotation(map);
		
		WhiteboardAnnotationHistoryReply was = new WhiteboardAnnotationHistoryReply(meetingID, presentationName, 1);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, meetingID, "PresentationSharedCommand", map);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		whiteboardInvoker.accept(was);
		verify(connInvokerService);
	}
}
