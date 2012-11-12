package org.bigbluebutton.conference.messages.out.users;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;
import org.bigbluebutton.conference.vo.UserVO;

public class UserJoined extends AbstractMessageOut {
	
	public final UserVO user;
	
	public UserJoined(String meetingID, UserVO user) {
		super(meetingID);
		this.user = user;
	}
}
