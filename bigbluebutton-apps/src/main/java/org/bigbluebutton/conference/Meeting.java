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
import org.bigbluebutton.conference.messages.in.chat.PublicChatHistoryQuery;
import org.bigbluebutton.conference.messages.in.chat.PublicChatMessageSend;
import org.bigbluebutton.conference.messages.in.meetings.MeetingForceEnd;
import org.bigbluebutton.conference.messages.in.meetings.MeetingStart;
import org.bigbluebutton.conference.messages.in.presentation.PresentationRemove;
import org.bigbluebutton.conference.messages.in.presentation.PresentationShare;
import org.bigbluebutton.conference.messages.in.presentation.PresentationSlideChange;
import org.bigbluebutton.conference.messages.in.users.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.users.UserHandStatusChange;
import org.bigbluebutton.conference.messages.in.users.UserJoin;
import org.bigbluebutton.conference.messages.in.users.UserLeave;
import org.bigbluebutton.conference.messages.in.users.UserVideoStatusChange;
import org.bigbluebutton.conference.messages.in.users.UserVoiceStatusChange;
import org.bigbluebutton.conference.messages.in.users.UsersQuery;
import org.bigbluebutton.conference.messages.out.chat.PublicChatHistoryQueryReply;
import org.bigbluebutton.conference.messages.out.chat.PublicChatMessageSent;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.presentation.PresentationRemoved;
import org.bigbluebutton.conference.messages.out.presentation.PresentationShared;
import org.bigbluebutton.conference.messages.out.presentation.PresentationSlideChanged;
import org.bigbluebutton.conference.messages.out.users.UserHandStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UserVideoStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UserVoiceStatusChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;
import org.bigbluebutton.conference.service.chat.ChatManager;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.UserVO;
import org.red5.logging.Red5LoggerFactory;
import java.util.Collection;
import java.util.Iterator;

public class Meeting {
	private static Logger log = Red5LoggerFactory.getLogger(Meeting.class, "bigbluebutton");	

	public final String meetingID;
	public final String meetingName;
	
	private final UsersManager usersMgr = new UsersManager();
	private final ChatManager chatMgr = new ChatManager();
	private final IMessageOutGateway msgOutGW;
	
	private boolean hasEnded = false;
	
	public Meeting(String meetingID, String meetingName, IMessageOutGateway outGW) {
		if(outGW == null)
			throw new IllegalArgumentException();
		
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
		//Chat Handlers
		else if(msg instanceof PublicChatMessageSend){
			handlePublicChatMessageSend((PublicChatMessageSend) msg);
		} else if (msg instanceof PublicChatHistoryQuery){
			handlePublicChatHistoryQuery((PublicChatHistoryQuery) msg);
		}
		//Presentation Handlers
		else if(msg instanceof PresentationShare){
			handlePresentationShare((PresentationShare) msg);
		}
		else if(msg instanceof PresentationRemove){
			handlePresentationRemove((PresentationRemove) msg);
		}
		else if(msg instanceof PresentationSlideChange){
			handlePresentationSlideChange((PresentationSlideChange) msg);
		}
	}
	
	private void handlePresentationSlideChange(PresentationSlideChange msg) {
		if(log.isDebugEnabled()){
			log.debug("Handling slide change for meeting [{}]",msg.meetingID);
		}
		msgOutGW.accept(new PresentationSlideChanged(msg.meetingID, msg.slideNum));
	}

	private void handlePresentationRemove(PresentationRemove msg) {
		if(log.isDebugEnabled()){
			log.debug("Handling presentation [{}] share for meeting [{}]",msg.presentationName ,msg.meetingID);
		}
		msgOutGW.accept(new PresentationRemoved(msg.meetingID, msg.presentationName));
	}

	private void handlePresentationShare(PresentationShare msg) {
		if(log.isDebugEnabled()){
			log.debug("Handling presentation [{}] share for meeting [{}]",msg.presentationName ,msg.meetingID);
		}
		msgOutGW.accept(new PresentationShared(msg.meetingID, msg.presentationName, msg.share));
	}

	private void handlePublicChatHistoryQuery(PublicChatHistoryQuery msg) {
		if(log.isDebugEnabled()){
			log.debug("Handling public chat query for meeting [{}] from [{}]",msg.meetingID ,msg.userID);
		}
		msgOutGW.accept(new PublicChatHistoryQueryReply(msg.meetingID, msg.userID, chatMgr.getChatMessages()));
	}

	private void handlePublicChatMessageSend(PublicChatMessageSend msg) {
		if(log.isDebugEnabled()){
			log.debug("Handling public chat message for meeting [{}] from [{}]",msg.meetingID,msg.chatVO.fromUsername);
		}
		chatMgr.addChatMessage(msg.chatVO);
		msgOutGW.accept(new PublicChatMessageSent(msg.meetingID, msg.chatVO));
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
			msgOutGW.accept(new UserVideoStatusChanged(meetingID, msg.userID, msg.hasVideo, msg.streamName));
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
		
		//TODO: This method has been changed to just use the userVO sent from the UserJoin Msg
		//UserVO uvo = usersMgr.addUser(msg.user);
		if(!usersMgr.addUser(msg.user)){
			log.debug("User already exists");
		}
		
		if (log.isDebugEnabled()) {
			log.debug("User [{}, {}] has joined [{}]", new Object [] {msg.user.intUserID, msg.user.name, meetingName});
		}
		
		msgOutGW.accept(new UserJoined(meetingID, msg.user));		
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