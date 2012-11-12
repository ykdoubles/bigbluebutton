package org.bigbluebutton.conference.service.users;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.in.UserAssignPresenter;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.MeetingEnded;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.bigbluebutton.conference.messages.out.UserJoined;
import org.bigbluebutton.conference.messages.out.UserKicked;
import org.bigbluebutton.conference.messages.out.UserLeft;
import org.bigbluebutton.conference.messages.out.UserStatusChanged;
import org.bigbluebutton.conference.messages.out.UsersQueryReply;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.vo.UserVO;
import com.google.gson.Gson;

public class UsersMessagePublisher implements IMessageOutListener {

	private IMessagePublisher publisher;
	
	@Override
	public void accept(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			
		} else if (message instanceof MeetingEnded) {
			handleMeetingStopped((MeetingEnded) message);
		} else if (message instanceof UserStatusChanged) {
			handleUserStatusChanged((UserStatusChanged) message);
		} else if (message instanceof UserKicked) {
			handleUserKicked((UserKicked) message);
		} else if (message instanceof UserJoined) {
			handleUserJoined((UserJoined) message);
		}
	}

	private void handleUserJoined(UserJoined msg) {
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
		map.put("internalUserId", msg.user.intUserID);
		map.put("externalUserId", msg.user.extUserID);
		map.put("fullname", msg.user.name);
		map.put("role", msg.user.role);
		
		Gson gson= new Gson();
		publisher.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
	}
	
	private void handleUserLeft(UserLeft msg) {
		HashMap<String,String> map= new HashMap<String, String>();
		map.put("meetingId", msg.meetingID);
		map.put("messageId", MessagingConstants.USER_LEFT_EVENT);
		map.put("internalUserId", msg.userID);
		
		Gson gson = new Gson();
		publisher.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
	}
	
	private void handleUserKicked(UserKicked m) {		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("meetingID", m.meetingID);
		message.put("userID", m.userID);
		
	}
	
	private void handleUserAssignPresenter(UserAssignPresenter msg) {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		message.put("newPresenterUserID", msg.newPresenterUserID);
		message.put("assignedByUserID", msg.assignedByUserID);
		

	}
	
	private void handleUsersQueryReply(UsersQueryReply msg) {
		if (msg.users != null) {
			Map<String, Object> message = new HashMap<String, Object>();
			message.put("count", msg.users.size());
			
			if (msg.users.size() > 0) {
				/**
				 * Somehow we need to convert to Map so the client will be able to decode it. Need to figure out if we can send Participant
				 * directly. (ralam - 2/20/2009)
				 */
				Collection<UserVO> pc = msg.users;
		    	Map<String, Object> pm = new HashMap<String, Object>();
		    	for (Iterator<UserVO> it = pc.iterator(); it.hasNext();) {
	//	    		User ap = (User) it.next();
	//	    		pm.put(ap.getInternalUserID(), ap.toMap()); 
		    	}  
		    	message.put("users", pm);
			}		
			
		}
	}
	
	private void handleUserStatusChanged(UserStatusChanged message) {
		HashMap<String,String> map= new HashMap<String, String>();
//		map.put("meetingId", this.room.getMeetingID());
//		map.put("messageId", MessagingConstants.USER_STATUS_CHANGE_EVENT);
		
//		map.put("internalUserId", p.getInternalUserID());
//		map.put("status", status);
//		map.put("value", value.toString());
		
		Gson gson= new Gson();
		publisher.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
	}
	
	private void handleMeetingStopped(MeetingEnded m) {
//		log.debug("Meeting stopped message [" + m.meetingID + "]");		
//		Map<String, Object> msg = new HashMap<String, Object>();
//		msg.put("meetingID", m.meetingID);
	
	}
	
	public void setMessagePublisher(IMessagePublisher pub) {
		publisher = pub;
	}
}
