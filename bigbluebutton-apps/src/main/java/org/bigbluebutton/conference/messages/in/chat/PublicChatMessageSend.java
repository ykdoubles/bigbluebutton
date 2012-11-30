package org.bigbluebutton.conference.messages.in.chat;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;
import org.bigbluebutton.conference.service.chat.ChatMessageVO;

public class PublicChatMessageSend extends AbstractMessageIn {
	
	public final ChatMessageVO chatVO; 

	public PublicChatMessageSend(String meetingID, ChatMessageVO chatVO) {
		super(meetingID);
		this.chatVO = chatVO;
	}

}
