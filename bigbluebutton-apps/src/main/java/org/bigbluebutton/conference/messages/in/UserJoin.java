package org.bigbluebutton.conference.messages.in;

import org.bigbluebutton.conference.messages.in.vo.UserVO;

public class UserJoin implements IMessageIn {

	public final String meetingID;
	public final UserVO user;
	
	public UserJoin(String meetingID, UserVO user) {
		this.meetingID = meetingID;
		this.user = user;
	}
}
