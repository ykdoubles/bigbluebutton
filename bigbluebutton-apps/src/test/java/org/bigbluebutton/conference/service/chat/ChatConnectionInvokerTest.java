package org.bigbluebutton.conference.service.chat;

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
import org.bigbluebutton.conference.messages.in.chat.PublicChatMessageSend;
import org.bigbluebutton.conference.messages.out.chat.PublicChatHistoryQueryReply;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.messages.out.meetings.MeetingEnded;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ChatConnectionInvokerTest {
	
	ChatConnectionInvoker chatConnectionInvoker;
	IConnectionInvokerService connInvokerService;
	String meetingID = "0123456789";
	ChatMessageVO testChatMsg;
	
	@BeforeTest
	public void init(){
		chatConnectionInvoker = new ChatConnectionInvoker();
		connInvokerService = createMock(IConnectionInvokerService.class);
		chatConnectionInvoker.setConnInvokerService(connInvokerService);
		
		testChatMsg =  new ChatMessageVO();
		testChatMsg.chatType = ChatMessageVO.PUBLIC_TYPE; 
		testChatMsg.fromUserID = "1111";
		testChatMsg.fromUsername = "John Doe";
		testChatMsg.fromColor = "0";
		testChatMsg.fromTime = Double.valueOf(System.currentTimeMillis());   
		testChatMsg.fromTimezoneOffset = Long.valueOf(0);
		testChatMsg.fromLang = "en"; 	 
		testChatMsg.message = "This is a test message";
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void setConnInvokerService_WhenParamIsNull_ShouldThrowException(){
		chatConnectionInvoker.setConnInvokerService(null);
	}
	
	@Test
	public void Accept_WhenPublicChatMessageSent_ShouldSendMessage() {
		reset(connInvokerService);
		
		PublicChatMessageSent pcms = new PublicChatMessageSent(meetingID, testChatMsg);
		Map<String,Object> msg = new HashMap<String,Object>();
		msg.put("meetingID", pcms.meetingID);
		msg.putAll(pcms.chatVO.toMap());
		
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, pcms.meetingID, "PublicChatMessageSentCommand", msg);
		connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		
		replay(connInvokerService);
		
		chatConnectionInvoker.accept(pcms);
		verify(connInvokerService);	
	}
	
	@Test(enabled = false)
	public void Accept_WhenPublicChatHistoryQueryReply_ShouldSendMessage() {
		reset(connInvokerService);
		
		Collection<ChatMessageVO> all_messages = new ArrayList<ChatMessageVO>();
		all_messages.add(testChatMsg);
		
		PublicChatHistoryQueryReply pcms = new PublicChatHistoryQueryReply(this.meetingID, "1112" , all_messages);
		
		
		//ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, pcms.meetingID, "PublicChatMessageSentCommand", msg);
		//connInvokerService.sendMessage(ClientMessageMatcher.eqClientMessage(cm));
		
		
		replay(connInvokerService);
		
		chatConnectionInvoker.accept(pcms);
		verify(connInvokerService);	
	}

}
