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
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.red5.logging.Red5LoggerFactory;
import net.jcip.annotations.ThreadSafe;
import java.util.Map;
/**
 * Contains information about a Room and it's Participants. 
 * Encapsulates Participants and RoomListeners.
 */
@ThreadSafe
public class Meeting {
	private static Logger log = Red5LoggerFactory.getLogger( Meeting.class, "bigbluebutton" );	

	private String meetingID;
	private UsersManager users;

	private final MessageOutGateway msgOutGW;
	
	public Meeting(String meetingID, MessageOutGateway outGW) {
		this.meetingID = meetingID;
		msgOutGW = outGW;
		
	}

	public String getMeetingID() {
		return meetingID;
	}

	public void start() {
		msgOutGW.accept(new MeetingStarted(meetingID));
	}
	
	public void end() {
		
	}
	
	public void addUser(User user) {
		users.addUser(user);
		
	}

	public void removeUser(String userID) {

		User p = users.removeUser(userID);

	}

	public void changeUserStatus(String userID, String status, Object value) {

		if (users.changeUserStatus(userID, status, value)) {
			User user = users.getUser(userID);

		}		
	}

	public void endAndKickAll() {

	}

	public Map<String, String> getCurrentPresenter() {		
		return users.getCurrentPresenter();
	}
	
	public void assignPresenter(String newPresenterUserID, String assignedByUserID) {
		if (users.assignPresenter(newPresenterUserID, assignedByUserID)) {		
			User user = users.getUser(newPresenterUserID);			
		}

	}
}