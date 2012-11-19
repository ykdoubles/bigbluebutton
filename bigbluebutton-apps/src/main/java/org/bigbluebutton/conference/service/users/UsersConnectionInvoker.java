package org.bigbluebutton.conference.service.users;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.meetings.MeetingEnded;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class UsersConnectionInvoker implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(UsersConnectionInvoker.class, "bigbluebutton");
	
	private IConnectionInvokerService connInvokerService;
	
	@Override
	public void accept(IMessageOut message) {
		if(message == null){
			log.error("IMessageOut is null");
			throw new IllegalArgumentException();
		}
		if (message instanceof MeetingStarted) {
			
		} else if (message instanceof MeetingEnded) {
			handleMeetingEnded((MeetingEnded) message);
		} else if (message instanceof UserPresenterChanged) {
			handleUserPresenterChanged((UserPresenterChanged) message);
		} else if (message instanceof UserKicked) {
			handleUserKicked((UserKicked) message);
		} else if (message instanceof UserJoined) {
			handleUserJoined((UserJoined) message);
		} else if (message instanceof UserLeft) {
			handleUserLeft((UserLeft) message);
		} else if (message instanceof UsersQueryReply) {
			handleUsersQueryReply((UsersQueryReply) message);
		}
	}

	private void handleUserJoined(UserJoined msg) {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		UserVO.toMap(msg.user, message);

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
	
	private void handleUserPresenterChanged(UserPresenterChanged msg) {
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("meetingID", msg.meetingID);
		message.put("newPresenterUserID", msg.pres.newPresenterUserID);
		message.put("newPresenterName", msg.pres.newPresenterName);
		message.put("assignedByUser", msg.pres.assignedByUser);
		message.put("assignedByUserID", msg.pres.assignedByUserID);
		
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, msg.meetingID, "AssignPresenterCommand", message);
		connInvokerService.sendMessage(m);
	}
	
	private void handleUsersQueryReply(UsersQueryReply msg) {
		if (msg.users != null) {
			Map<String, Object> message = new HashMap<String, Object>();
			message.put("count", msg.users.size());
			
			if (msg.users.size() > 0) {
				Collection<UserVO> pc = msg.users;
		    	Map<String, Object> pm = new HashMap<String, Object>();
		    	for (Iterator<UserVO> it = pc.iterator(); it.hasNext();) {
		    		UserVO uv = (UserVO) it.next();
		    		
		    		Map<String, Object> m = new HashMap<String, Object>();
		    		UserVO.toMap(uv, m);
		    		
		    		pm.put(uv.intUserID, m); 
		    	}  
		    	message.put("users", pm);
			}		
			
			ClientMessage m = new ClientMessage(ClientMessage.DIRECT, msg.userID, "UsersListQueryReply", message);
			connInvokerService.sendMessage(m);
		}
	}
		
	private void handleMeetingEnded(MeetingEnded m) {
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
