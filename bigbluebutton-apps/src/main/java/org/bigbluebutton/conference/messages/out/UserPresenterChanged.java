package org.bigbluebutton.conference.messages.out;

import org.bigbluebutton.conference.vo.NewPresenterVO;

public class UserPresenterChanged extends AbstractMessageOut {

	public final NewPresenterVO pres;
	
	public UserPresenterChanged(String meetingID, NewPresenterVO pres) {
		super(meetingID);
		this.pres = pres;
	}
}
