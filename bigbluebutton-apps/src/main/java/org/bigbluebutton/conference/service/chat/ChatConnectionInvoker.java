package org.bigbluebutton.conference.service.chat;

import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.vo.UserVO;
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
		}
	}
	
	
	private void handlePublicChatMessageSent(PublicChatMessageSent msg) {
		log.debug("holaaaaaaaaaaaaaaa");
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
