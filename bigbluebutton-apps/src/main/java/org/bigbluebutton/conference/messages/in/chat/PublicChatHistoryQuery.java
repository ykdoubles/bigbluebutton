package org.bigbluebutton.conference.messages.in.chat;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;

public class PublicChatHistoryQuery extends AbstractMessageIn {

	public final String userID;
	
	public PublicChatHistoryQuery(String meetingID,String userID) {
		super(meetingID);
		this.userID = userID;
	}

}
