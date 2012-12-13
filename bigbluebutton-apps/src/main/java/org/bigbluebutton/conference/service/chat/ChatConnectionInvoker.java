package org.bigbluebutton.conference.service.chat;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.chat.PublicChatHistoryQueryReply;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ChatConnectionInvoker implements IMessageOutListener {
	
	private static Logger log = Red5LoggerFactory.getLogger(ChatConnectionInvoker.class, "bigbluebutton");
	
	private IConnectionInvokerService connInvokerService;

	@Override
	public void accept(IMessageOut msg) {
		if(msg == null)
			throw new IllegalArgumentException();
		
		if(msg instanceof PublicChatMessageSent){
			handlePublicChatMessageSent((PublicChatMessageSent) msg);
		}else if(msg instanceof PublicChatHistoryQueryReply){
			handlePublicChatHistoryQueryReply((PublicChatHistoryQueryReply) msg);
		}
	}
	
	
	private void handlePublicChatHistoryQueryReply(PublicChatHistoryQueryReply msg) {
		Map<String,Object> message = new HashMap<String, Object>();
		message.put("count", msg.all_messages.size());
		
		Map<String, Object> pm = new HashMap<String, Object>();
	    for(ChatMessageVO chatObj : msg.all_messages){
	    	Map<String, Object> m = chatObj.toMap();
	    	pm.put(Double.toString(chatObj.fromTime), m);
		}
		message.put("messages", pm);
		
		ClientMessage cm = new ClientMessage(ClientMessage.DIRECT, msg.userID, "PublicChatHistoryQueryReply", message);
		connInvokerService.sendMessage(cm);
	}


	private void handlePublicChatMessageSent(PublicChatMessageSent msg) {
		Map<String,Object> message = new HashMap<String, Object>();
		message.put("meetingID",msg.meetingID);
		message.putAll(msg.chatVO.toMap());
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "PublicChatMessageSentCommand", message);
		connInvokerService.sendMessage(m);
	}


	public void setConnInvokerService(IConnectionInvokerService cis){
		if(cis == null)
			throw new IllegalArgumentException();
		this.connInvokerService = cis;
	}
}
