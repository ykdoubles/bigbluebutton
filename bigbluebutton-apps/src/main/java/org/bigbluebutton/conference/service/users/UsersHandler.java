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

import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.Red5;
import org.bigbluebutton.conference.BigBlueButton;
import org.bigbluebutton.conference.BigBlueButtonSession;import org.bigbluebutton.conference.Constants;import org.bigbluebutton.conference.messages.in.users.UserJoin;
import org.bigbluebutton.conference.messages.in.users.UserLeave;
import org.bigbluebutton.conference.vo.UserVO;


public class UsersHandler extends ApplicationAdapter implements IApplication {
	private static Logger log = Red5LoggerFactory.getLogger( UsersHandler.class, "bigbluebutton" );

	private static final String APP = "PARTICIPANTS";

	private BigBlueButton bbb;


	public boolean roomJoin(IClient client, IScope scope) {
		log.debug(APP + ":roomJoin " + scope.getName() + " - " + scope.getParent().getName());
		BigBlueButtonSession bbbSession = getBbbSession();
		if (bbbSession != null) {
			String userid = bbbSession.getInternalUserID();
			String username = bbbSession.getUsername();
			String role = bbbSession.getRole();
			String room = bbbSession.getRoom();
			log.debug(APP + ":participantJoin - [" + room + "] [" + userid + ", " + username + ", " + role + "]");
			
			UserVO uvo = new UserVO(userid, username, role, bbbSession.getExternUserID());
			bbb.accept(new UserJoin(room, uvo));
			return true;
		}
		log.warn("Can't send user join as session is null.");
		return true;
	}

	public void roomLeave(IClient client, IScope scope) {
		log.debug(APP + ":roomLeave " + scope.getName());
		BigBlueButtonSession bbbSession = getBbbSession();
		if (bbbSession == null) {
			log.debug("roomLeave - session is null"); 
		} else {
			bbb.accept(new UserLeave(bbbSession.getSessionName(), bbbSession.getInternalUserID()));
		}		
	}
		
	public void setBigBlueButton(BigBlueButton a) {
		bbb = a;
	}
		
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
}
