package org.bigbluebutton.conference.messages.out.chat;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;
import org.bigbluebutton.conference.service.chat.ChatMessageVO;

public class PublicChatMessageSent extends AbstractMessageOut {

	public final ChatMessageVO chatVO;
	
	public PublicChatMessageSent(String meetingID,ChatMessageVO chatVO) {
		super(meetingID);
		this.chatVO = chatVO;
	}
	
}
