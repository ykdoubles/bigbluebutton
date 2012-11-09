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
import org.bigbluebutton.conference.messages.in.IMessageIn;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.service.messaging.MessageListener;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.MessagingService;
import org.bigbluebutton.conference.service.presentation.ConversionUpdatesMessageListener;
import org.red5.logging.Red5LoggerFactory;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingsManager {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingsManager.class, "bigbluebutton");
	
	private final Map <String, Meeting> meetings;

	private MessageOutGateway messageOutGW;
	
	MessagingService messagingService;
	ConversionUpdatesMessageListener conversionUpdatesMessageListener;
	
	public MeetingsManager() {
		meetings = new ConcurrentHashMap<String, Meeting>();		
	}
	
	public void accept(IMessageIn message) {
		if (message instanceof MeetingStart) {
			handleMeetingStart((MeetingStart) message);
		}
	}
	
	private void handleMeetingStart(MeetingStart message) {
		if (! meetings.containsKey(message.meetingID)) {
			Meeting m = new Meeting(message.meetingID, messageOutGW);
			meetings.put(message.meetingID, m);
			m.start();
		}		
	}
	

	public void stopMeeting(String meetingID) {
		Meeting m = meetings.get(meetingID);
		if (m != null) {
			m.end();
		}
	}
	
	
	public void removeMeeting(String meetingID) {
		log.debug("Remove room " + meetingID);
		Meeting room = meetings.remove(meetingID);
		if (checkPublisher() && room != null) {
			room.endAndKickAll();

			
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
			return null; //r.getUsers();
		}
		log.warn("Getting participants from a non-existing room " + meetingID);
		return null;
	}
	



	public void addUser(String meetingID, User user) {
		log.debug("Add participant " + user.getName());
		Meeting r = getMeeting(meetingID);
		if (r != null) {
			r.addUser(user);

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
	
	public void setMessageOutGateway(MessageOutGateway gw) {
		messageOutGW = gw;
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
