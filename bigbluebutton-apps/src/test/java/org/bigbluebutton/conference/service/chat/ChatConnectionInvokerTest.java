package org.bigbluebutton.conference.service.chat;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.messages.in.chat.PublicChatMessageSend;
import org.bigbluebutton.conference.messages.out.meetings.MeetingEnded;
import org.bigbluebutton.conference.service.users.ClientMessageMatcher;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ChatConnectionInvokerTest {
	
	ChatConnectionInvoker chatConnectionInvoker;
	IConnectionInvokerService connInvokerService;
	String meetingID = "0123456789";
	
	@BeforeTest
	public void init(){
		chatConnectionInvoker = new ChatConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		chatConnectionInvoker.setConnInvokerService(connInvokerService);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void setConnInvokerService_WhenParamIsNull_ShouldThrowException(){
		chatConnectionInvoker.setConnInvokerService(null);
	}
	
	@Test
	public void Accept_WhenNewPublicMessage_ShouldSendMessage() {
		reset(connInvokerService);
		/*ChatMessageVO cm = new ChatMessageVO();
		cm.chatType = ChatMessageVO.PUBLIC_TYPE;
		cm.fromUserID = "1111";
		cm.fromUsername = "John Doe";
		cm.fromLang = "en";
		cm.fromColor = "0";
		cm.fromTime = 12.0;
		cm.fromTimezoneOffset = (long) 0;
		cm.message = "Test Message";
		*/
		//PublicChatMessageSend pcms = new PublicChatMessageSend(meetingID, cm);
		
		/*MeetingEnded me = new MeetingEnded(meetingID);
		
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", me.meetingID);
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, me.meetingID, "UserLogoutCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		replay(connInvokerService);
		
		usersConnectionInvoker.accept(me);
		verify(connInvokerService);*/
	}

}
