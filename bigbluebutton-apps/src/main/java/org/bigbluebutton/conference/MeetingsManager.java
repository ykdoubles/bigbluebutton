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
import org.bigbluebutton.conference.imp.MessageOutGateway;
import org.bigbluebutton.conference.messages.in.AbstractMessageIn;
import org.bigbluebutton.conference.messages.in.AllMeetingsStop;
import org.bigbluebutton.conference.messages.in.IMessageIn;
import org.bigbluebutton.conference.messages.in.MeetingEnd;
import org.bigbluebutton.conference.messages.in.MeetingForceEnd;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.service.messaging.MessageListener;
import org.bigbluebutton.conference.service.messaging.MessagingService;
import org.bigbluebutton.conference.service.presentation.ConversionUpdatesMessageListener;
import org.red5.logging.Red5LoggerFactory;
import java.util.HashMap;
import java.util.Map;
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
		} else if (message instanceof MeetingEnd) {
			handleMeetingEnd((MeetingEnd) message);
		} else if (message instanceof AllMeetingsStop) {
			
		} else {
			AbstractMessageIn min = (AbstractMessageIn) message;
			Meeting m = meetings.get(min.meetingID);
			if (m != null) {
				m.processMessage(message);
			} else {
				log.warn("Can't process " + min.getClass().getName() + " for meeting id [" + min.meetingID + "]");
			}
		}
	}
	
	private void handleMeetingStart(MeetingStart msg) {
		if (! meetings.containsKey(msg.meetingID)) {
			Meeting m = new Meeting(msg.meetingID, msg.meetingID, messageOutGW);
			meetings.put(msg.meetingID, m);
			m.processMessage(msg);
		} else {
			log.info("Requesting to start already running meeting [" + msg.meetingID + "]");
		}
	}
		
	private void handleMeetingEnd(MeetingEnd msg) {
		Meeting m = meetings.remove(msg.meetingID);
		if (m != null) {
			log.debug("Meeting [{}] , [{}] ended.", m.meetingID, m.meetingName);
		}
	}
	

	public void setMessagingService(MessagingService messagingService) {
		this.messagingService = messagingService;
		this.messagingService.addListener(new RoomsManagerListener());
		this.messagingService.start();
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
//			Meeting room = getMeeting(meetingId); // must do this because the room coming in is serialized (no transient values are present)
//			if (room != null)
//				room.endAndKickAll();
//			else
//				log.debug("Could not find room " + meetingId);
		}
		
		@Override
		public void presentationUpdates(HashMap<String, String> map) {
			conversionUpdatesMessageListener.handleReceivedMessage(map);
		}
		
	}
	
}
