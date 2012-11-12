package org.bigbluebutton.conference.messages.out.users;

import org.bigbluebutton.conference.messages.out.AbstractMessageOut;
import org.bigbluebutton.conference.vo.NewPresenterVO;

public class UserPresenterChanged extends AbstractMessageOut {

	public final NewPresenterVO pres;
	
	public UserPresenterChanged(String meetingID, NewPresenterVO pres) {
		super(meetingID);
		this.pres = pres;
	}
}
