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
import org.bigbluebutton.conference.messages.in.IMessageIn;
import org.bigbluebutton.conference.messages.in.meetings.AllMeetingsStop;
import org.bigbluebutton.conference.messages.in.meetings.MeetingEnd;
import org.bigbluebutton.conference.messages.in.meetings.MeetingStart;
import org.red5.logging.Red5LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingsManager {
	private static Logger log = Red5LoggerFactory.getLogger(MeetingsManager.class, "bigbluebutton");
	
	private final Map <String, Meeting> meetings = new ConcurrentHashMap<String, Meeting>();

	private MessageOutGateway messageOutGW;
	
		
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
		
	public void setMessageOutGateway(MessageOutGateway gw) {
		messageOutGW = gw;
	}
	
}
