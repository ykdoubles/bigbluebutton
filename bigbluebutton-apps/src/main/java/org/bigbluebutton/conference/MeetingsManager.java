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
package org.bigbluebutton.conference;

import org.slf4j.Logger;
import org.bigbluebutton.conference.service.messaging.MessageListener;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.MessagingService;
import org.bigbluebutton.conference.service.presentation.ConversionUpdatesMessageListener;
import org.red5.logging.Red5LoggerFactory;
import com.google.gson.Gson;
import net.jcip.annotations.ThreadSafe;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This encapsulates access to Room and Participant. This class must be threadsafe.
 */
@ThreadSafe
public class MeetingsManager {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingsManager.class, "bigbluebutton");
	
	private final Map <String, Meeting> meetings;

	MessagingService messagingService;
	ConversionUpdatesMessageListener conversionUpdatesMessageListener;
	
	public MeetingsManager() {
		meetings = new ConcurrentHashMap<String, Meeting>();		
	}
	
	public void addMeeting(Meeting meeting) {

		meeting.addMeetingListener(new ParticipantUpdatingRoomListener(meeting,messagingService)); 	
		
		if (checkPublisher()) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("meetingId", meeting.getMeetingID());
			map.put("messageId", MessagingConstants.MEETING_STARTED_EVENT);
			
			Gson gson = new Gson();
			messagingService.send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
			
			log.debug("Notified event listener of conference start");
		}
		meetings.put(meeting.getMeetingID(), meeting);
	}
	
	public void removeMeeting(String meetingID) {
		log.debug("Remove room " + meetingID);
		Meeting room = meetings.remove(meetingID);
		if (checkPublisher() && room != null) {
			room.endAndKickAll();
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("meetingId", room.getMeetingID());
			map.put("messageId", MessagingConstants.MEETING_ENDED_EVENT);
			
			Gson gson = new Gson();
			messagingService.send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
			
			log.debug("Notified event listener of conference end");
		}
	}

	
	public Set<Map.Entry<String,Meeting>> getAllMeetings() {
		return meetings.entrySet();
	}
	
	private boolean checkPublisher() {
		return messagingService != null;
	}

		
	public boolean hasMeeting(String meetingID) {
		return meetings.containsKey(meetingID);
	}
	
	public int numberOfMeetings() {
		return meetings.size();
	}
	
	/**
	 * Keeping getRoom private so that all access to Room goes through here.
	 */
	//TODO: this method becomes public for ParticipantsApplication, ask if it's right? 
	public Meeting getMeeting(String meetingID) {
		return meetings.get(meetingID);
	}
	
	public Map<String, User> getUsers(String meetingID) {
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			return r.getUsers();
		}
		log.warn("Getting participants from a non-existing room " + meetingID);
		return null;
	}
	
	public void addMeetingListener(String meetingID, IMeetingListener listener) {
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			r.addMeetingListener(listener);
			return;
		}
		log.warn("Adding listener to a non-existing room " + meetingID);
	}
	
	// TODO: this must be broken, right?  where is roomName? (JRT: 9/25/2009)
//	public void removeRoomListener(IRoomListener listener) {
//		
//		Room r = getRoom(roomName);
//		if (r != null) {
//			r.removeRoomListener(listener)
//			return
//		}	
//		log.warn("Removing listener from a non-existing room ${roomName}")
//	}

	public void addUser(String meetingID, User user) {
		log.debug("Add participant " + user.getName());
		Meeting r = getMeeting(meetingID);
		if (r != null) {
/*			if (checkPublisher()) {

				if (r.getNumberOfParticipants() == 0) {
					log.debug("Notified event listener of conference start");
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("meetingId", roomName);
					map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
					
					Gson gson = new Gson();
					publisher.publish(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
					
				}
			}
*/			r.addUser(user);

			return;
		}
		log.warn("Adding participant to a non-existing room " + meetingID);
	}
	
	public void removeUser(String roomName, String userid) {
		log.debug("Remove participant " + userid + " from " + roomName);
		Meeting r = getMeeting(roomName);
		if (r != null) {
			if (checkPublisher()) {
				//conferenceEventListener.participantsUpdated(r);
				//missing method()?
			}
			r.removeUser(userid);

			return;
		}
		log.warn("Removing listener from a non-existing room " + roomName);
	}
	
	public void changeUserStatus(String meetingID, String userid, String status, Object value) {
		log.debug("Change participant status " + userid + " - " + status + " [" + value + "]");
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			r.changeUserStatus(userid, status, value);
			return;
		}		
		log.warn("Changing participant status on a non-existing room " + meetingID);
	}

	public void setMessagingService(MessagingService messagingService) {
		this.messagingService = messagingService;
		this.messagingService.addListener(new RoomsManagerListener());
		this.messagingService.start();
	}
	
	public Map<String, String> getCurrentPresenter(String meetingID){
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			return r.getCurrentPresenter();		
		}	
		log.warn("Getting presenter from a non-existing room " + meetingID);
		return null;
	}
	
	public void assignPresenter(String meetingID, String newPresenterUserID, String assignedByUserID){
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			r.assignPresenter(newPresenterUserID, assignedByUserID);
			return;
		}	
		log.warn("Assigning presenter to a non-existing room " + meetingID);	
	}
	
	public void setConversionUpdatesMessageListener(ConversionUpdatesMessageListener conversionUpdatesMessageListener) {
		this.conversionUpdatesMessageListener = conversionUpdatesMessageListener;
	}
	
	private class RoomsManagerListener implements MessageListener{

		@Override
		public void endMeetingRequest(String meetingId) {
			log.debug("End meeting request for room: " + meetingId);
			Meeting room = getMeeting(meetingId); // must do this because the room coming in is serialized (no transient values are present)
			if (room != null)
				room.endAndKickAll();
			else
				log.debug("Could not find room " + meetingId);
		}
		
		@Override
		public void presentationUpdates(HashMap<String, String> map) {
			conversionUpdatesMessageListener.handleReceivedMessage(map);
		}
		
	}
	
}
