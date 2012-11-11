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
import org.bigbluebutton.conference.exceptions.PresenterChangeException;
import org.bigbluebutton.conference.exceptions.SamePresenterChangeException;
import org.bigbluebutton.conference.messages.in.IMessageIn;
import org.bigbluebutton.conference.messages.in.MeetingEnd;
import org.bigbluebutton.conference.messages.in.MeetingForceEnd;
import org.bigbluebutton.conference.messages.in.MeetingStart;
import org.bigbluebutton.conference.messages.in.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.UserHandStatusChange;
import org.bigbluebutton.conference.messages.in.UserJoin;
import org.bigbluebutton.conference.messages.in.UserLeave;
import org.bigbluebutton.conference.messages.in.UserVideoStatusChange;
import org.bigbluebutton.conference.messages.in.UserVoiceStatusChange;
import org.bigbluebutton.conference.messages.in.UsersQuery;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.bigbluebutton.conference.messages.out.UserHandStatusChanged;
import org.bigbluebutton.conference.messages.out.UserJoined;
import org.bigbluebutton.conference.messages.out.UserKicked;
import org.bigbluebutton.conference.messages.out.UserLeft;
import org.bigbluebutton.conference.messages.out.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.UserVoiceStatusChanged;
import org.bigbluebutton.conference.messages.out.UsersQueryReply;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.StatusVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.logging.Red5LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Meeting {
	private static Logger log = Red5LoggerFactory.getLogger(Meeting.class, "bigbluebutton");	

	public final String meetingID;
	public final String meetingName;
	
	private final UsersManager usersMgr = new UsersManager();
	private final IMessageOutGateway msgOutGW;
	
	private boolean hasEnded = false;
	
	public Meeting(String meetingID, String meetingName, IMessageOutGateway outGW) {
		this.meetingID = meetingID;
		this.meetingName = meetingName;
		msgOutGW = outGW;		
	}

	public void processMessage(IMessageIn msg) {
		if (msg instanceof MeetingStart) {
			handleMeetingStart((MeetingStart) msg);
		} else if (msg instanceof UsersQuery) {
			handleUsersQuery((UsersQuery) msg);
		} else if (msg instanceof UserAssignPresenter) {
			handleUserAssignPresenter((UserAssignPresenter) msg);
		} else if (msg instanceof UserJoin) {
			handleUserJoin((UserJoin) msg);
		} else if (msg instanceof UserLeave) {
			handleUserLeave((UserLeave) msg);
		} else if (msg instanceof MeetingForceEnd) {
			handleMeetingForceEnd((MeetingForceEnd) msg);
		} else if (msg instanceof UserHandStatusChange) {
			handleUserHandStatusChange((UserHandStatusChange) msg);
		} else if (msg instanceof UserVoiceStatusChange) {
			handleUserVoiceStatusChange((UserVoiceStatusChange) msg);
		} else if (msg instanceof UserVideoStatusChange) {
			handleUserVideoStatusChange((UserVideoStatusChange) msg);
		}
	}
	
	private void handleUserHandStatusChange(UserHandStatusChange msg) {
		if (log.isDebugEnabled()) {
			log.debug("Handling users hand for meeting [{}] [{}]", meetingID, meetingName);
		}	

		if (usersMgr.raiseHand(msg.userID, msg.raised)) {
			msgOutGW.accept(new UserHandStatusChanged(meetingID, msg.userID, msg.raised, msg.setByUserID));
		}
	}
	
	private void handleUserVoiceStatusChange(UserVoiceStatusChange msg) {
		if (log.isDebugEnabled()) {
			log.debug("Handling users voice for meeting [{}] [{}]", meetingID, meetingName);
		}		
		
		if (usersMgr.hasAudio(msg.userID, msg.hasVoice, msg.streamName)) {
			msgOutGW.accept(new UserVoiceStatusChanged(meetingID, msg.userID, msg.hasVoice, msg.streamName));
		}		
	}
	
	private void handleUserVideoStatusChange(UserVideoStatusChange msg) {
		if (log.isDebugEnabled()) {
			log.debug("Handling users video for meeting [{}] [{}]", meetingID, meetingName);
		}	
		
		if (usersMgr.hasVideo(msg.userID, msg.hasVideo, msg.streamName)) {
			msgOutGW.accept(new UserVoiceStatusChanged(meetingID, msg.userID, msg.hasVideo, msg.streamName));
		}	
	}
	
	private void handleUsersQuery(UsersQuery msg) {
		if (log.isDebugEnabled()) {
			log.debug("Handling users query for meeting [{}] [{}]", meetingID, meetingName);
		}
		
		Collection<UserVO> pm =  usersMgr.getUsers();
    	UsersQueryReply uqr = new UsersQueryReply(meetingID, msg.userID, pm);
    	msgOutGW.accept(uqr);
	}
	
	private void handleMeetingStart(MeetingStart msg) {
		if (log.isDebugEnabled()) {
			log.debug("Starting meeting [{}] [{}]", meetingID, meetingName);
		}
		
		msgOutGW.accept(new MeetingStarted(meetingID));
	}
	
	private void handleMeetingForceEnd(MeetingForceEnd msg) {
		if (log.isDebugEnabled()) {
			log.debug("Request to end meeting [{}] [{}]. Kicking everyone out.", meetingID, meetingName);
		}
		
		// Flag meeting as ended.
		hasEnded = true;
		
		// Kick all users.
		Collection<UserVO> us = usersMgr.getUsers();
		for (Iterator<UserVO> it = us.iterator(); it.hasNext(); ) {
			UserVO part = it.next();
			msgOutGW.accept(new UserKicked(meetingID, part.intUserID));
		}		
	}
	
	private void handleUserJoin(UserJoin msg) {
		
		if (hasEnded) {
			// Meeting has ended. Reject.
			return;
		}
		
		UserVO uvo = usersMgr.addUser(msg.user);	
		
		if (log.isDebugEnabled()) {
			log.debug("User [{}, {}] has joined [{}]", new Object [] {uvo.intUserID, uvo.name, meetingName});
		}
		
		msgOutGW.accept(new UserJoined(meetingID, uvo));		
		makeSomebodyPresenter();
	}
	
	private void makeSomebodyPresenter() {
		if (! usersMgr.hasPresenter() && usersMgr.getNumModerators() > 0) {
			NewPresenterVO np = usersMgr.makeAModeratorPresenter();
			if (np != null) {
				
				if (log.isDebugEnabled()) {
					log.debug("Making [{}, {}]", np.newPresenterUserID, np.newPresenterName);	
				}
				
				msgOutGW.accept(new UserPresenterChanged(meetingID, np));
			}
		}
	}
	
	private void handleUserLeave(UserLeave msg) {		
		UserVO p = usersMgr.removeUser(msg.userID);	
		
		if (p != null) {
			if (log.isDebugEnabled()) {
				log.debug("User [{}] is leaving [{}]", p.name, meetingName);	
			}
			msgOutGW.accept(new UserLeft(meetingID, p.intUserID));			
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Cannot find user [{}] is leaving [{}]", msg.userID, meetingName);	
			}			
		}
		
		if (!hasEnded) {
			// The meeting has not ended. So try making a moderator presenter.
			makeSomebodyPresenter();
		}		
		
	}

	private void handleUserAssignPresenter(UserAssignPresenter msg) {
		try {
			NewPresenterVO npv = usersMgr.makePresenter(msg.newPresenterUserID, true, msg.assignedByUserID);
			msgOutGW.accept(new UserPresenterChanged(meetingID, npv));
		} catch (SamePresenterChangeException e) {
			log.info(e.getMessage());
		} catch (PresenterChangeException e) {
			log.error(e.getMessage());
		}		

	}
}