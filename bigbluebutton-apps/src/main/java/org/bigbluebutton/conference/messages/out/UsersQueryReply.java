package org.bigbluebutton.conference.messages.out;

import java.util.Collection;

import org.bigbluebutton.conference.vo.UserVO;

public class UsersQueryReply extends AbstractMessageOut {

	public final String userID;
	public final Collection<UserVO> users;
	
	public UsersQueryReply(String meetingID, String userID, Collection<UserVO> users) {
		super(meetingID);
		this.userID = userID;
		this.users = users;
	}
}
