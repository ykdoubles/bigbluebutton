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
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.bigbluebutton.conference.BigBlueButtonSession;
import org.bigbluebutton.conference.Constants;


public class ParticipantsService {

	private static Logger log = Red5LoggerFactory.getLogger( ParticipantsService.class, "bigbluebutton" );	
	private ParticipantsApplication application;

	public void assignPresenter(String newPresenterUserID, String assignedBy) {
		IScope scope = Red5.getConnectionLocal().getScope();
		application.assignPresenter(scope.getName(), newPresenterUserID, assignedBy);
	}
	
	public void getParticipants() {		
		String meetingID = Red5.getConnectionLocal().getScope().getName();
		application.getParticipants(meetingID, getBbbSession().getInternalUserID());		
	}
	
	public void setParticipantStatus(String userid, String status, Object value) {
		String roomName = Red5.getConnectionLocal().getScope().getName();
		log.debug("Setting participant status " + roomName + " " + userid + " " + status + " " + value);
		application.setParticipantStatus(roomName, userid, status, value);
	}
	
	public void setParticipantsApplication(ParticipantsApplication a) {
		log.debug("Setting Participants Applications");
		application = a;
	}
	
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
}
