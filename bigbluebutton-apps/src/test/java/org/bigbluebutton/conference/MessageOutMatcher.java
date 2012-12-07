package org.bigbluebutton.conference;

import java.util.ArrayList;
import java.util.Arrays;

import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.chat.PublicChatHistoryQueryReply;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserHandStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UserVideoStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserVoiceStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.service.chat.ChatMessageVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
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
	public boolean matches(Object expectedObj) {
		if(actuals == null){
			return false;
		}
		
		if(actuals.size() == 0)
			return false;
		
		IMessageOut actualObj = actuals.remove(0);
		
		if(expectedObj instanceof MeetingStarted){
			MeetingStarted actual = (MeetingStarted) actualObj;
			MeetingStarted expected = (MeetingStarted) expectedObj;
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			return true;
		}else if(expectedObj instanceof UserJoined){
			UserJoined actual = (UserJoined)actualObj;
			UserJoined expected = (UserJoined) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.user, expected.user);
			return true;
		}else if(expectedObj instanceof UserPresenterChanged){
			UserPresenterChanged actual = (UserPresenterChanged) actualObj;
			UserPresenterChanged expected = (UserPresenterChanged) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.pres.newPresenterUserID, expected.pres.newPresenterUserID);
			Assert.assertEquals(actual.pres.newPresenterName, expected.pres.newPresenterName);
			Assert.assertEquals(actual.pres.assignedByUser, expected.pres.assignedByUser);
			Assert.assertEquals(actual.pres.assignedByUserID, expected.pres.assignedByUserID);
			return true;
		}else if(expectedObj instanceof UsersQueryReply){
			UsersQueryReply actual = (UsersQueryReply) actualObj;
			UsersQueryReply expected = (UsersQueryReply) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			
			//We will need to compare that both collections has the same size of users
			Assert.assertEquals(actual.users.size(), expected.users.size());
			
			//Now, we need to check that it has the same users
			for(UserVO uservo_actual : actual.users){
				boolean valid = false;
				
				for(UserVO uservo_expected : expected.users){
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
		}else if(expectedObj instanceof UserHandStatusChanged){
			UserHandStatusChanged actual = (UserHandStatusChanged) actualObj;
			UserHandStatusChanged expected = (UserHandStatusChanged) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			Assert.assertEquals(actual.raised, expected.raised);
			Assert.assertEquals(actual.setByUserID, expected.setByUserID);
			return true;
		}
		else if(expectedObj instanceof UserVoiceStatusChanged){
			UserVoiceStatusChanged actual = (UserVoiceStatusChanged) actualObj;
			UserVoiceStatusChanged expected = (UserVoiceStatusChanged) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			Assert.assertEquals(actual.hasVoice, expected.hasVoice);
			Assert.assertEquals(actual.streamName, expected.streamName);
			return true;
		}
		else if(expectedObj instanceof UserVideoStatusChanged){
			UserVideoStatusChanged actual = (UserVideoStatusChanged) actualObj;
			UserVideoStatusChanged expected = (UserVideoStatusChanged) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			Assert.assertEquals(actual.hasVideo, expected.hasVideo);
			Assert.assertEquals(actual.streamName, expected.streamName);
			return true;
		}
		else if(expectedObj instanceof UserLeft){
			UserLeft actual = (UserLeft) actualObj;
			UserLeft expected = (UserLeft) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			return true;
		}
		else if(expectedObj instanceof UserKicked){
			UserKicked actual = (UserKicked) actualObj;
			UserKicked expected = (UserKicked) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			return true;
		}else if(expectedObj instanceof PublicChatMessageSent){
			PublicChatMessageSent actual = (PublicChatMessageSent) actualObj;
			PublicChatMessageSent expected = (PublicChatMessageSent) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.chatVO, expected.chatVO);
			return true;
		}else if(expectedObj instanceof PublicChatHistoryQueryReply){
			PublicChatHistoryQueryReply actual = (PublicChatHistoryQueryReply) actualObj;
			PublicChatHistoryQueryReply expected = (PublicChatHistoryQueryReply) expectedObj;
			
			Assert.assertEquals(actual.meetingID, expected.meetingID);
			Assert.assertEquals(actual.userID, expected.userID);
			
			//We will need to compare that both collections has the same size of users
			Assert.assertEquals(actual.all_messages.size(), expected.all_messages.size());
			
			//Now, we need to check that it has the same users
			for(ChatMessageVO chatvoActual : actual.all_messages){
				boolean valid = false;
				
				for(ChatMessageVO chatvoExpected : expected.all_messages){
					if(chatvoActual.fromTime == chatvoExpected.fromTime){
						valid = true;
						Assert.assertEquals(chatvoActual.chatType, chatvoExpected.chatType);
						Assert.assertEquals(chatvoActual.fromUserID, chatvoExpected.fromUserID);
						Assert.assertEquals(chatvoActual.fromUsername, chatvoExpected.fromUsername);
						Assert.assertEquals(chatvoActual.fromColor, chatvoExpected.fromColor);
						Assert.assertEquals(chatvoActual.fromLang, chatvoExpected.fromLang);
						Assert.assertEquals(chatvoActual.fromTimezoneOffset, chatvoExpected.fromTimezoneOffset);
						Assert.assertEquals(chatvoActual.message, chatvoExpected.message);
						
					}
				}
				
				if(!valid){
					return false;
				}
			}
			return true;
			
		}
		
		return false;
	}
	
}
