package org.bigbluebutton.conference;

import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.testng.Assert;

public class MessageOutMatcher implements IArgumentMatcher{
	
	private IMessageOut actual;
	private IMessageOut second_param;

	public MessageOutMatcher(IMessageOut mo){
		this.actual = mo;
	}
	
	public MessageOutMatcher(IMessageOut mo, IMessageOut second_param){
		this.actual = mo;
		this.second_param = second_param;
	}
	
	public static IMessageOut eqMessageOut(IMessageOut mo){
		EasyMock.reportMatcher(new MessageOutMatcher(mo));
		return null;
	}
	
	public static IMessageOut eqMessageOutWithSecondParam(IMessageOut mo, IMessageOut second_param){
		EasyMock.reportMatcher(new MessageOutMatcher(mo,second_param));
		return null;
	}
	
	@Override
	public void appendTo(StringBuffer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean matches(Object arg0) {
		if(arg0 instanceof MeetingStarted){
			MeetingStarted ms_expected = (MeetingStarted) arg0;
			Assert.assertEquals(((MeetingStarted)actual).meetingID, ms_expected.meetingID);
			return true;
		}else if(arg0 instanceof UserJoined){
			UserJoined us_expected = (UserJoined) arg0;
			Assert.assertEquals(((UserJoined)actual).meetingID, us_expected.meetingID);
			Assert.assertEquals(((UserJoined)actual).user, us_expected.user);
			return true;
		}else if(arg0 instanceof UserPresenterChanged){
			UserPresenterChanged upc_actual = (UserPresenterChanged) ((actual instanceof UserPresenterChanged) ? actual : second_param);
			UserPresenterChanged upc_expected = (UserPresenterChanged) arg0;
			Assert.assertEquals(upc_actual.meetingID, upc_expected.meetingID);
			Assert.assertEquals(upc_actual.meetingID, upc_expected.meetingID);
			return true;
		}else if(arg0 instanceof UsersQueryReply){
			UsersQueryReply uqr_expected = (UsersQueryReply) arg0;
			//Assert.assertEquals(((UsersQueryReply)actual).meetingID, uqr_expected.meetingID);
			//Assert.assertEquals(((UsersQueryReply)actual).userID, uqr_expected.userID);
			//Assert.assertEquals(((UsersQueryReply)actual).users, uqr_expected.users);
			return true;
		}
		
		return false;
	}
	
}
