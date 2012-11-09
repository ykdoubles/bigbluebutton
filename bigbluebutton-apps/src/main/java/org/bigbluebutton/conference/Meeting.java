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
import org.red5.logging.Red5LoggerFactory;
import net.jcip.annotations.ThreadSafe;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Contains information about a Room and it's Participants. 
 * Encapsulates Participants and RoomListeners.
 */
@ThreadSafe
public class Meeting {
	private static Logger log = Red5LoggerFactory.getLogger( Meeting.class, "bigbluebutton" );	
	String curPresenterUserID = "";
	String presenterAssignedBy = "";
	
	private String meetingID;
	private Map<String, User> users;

	private transient Map<String, User> unmodifiableMap;
	private transient final Map<String, IMeetingListener> listeners;

	public Meeting(String meetingID) {
		this.meetingID = meetingID;
		users = new ConcurrentHashMap<String, User>();
		unmodifiableMap = Collections.unmodifiableMap(users);
		listeners   = new ConcurrentHashMap<String, IMeetingListener>();
	}

	public String getMeetingID() {
		return meetingID;
	}

	public void addMeetingListener(IMeetingListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			log.debug("adding room listener");
			listeners.put(listener.getName(), listener);			
		}
	}

	public void removeMeetingListener(IMeetingListener listener) {
		log.debug("removing room listener");
		listeners.remove(listener);		
	}

	public void addUser(User participant) {
		synchronized (this) {
			log.debug("adding participant " + participant.getInternalUserID());
			users.put(participant.getInternalUserID(), participant);
//			unmodifiableMap = Collections.unmodifiableMap(participants)
		}
		log.debug("Informing roomlisteners " + listeners.size());
		for (Iterator<IMeetingListener> it = listeners.values().iterator(); it.hasNext();) {
			IMeetingListener listener = (IMeetingListener) it.next();
			log.debug("calling participantJoined on listener " + listener.getName());
			listener.participantJoined(participant);
		}
	}

	public void removeUser(String userid) {
		boolean present = false;
		User p = null;
		synchronized (this) {
			present = users.containsKey(userid);
			if (present) {
				log.debug("removing participant");
				p = users.remove(userid);
			}
		}
		if (present) {
			for (Iterator<IMeetingListener> it = listeners.values().iterator(); it.hasNext();) {
				IMeetingListener listener = (IMeetingListener) it.next();
				log.debug("calling participantLeft on listener " + listener.getName());
				listener.participantLeft(p);
			}
		}
	}

	public void changeUserStatus(String userid, String status, Object value) {
		boolean present = false;
		User p = null;
		synchronized (this) {
			present = users.containsKey(userid);
			if (present) {
				log.debug("change participant status");
				p = users.get(userid);
				p.setStatus(status, value);
				//participants.put(userid, p);
				//unmodifiableMap = Collections.unmodifiableMap(participants);
			}
		}
		if (present) {
			for (Iterator<IMeetingListener> it = listeners.values().iterator(); it.hasNext();) {
				IMeetingListener listener = (IMeetingListener) it.next();
				log.debug("calling participantStatusChange on listener " + listener.getName());
				listener.participantStatusChange(p, status, value);
			}
		}		
	}

	public void endAndKickAll() {
		for (Iterator<IMeetingListener> it = listeners.values().iterator(); it.hasNext();) {
			IMeetingListener listener = (IMeetingListener) it.next();
			log.debug("calling endAndKickAll on listener " + listener.getName());
			listener.endAndKickAll();
		}
	}

	public Map<String, User> getUsers() {
		return unmodifiableMap;
	}	

	public int getNumberOfModerators() {
		int sum = 0;
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isModerator()) {
				sum++;
			}
		} 
		log.debug("Returning number of moderators: " + sum);
		return sum;
	}

	public Map<String, String> getCurrentPresenter() {
		Map<String, String> curPres =  new HashMap<String, String>();
		curPres.put("presenterUserID", curPresenterUserID);
		curPres.put("assignedBy", presenterAssignedBy);
		curPres.put("presenterName", "");
		
		User user = getUser(curPresenterUserID);
		if (user != null) {
			curPres.put("presenterName", user.getName());
		}

		
		return curPres;
	}
	
	public void assignPresenter(String newPresenterUserID, String assignedByUserID) {
		User user = getUser(newPresenterUserID);
		if (user != null) {
			curPresenterUserID = newPresenterUserID;
			presenterAssignedBy = assignedByUserID;
			
			for (Iterator<IMeetingListener> iter = listeners.values().iterator(); iter.hasNext();) {
				IMeetingListener listener = (IMeetingListener) iter.next();
				listener.assignPresenter(newPresenterUserID, user.getName(), assignedByUserID);
			}				
		}

	}
	
	private User getUser(String userID) {
		return users.get(userID);
	}
}