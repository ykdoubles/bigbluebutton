package org.bigbluebutton.conference.messages.out.chat;

import java.util.Collection;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;
import org.bigbluebutton.conference.service.chat.ChatMessageVO;

public class PublicChatHistoryQueryReply extends AbstractMessageOut {

	public final String userID;
	public final Collection<ChatMessageVO> all_messages;
	
	public PublicChatHistoryQueryReply(String meetingID, String userID, Collection<ChatMessageVO> all_messages) {
		super(meetingID);
		this.userID = userID;
		this.all_messages = all_messages;
	}

}
