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
import java.util.Map;
import org.bigbluebutton.conference.imp.MessageInGateway;
import org.bigbluebutton.conference.messages.in.AllMeetingsStop;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.messages.in.MeetingEnd;
import org.bigbluebutton.conference.messages.in.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.UserJoin;
import org.bigbluebutton.conference.messages.in.UserKick;
import org.bigbluebutton.conference.messages.in.UserLeave;
import org.bigbluebutton.conference.messages.in.UserStatusChange;
import org.bigbluebutton.conference.messages.in.UsersQuery;
import org.bigbluebutton.conference.vo.StatusVO;
import org.bigbluebutton.conference.vo.UserVO;

public class ParticipantsApplication {
	private static Logger log = Red5LoggerFactory.getLogger( ParticipantsApplication.class, "bigbluebutton" );	
		
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
			
	public void setParticipantStatus(String meetingID, String userid, String status, Object value) {
		messageInGW.accept(new UserStatusChange(meetingID, userid, status, value));
	}
	
	public void kickUser(String meetingID, String userID) {
		messageInGW.accept(new UserKick(meetingID, userID));		
	}
	
	public void getParticipants(String meetingID, String userID) {
		messageInGW.accept(new UsersQuery(meetingID, userID));
	}
	
	public void participantLeft(String meetingID, String userID) {
		messageInGW.accept(new UserLeave(meetingID, userID));
	}
	
	public void participantJoin(String meetingID, String userID, String username, String role, String externUserID, Map<String, StatusVO> status) {
		UserVO uvo = new UserVO(userID, externUserID, role, username);
		messageInGW.accept(new UserJoin(meetingID, uvo));
	}
		
	public void assignPresenter(String meetingID, String newPresenterUserID, String assignedByUserID){
		messageInGW.accept(new UserAssignPresenter(meetingID, newPresenterUserID, assignedByUserID));	
	}
		
	public void setMessageInGateway(MessageInGateway gw) {
		messageInGW = gw;
	}
}
