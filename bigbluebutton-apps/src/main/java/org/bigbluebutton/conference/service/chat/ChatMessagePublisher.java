package org.bigbluebutton.conference.service.chat;

import java.util.HashMap;

import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ChatMessagePublisher implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(ChatMessagePublisher.class, "bigbluebutton");
	
	private IMessagePublisher publisher;
	
	@Override
	public void accept(IMessageOut message) {
		if(message == null){
			throw new IllegalArgumentException();
		}
		
		if(message instanceof PublicChatMessageSent){
			handlePublicChatMessageSent((PublicChatMessageSent)message);
		}
	}
	
	private void handlePublicChatMessageSent(PublicChatMessageSent msg) {
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.PUBLIC_CHAT_MESSAGE_SENT_EVENT);
		map.put("chatType", msg.chatVO.chatType);
		map.put("fromUserID", msg.chatVO.fromUserID);
		map.put("fromUsername", msg.chatVO.fromUsername);
		map.put("fromColor", msg.chatVO.fromColor);
		map.put("fromTime", Double.toString(msg.chatVO.fromTime));
		map.put("fromTimezoneOffset", Long.toString(msg.chatVO.fromTimezoneOffset));
		map.put("fromLang", msg.chatVO.fromLang);
		map.put("message", msg.chatVO.message);
		
		publisher.send(new RedisMessage(MessagingConstants.SYSTEM_CHANNEL, map));
	}

	public void setMessagePublisher(IMessagePublisher pub) {
		if( pub == null )
			throw new IllegalArgumentException();
		publisher = pub;
	}
}
