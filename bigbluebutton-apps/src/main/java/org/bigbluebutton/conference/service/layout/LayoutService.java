/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
* 
* Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 3.0 of the License, or (at your option) any later
* version.
* 
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* Author: Felipe Cecagno <felipe@mconf.org>
*/
package org.bigbluebutton.conference.service.layout;

import org.bigbluebutton.conference.BigBlueButtonSession;
import org.bigbluebutton.conference.Constants;
import org.bigbluebutton.conference.IBigBlueButtonGateway;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.Red5;
import org.slf4j.Logger;

public class LayoutService {
	
	private static Logger log = Red5LoggerFactory.getLogger( LayoutService.class, "bigbluebutton" );
	
	private IBigBlueButtonGateway bbbGW;

	public void getCurrentLayout() {
		log.debug("getCurrentLayout");
		String meetingID = Red5.getConnectionLocal().getScope().getName();
		bbbGW.sendCurrentLayout(meetingID, getMyUserId());
	}
	
	public void lock(String userID, String layoutID) {
		log.debug("Layout locked");
		String meetingID = Red5.getConnectionLocal().getScope().getName();
		bbbGW.lockLayout(meetingID, userID, layoutID);
	}
	
	public void unlock() {
		log.debug("Layout unlocked");
		String meetingID = Red5.getConnectionLocal().getScope().getName();
		bbbGW.unlockLayout(meetingID);
	}
	
	public void setBigBlueButtonGateway(IBigBlueButtonGateway bbbGW) {
		this.bbbGW = bbbGW;
	}
	
	public String getMyUserId() {
		BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
		assert bbbSession != null;
		return bbbSession.getInternalUserID();
	}
}
