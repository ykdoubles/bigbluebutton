package org.bigbluebutton.conference.service.users;

import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.meetings.MeetingEnded;
import org.bigbluebutton.conference.messages.out.meetings.MeetingStarted;
import org.bigbluebutton.conference.messages.out.users.UserJoined;
import org.bigbluebutton.conference.messages.out.users.UserKicked;
import org.bigbluebutton.conference.messages.out.users.UserLeft;
import org.bigbluebutton.conference.messages.out.users.UserPresenterChanged;
import org.bigbluebutton.conference.messages.out.users.UsersQueryReply;

public class UsersEventRecorder implements IMessageOutListener {

	@Override
	public void accept(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			
		} else if (message instanceof MeetingEnded) {
			handleMeetingEnded((MeetingEnded) message);
		} else if (message instanceof UserPresenterChanged) {
			handleUserPresenterChanged((UserPresenterChanged) message);
		} else if (message instanceof UserKicked) {
			handleUserKicked((UserKicked) message);
		} else if (message instanceof UserJoined) {
			handleUserJoined((UserJoined) message);
		} else if (message instanceof UserLeft) {
			handleUserLeft((UserLeft) message);
		} else if (message instanceof UsersQueryReply) {
			handleUsersQueryReply((UsersQueryReply) message);
		}
	}

	private void handleUsersQueryReply(UsersQueryReply message) {
		// TODO Auto-generated method stub
		
	}

	private void handleUserLeft(UserLeft message) {
		// TODO Auto-generated method stub
		
	}

	private void handleUserJoined(UserJoined message) {
		// TODO Auto-generated method stub
		
	}

	private void handleUserKicked(UserKicked message) {
		// TODO Auto-generated method stub
		
	}

	private void handleUserPresenterChanged(UserPresenterChanged message) {
		// TODO Auto-generated method stub
		
	}

	private void handleMeetingEnded(MeetingEnded message) {
		// TODO Auto-generated method stub
		
	}

}
