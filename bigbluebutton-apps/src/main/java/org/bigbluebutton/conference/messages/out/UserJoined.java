package org.bigbluebutton.conference.messages.out;

import org.bigbluebutton.conference.vo.UserVO;

public class UserJoined extends AbstractMessageOut {
	
	public final UserVO user;
	
	public UserJoined(String meetingID, UserVO user) {
		super(meetingID);
		this.user = user;
	}
}
