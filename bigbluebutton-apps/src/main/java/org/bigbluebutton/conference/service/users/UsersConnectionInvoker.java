package org.bigbluebutton.conference.service.users;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.in.UserAssignPresenter;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.MeetingEnded;
import org.bigbluebutton.conference.messages.out.UserJoined;
import org.bigbluebutton.conference.messages.out.UserKicked;
import org.bigbluebutton.conference.messages.out.UserLeft;
import org.bigbluebutton.conference.messages.out.UserStatusChanged;
import org.bigbluebutton.conference.messages.out.UsersQueryReply;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class UsersConnectionInvoker implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(UsersConnectionInvoker.class, "bigbluebutton");
	
	private IConnectionInvokerService connInvokerService;
	
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
		Map<String, Object> message = new HashMap<String, Object>();	
//		message.put("user", p.toMap());
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "UserJoinedCommand", message);
		connInvokerService.sendMessage(m);
	}
	
	private void handleUserLeft(UserLeft msg) {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		message.put("userID", msg.userID);
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "UserLeftCommand", message);
		connInvokerService.sendMessage(m);
	}
	
	private void handleUserKicked(UserKicked m) {		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("meetingID", m.meetingID);
		message.put("userID", m.userID);
		
		ClientMessage cm = new ClientMessage(ClientMessage.DIRECT, m.userID, "UserKickCommand", message);
		connInvokerService.sendMessage(cm);
	}
	
	private void handleUserAssignPresenter(UserAssignPresenter msg) {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		message.put("newPresenterUserID", msg.newPresenterUserID);
		message.put("assignedByUserID", msg.assignedByUserID);
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "AssignPresenterCommand", message);
		connInvokerService.sendMessage(m);
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
			
			ClientMessage m = new ClientMessage(ClientMessage.DIRECT, msg.userID, "UsersListQueryReply", message);
			connInvokerService.sendMessage(m);
		}
	}
	
	private void handleUserStatusChanged(UserStatusChanged message) {
		Map<String, Object> msg = new HashMap<String, Object>();	
		msg.put("meetingID", message.meetingID);
		msg.put("userID", message.userID);
		msg.put("statusName", message.statusName);
		msg.put("statusValue", message.statusValue);
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, message.meetingID, "UserStatusChangeCommand", msg);
		connInvokerService.sendMessage(m);	
	}
	
	private void handleMeetingStopped(MeetingEnded m) {
		log.debug("Meeting stopped message [" + m.meetingID + "]");		
		Map<String, Object> msg = new HashMap<String, Object>();
		msg.put("meetingID", m.meetingID);
		ClientMessage cm = new ClientMessage(ClientMessage.BROADCAST, m.meetingID, "UserLogoutCommand", msg);
		connInvokerService.sendMessage(cm);		
	}
	
	public void setConnInvokerService(IConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}
}
