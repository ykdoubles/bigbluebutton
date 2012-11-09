/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
*
* Copyright (c) 2010 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 2.1 of the License, or (at your option) any later
* version.
*
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* 
*/
package org.bigbluebutton.conference.service.participants;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ConnectionInvokerService;
import org.bigbluebutton.conference.MeetingsManager;
import org.bigbluebutton.conference.Meeting;import org.bigbluebutton.conference.MessageInGateway;
import org.bigbluebutton.conference.User;import org.bigbluebutton.conference.IMeetingListener;
import org.bigbluebutton.conference.messages.in.AllMeetingsStop;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.messages.in.MeetingEnd;

public class ParticipantsApplication {
	private static Logger log = Red5LoggerFactory.getLogger( ParticipantsApplication.class, "bigbluebutton" );	
	
	private ConnectionInvokerService connInvokerService;
	
	private MeetingsManager roomsManager;
	private MessageInGateway messageInGW;
	
	public boolean createRoom(String meetingID) {
		messageInGW.accept(new MeetingStart(meetingID));
		return true;
	}
	
	public boolean destroyRoom(String meetingID) {
		messageInGW.accept(new MeetingEnd(meetingID));
		
		return true;
	}
	
	public void destroyAllRooms() {
		messageInGW.accept(new AllMeetingsStop());
	}
	
	public boolean hasRoom(String name) {
		return roomsManager.hasMeeting(name);
	}
		
	public void setParticipantStatus(String meetingID, String userid, String status, Object value) {
		roomsManager.changeUserStatus(meetingID, userid, status, value);
		
	
	}
	
	public void kickUser(String userID) {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("userID", userID);
		
		ClientMessage m = new ClientMessage(ClientMessage.DIRECT, userID, "UserKickCommand", message);
		connInvokerService.sendMessage(m);		
	}
	
	public void getParticipants(String meetingID, String userID) {
		Map<String, User> users = roomsManager.getUsers(meetingID);
		
		if (users != null) {
			Map<String, Object> message = new HashMap<String, Object>();
			message.put("count", users.size());
			
			if (users.size() > 0) {
				/**
				 * Somehow we need to convert to Map so the client will be able to decode it. Need to figure out if we can send Participant
				 * directly. (ralam - 2/20/2009)
				 */
				Collection<User> pc = users.values();
		    	Map<String, Object> pm = new HashMap<String, Object>();
		    	for (Iterator<User> it = pc.iterator(); it.hasNext();) {
		    		User ap = (User) it.next();
		    		pm.put(ap.getInternalUserID(), ap.toMap()); 
		    	}  
		    	message.put("users", pm);
			}		
			
			ClientMessage m = new ClientMessage(ClientMessage.DIRECT, userID, "UsersListQueryReply", message);
			connInvokerService.sendMessage(m);
		} else {
			log.warn("Could not find room " + meetingID + ". Total rooms " + roomsManager.numberOfMeetings());
		}
	}
	
	public boolean participantLeft(String meetingID, String userid) {
		log.debug("Participant " + userid + " leaving room " + meetingID);
		if (roomsManager.hasMeeting(meetingID)) {
			Meeting room = roomsManager.getMeeting(meetingID);
			log.debug("Removing " + userid + " from room " + meetingID);
			room.removeUser(userid);
						
			Map<String, Object> message = new HashMap<String, Object>();	
			message.put("userID", userid);
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "UserLeftCommand", message);
			connInvokerService.sendMessage(m);
			
			return true;
		}

		return false;
	}
	
	public boolean participantJoin(String meetingID, String userid, String username, String role, String externUserID, Map<String, Object> status) {
		log.debug("participant joining room " + meetingID);
		if (roomsManager.hasMeeting(meetingID)) {
			User p = new User(userid, username, role, externUserID, status);			
			Meeting room = roomsManager.getMeeting(meetingID);
			room.addUser(p);
			log.debug("participant joined room " + meetingID);
						
			Map<String, Object> message = new HashMap<String, Object>();	
			message.put("user", p.toMap());
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "UserJoinedCommand", message);
			connInvokerService.sendMessage(m);
			
			return true;
		}
		log.debug("participant failed to join room " + meetingID);
		return false;
	}
	
	public Map<String, String> getCurrentPresenter(String meetingID) {
		return roomsManager.getCurrentPresenter(meetingID);
	}
	
	public void assignPresenter(String meetingID, String newPresenterUserID, String assignedByUserID){
		if (roomsManager.hasMeeting(meetingID)){
			
			Map<String,String> curPresenter = getCurrentPresenter(meetingID);
			if (curPresenter != null) { 
				setParticipantStatus(meetingID, newPresenterUserID, "presenter", true);
						
				String curPresenterUserid = (String) curPresenter.get("presenterUserID");
				if (curPresenterUserid != null && !curPresenterUserid.equals(newPresenterUserID)){
					log.info("Changing the current presenter [" + curPresenter.get(0) + "] to viewer.");
					setParticipantStatus(meetingID, curPresenterUserid, "presenter", false);
				}
			} else {
				log.info("No current presenter. So do nothing.");
			}
			
			roomsManager.assignPresenter(meetingID, newPresenterUserID, assignedByUserID);
						
			Map<String, Object> message = new HashMap<String, Object>();	
			message.put("newPresenterUserID", newPresenterUserID);
			message.put("assignedByUserID", assignedByUserID);
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "AssignPresenterCommand", message);
			connInvokerService.sendMessage(m);
			
			return;
		}
		log.warn("Assigning presenter on a non-existant room " + meetingID);	
	}
	
	public void setRoomsManager(MeetingsManager r) {
		log.debug("Setting room manager");
		roomsManager = r;
	}
			
	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}
	
	public void setMessageInGateway(MessageInGateway gw) {
		messageInGW = gw;
	}
}
