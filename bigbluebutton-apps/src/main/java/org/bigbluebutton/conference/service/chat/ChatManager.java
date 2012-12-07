package org.bigbluebutton.conference.service.chat;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
	
	private final Map<Double, ChatMessageVO> chat_messages = new ConcurrentHashMap<Double, ChatMessageVO>();
	
	public boolean addChatMessage(ChatMessageVO chat){
		if(chat_messages.containsKey(chat.fromTime)){
			return false;
		}
		chat_messages.put(chat.fromTime, chat);
		return true;
	}
	
	public ChatMessageVO removeChatMessage(Double timestamp){
		return chat_messages.remove(timestamp);
	}
	
	public Collection<ChatMessageVO> getChatMessages(){
		return chat_messages.values();
	}
	
}
