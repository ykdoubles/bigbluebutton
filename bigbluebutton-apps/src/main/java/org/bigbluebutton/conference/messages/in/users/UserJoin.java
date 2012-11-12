package org.bigbluebutton.conference.messages.in.users;

import org.bigbluebutton.conference.messages.in.AbstractMessageIn;
import org.bigbluebutton.conference.vo.UserVO;

public class UserJoin extends AbstractMessageIn {

	public final UserVO user;
	
	public UserJoin(String meetingID, UserVO user) {
		super(meetingID);
		this.user = user;
	}
}
