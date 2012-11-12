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

package org.bigbluebutton.conference.service.users;

import java.util.Map;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.bigbluebutton.conference.BigBlueButton;
import org.bigbluebutton.conference.BigBlueButtonSession;
import org.bigbluebutton.conference.Constants;
import org.bigbluebutton.conference.messages.in.users.UserAssignPresenter;
import org.bigbluebutton.conference.messages.in.users.UserKick;
import org.bigbluebutton.conference.messages.in.users.UserVideoStatusChange;
import org.bigbluebutton.conference.messages.in.users.UsersQuery;

public class UsersService {
	private static Logger log = Red5LoggerFactory.getLogger( UsersService.class, "bigbluebutton" );	
	private BigBlueButton bbb;

	public void kickUser(Map<String, Object> message) {
		IScope scope = Red5.getConnectionLocal().getScope();
		bbb.accept(new UserKick(scope.getName(), message.get("userID").toString()));
	}
	
	public void assignPresenter(Map<String, Object> message) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String newPresenterUserID = message.get("newPresenterUserID").toString();
		String assignedBy = message.get("assigneByUserID").toString();
		
		bbb.accept(new UserAssignPresenter(scope.getName(), newPresenterUserID, assignedBy));
	}
	
	public void getParticipants() {		
		String meetingID = Red5.getConnectionLocal().getScope().getName();
	
		bbb.accept(new UsersQuery(meetingID, getBbbSession().getInternalUserID()));
	}
	
	public void setParticipantStatus(Map<String, Object> message) {
		String userID = message.get("userID").toString();
		String status = message.get("status").toString();
		String streamName = message.get("streamName").toString();
		
		String meetingID = Red5.getConnectionLocal().getScope().getName();
		
		if ("HASVIDEO".equals(status.toUpperCase())) {
			bbb.accept(new UserVideoStatusChange(meetingID, userID, true, streamName));
		} else {
			bbb.accept(new UserVideoStatusChange(meetingID, userID, false, streamName));
		}
	}
	
	public void setBigBlueButton(BigBlueButton bbb) {
		this.bbb = bbb;
	}
	
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
}
