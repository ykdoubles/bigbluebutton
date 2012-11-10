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
import org.bigbluebutton.conference.messages.in.MeetingEnd;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.messages.in.UsersQuery;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.bigbluebutton.conference.messages.out.UsersQueryReply;
import org.bigbluebutton.conference.vo.StatusVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.logging.Red5LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Meeting {
	private static Logger log = Red5LoggerFactory.getLogger(Meeting.class, "bigbluebutton");	

	private String meetingID;
	private UsersManager usersMgr;
	private final IMessageOutGateway msgOutGW;
	
	public Meeting(String meetingID, IMessageOutGateway outGW) {
		this.meetingID = meetingID;
		msgOutGW = outGW;		
	}

	public String getMeetingID() {
		return meetingID;
	}

	public void processMessage(IMessageIn msg) {
		if (msg instanceof MeetingStart) {
			handleMeetingStart((MeetingStart) msg);
		} else if (msg instanceof MeetingEnd) {
			handleMeetingEnd((MeetingEnd) msg);
		} else if (msg instanceof UsersQuery) {
			handleUsersQuery((UsersQuery) msg);
		}
	}
	
	private void handleUsersQuery(UsersQuery msg) {
		Map<String, User> users = usersMgr.getUsers();
		
		Map<String, UserVO> pm = new HashMap<String, UserVO>();
		
		if (! users.isEmpty()) {
			Collection<User> uc = users.values();
	    	for (Iterator<User> it = uc.iterator(); it.hasNext();) {
	    		User ap = (User) it.next();
	    		
	    		Collection<StatusVO> svo = ap.getStatus();
	    		
	    		Map<String, StatusVO> s = new HashMap<String, StatusVO>();
	    			    		
	    		for (Iterator<StatusVO> it1 = svo.iterator(); it1.hasNext();) {
	    			StatusVO key = (StatusVO) it1.next();
	    			StatusVO zx = new StatusVO(key.name, key.value);
	    			s.put(zx.name, zx);
	    		}
	    		
	    		UserVO uvo = new UserVO(ap.internalUserID, ap.externalUserID, ap.role, ap.name, s.values());
	    		
	    		pm.put(uvo.internalUserID, uvo); 
	    	}			
		}
		
		    	
    	UsersQueryReply uqr = new UsersQueryReply(meetingID, msg.userID, pm.values());
    	msgOutGW.accept(uqr);
	}
	
	private void handleMeetingStart(MeetingStart msg) {
		msgOutGW.accept(new MeetingStarted(meetingID));
	}
	
	public void handleMeetingEnd(MeetingEnd msg) {
		
	}
	

	
	public void addUser(User user) {
		usersMgr.addUser(user);		
	}

	public void removeUser(String userID) {
		User p = usersMgr.removeUser(userID);
	}

	public void changeUserStatus(String userID, String status, Object value) {

		if (usersMgr.changeUserStatus(userID, status, value)) {
			User user = usersMgr.getUser(userID);

		}		
	}

	public void endAndKickAll() {

	}

	public Map<String, String> getCurrentPresenter() {		
		return usersMgr.getCurrentPresenter();
	}
	
	public void assignPresenter(String newPresenterUserID, String assignedByUserID) {
		if (usersMgr.assignPresenter(newPresenterUserID, assignedByUserID)) {		
			User user = usersMgr.getUser(newPresenterUserID);			
		}

	}
}