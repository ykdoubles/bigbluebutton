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
import org.bigbluebutton.conference.RoomsManager;
import org.bigbluebutton.conference.Room;import org.bigbluebutton.conference.User;import org.bigbluebutton.conference.IRoomListener;

public class ParticipantsApplication {
	private static Logger log = Red5LoggerFactory.getLogger( ParticipantsApplication.class, "bigbluebutton" );	
	
	private ConnectionInvokerService connInvokerService;
	
	private RoomsManager roomsManager;
	
	public boolean createRoom(String name) {
		if(!roomsManager.hasRoom(name)){
			log.info("Creating room " + name);
			roomsManager.addRoom(new Room(name));
			return true;
		}
		return false;
	}
	
	public boolean destroyRoom(String meetingID) {
		if (roomsManager.hasRoom(meetingID)) {
			Map<String, Object> message = new HashMap<String, Object>();	
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "UserLogoutCommand", message);
			connInvokerService.sendMessage(m);
			
			roomsManager.removeRoom(meetingID);			
		} else {
			log.warn("Destroying non-existing room " + meetingID);
		}
		return true;
	}
	
	public void destroyAllRooms() {
		Set<Map.Entry<String,Room>> meetings = roomsManager.getAllMeetings();
		for (Map.Entry<String,Room> meeting : meetings) {
		    Room room = meeting.getValue();
			Map<String, Object> message = new HashMap<String, Object>();	
			message.put("empty", "nothing");
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, room.getName(), "UserLogoutCommand", message);
			connInvokerService.sendMessage(m);
		}
	}
	
	public boolean hasRoom(String name) {
		return roomsManager.hasRoom(name);
	}
	
	public boolean addRoomListener(String room, IRoomListener listener) {
		if (roomsManager.hasRoom(room)){
			roomsManager.addRoomListener(room, listener);
			return true;
		}
		log.warn("Adding listener to a non-existant room " + room);
		return false;
	}
	
	public void setParticipantStatus(String meetingID, String userid, String status, Object value) {
		roomsManager.changeParticipantStatus(meetingID, userid, status, value);
		
		Map<String, Object> message = new HashMap<String, Object>();	
		message.put("userID", userid);
		message.put("statusName", status);
		message.put("statusValue", value);
		ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, meetingID, "UserStatusChangeCommand", message);
		connInvokerService.sendMessage(m);		
	}
	
	public void getParticipants(String meetingID, String userID) {
		Map<String, User> users = roomsManager.getParticipants(meetingID);
		
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
			log.warn("Could not find room " + meetingID + ". Total rooms " + roomsManager.numberOfRooms());
		}
	}
	
	public boolean participantLeft(String meetingID, String userid) {
		log.debug("Participant " + userid + " leaving room " + meetingID);
		if (roomsManager.hasRoom(meetingID)) {
			Room room = roomsManager.getRoom(meetingID);
			log.debug("Removing " + userid + " from room " + meetingID);
			room.removeParticipant(userid);
						
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
		if (roomsManager.hasRoom(meetingID)) {
			User p = new User(userid, username, role, externUserID, status);			
			Room room = roomsManager.getRoom(meetingID);
			room.addParticipant(p);
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
		if (roomsManager.hasRoom(meetingID)){
			
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
	
	public void setRoomsManager(RoomsManager r) {
		log.debug("Setting room manager");
		roomsManager = r;
	}
			
	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}
}
