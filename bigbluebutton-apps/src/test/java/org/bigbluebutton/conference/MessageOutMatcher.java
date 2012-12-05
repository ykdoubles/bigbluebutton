package org.bigbluebutton.conference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserHandStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UserVideoStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserVoiceStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.vo.UserVO;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.red5.compatibility.flex.messaging.io.ArrayCollection;
import org.testng.Assert;

public class MessageOutMatcher implements IArgumentMatcher{
	
	private ArrayList<IMessageOut> actuals;

	public MessageOutMatcher(IMessageOut... mo){
		this.actuals = new ArrayList<IMessageOut>(Arrays.asList(mo));
	}
	
	public static IMessageOut eqMessageOut(IMessageOut... mo){
		EasyMock.reportMatcher(new MessageOutMatcher(mo));
		return null;
	}
	
	@Override
	public void appendTo(StringBuffer buffer) {
		buffer.append("Number of parameters:");
		buffer.append(actuals.size());
		for(IMessageOut imo : actuals){
			buffer.append("Instance type:");
			buffer.append(imo.getClass());
			buffer.append("ToString:");
			buffer.append(imo.toString());
		}
	}

	@Override
	public boolean matches(Object arg0) {
		if(actuals == null){
			return false;
		}
		
		if(actuals.size() == 0)
			return false;
		
		IMessageOut actual = actuals.remove(0);
		
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
			UserPresenterChanged upc_actual = (UserPresenterChanged) actual;
			UserPresenterChanged upc_expected = (UserPresenterChanged) arg0;
			
			Assert.assertEquals(upc_actual.meetingID, upc_expected.meetingID);
			Assert.assertEquals(upc_actual.pres.newPresenterUserID, upc_expected.pres.newPresenterUserID);
			Assert.assertEquals(upc_actual.pres.newPresenterName, upc_expected.pres.newPresenterName);
			Assert.assertEquals(upc_actual.pres.assignedByUser, upc_expected.pres.assignedByUser);
			Assert.assertEquals(upc_actual.pres.assignedByUserID, upc_expected.pres.assignedByUserID);
			return true;
		}else if(arg0 instanceof UsersQueryReply){
			UsersQueryReply uqr_actual = (UsersQueryReply) actual;
			UsersQueryReply uqr_expected = (UsersQueryReply) arg0;
			
			Assert.assertEquals(uqr_actual.meetingID, uqr_expected.meetingID);
			Assert.assertEquals(uqr_actual.userID, uqr_expected.userID);
			
			//We will need to compare that both collections has the same size of users
			Assert.assertEquals(uqr_actual.users.size(), uqr_expected.users.size());
			
			//Now, we need to check that it has the same users
			for(UserVO uservo_actual : uqr_actual.users){
				boolean valid = false;
				
				for(UserVO uservo_expected : uqr_expected.users){
					if(uservo_actual.intUserID.equalsIgnoreCase(uservo_expected.intUserID)){
						valid = true;
						Assert.assertEquals(uservo_actual.extUserID, uservo_expected.extUserID);
						Assert.assertEquals(uservo_actual.name, uservo_expected.name);
						Assert.assertEquals(uservo_actual.role, uservo_expected.role);
					}
				}
				
				if(!valid){
					return false;
				}
			}
			
			return true;
		}else if(arg0 instanceof UserHandStatusChanged){
			UserHandStatusChanged uhsc_actual = (UserHandStatusChanged) actual;
			UserHandStatusChanged uhsc_expected = (UserHandStatusChanged) arg0;
			
			Assert.assertEquals(uhsc_actual.meetingID, uhsc_expected.meetingID);
			Assert.assertEquals(uhsc_actual.userID, uhsc_expected.userID);
			Assert.assertEquals(uhsc_actual.raised, uhsc_expected.raised);
			Assert.assertEquals(uhsc_actual.setByUserID, uhsc_expected.setByUserID);
			return true;
		}
		else if(arg0 instanceof UserVoiceStatusChanged){
			UserVoiceStatusChanged uvsc_actual = (UserVoiceStatusChanged) actual;
			UserVoiceStatusChanged uvsc_expected = (UserVoiceStatusChanged) arg0;
			
			Assert.assertEquals(uvsc_actual.meetingID, uvsc_expected.meetingID);
			Assert.assertEquals(uvsc_actual.userID, uvsc_expected.userID);
			Assert.assertEquals(uvsc_actual.hasVoice, uvsc_expected.hasVoice);
			Assert.assertEquals(uvsc_actual.streamName, uvsc_expected.streamName);
			return true;
		}
		else if(arg0 instanceof UserVideoStatusChanged){
			UserVideoStatusChanged uvsc_actual = (UserVideoStatusChanged) actual;
			UserVideoStatusChanged uvsc_expected = (UserVideoStatusChanged) arg0;
			
			Assert.assertEquals(uvsc_actual.meetingID, uvsc_expected.meetingID);
			Assert.assertEquals(uvsc_actual.userID, uvsc_expected.userID);
			Assert.assertEquals(uvsc_actual.hasVideo, uvsc_expected.hasVideo);
			Assert.assertEquals(uvsc_actual.streamName, uvsc_expected.streamName);
			return true;
		}
		else if(arg0 instanceof UserLeft){
			UserLeft ul_actual = (UserLeft) actual;
			UserLeft ul_expected = (UserLeft) arg0;
			
			Assert.assertEquals(ul_actual.meetingID, ul_expected.meetingID);
			Assert.assertEquals(ul_actual.userID, ul_expected.userID);
			return true;
		}
		else if(arg0 instanceof UserKicked){
			UserKicked uk_actual = (UserKicked) actual;
			UserKicked uk_expected = (UserKicked) arg0;
			
			Assert.assertEquals(uk_actual.meetingID, uk_expected.meetingID);
			Assert.assertEquals(uk_actual.userID, uk_expected.userID);
			return true;
		}
		
		return false;
	}
	
}
