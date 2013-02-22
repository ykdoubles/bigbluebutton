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
*
*/
package org.bigbluebutton.conference.service.voice;
import org.slf4j.Logger;import org.red5.server.api.Red5;import org.red5.server.api.scope.IScope;
import org.bigbluebutton.conference.BigBlueButtonSession;import org.bigbluebutton.conference.Constants;import org.bigbluebutton.conference.IBigBlueButtonGateway;
import org.red5.logging.Red5LoggerFactory;
import org.bigbluebutton.webconference.voice.ConferenceService;import java.util.ArrayList;import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bigbluebutton.webconference.voice.Participant;
public class VoiceService {
	
	private static Logger log = Red5LoggerFactory.getLogger( VoiceService.class, "bigbluebutton" );
	
	private IBigBlueButtonGateway bbbGW;
	
	public void sendVoiceUsers() {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		String requesterID = getMyUserId();
		bbbGW.sendVoiceUsers(meetingID, requesterID);
	}
	
	private Map<Integer, Map> arrayListToMap(ArrayList<Participant> alp) {
		log.debug("Converting arraylist to Map " + alp.size());
		Map<Integer, Map> result = new HashMap();
		
		for (Participant p : alp) {
			Map<String, Object> pmap = new HashMap();
			pmap.put("participant", p.getId());
			pmap.put("name", p.getName());
			pmap.put("muted", p.isMuted());
			pmap.put("talking", p.isTalking());
			pmap.put("locked", p.isMuteLocked());
			log.debug("[" + p.getId() + "," + p.getName() + "," + p.isMuted() + "," + p.isTalking() + "]");
			result.put(p.getId(), pmap);
		}
		
		return result;
	}
	
	public void muteAllUsers(boolean mute) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
    	bbbGW.muteAll(meetingID, mute);	   	
	}	
	
	public void isRoomMuted(){
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		String requesterID = getMyUserId();
    	bbbGW.isRoomMuted(meetingID, requesterID);
	}
	
	public void muteUnmuteUser(String userID, Boolean mute) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
    	bbbGW.mute(meetingID, userID, mute);
	}

	public void lockMuteUser(String userID, Boolean lock) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		
    	bbbGW.lock(meetingID, userID.toString(), lock);
	}
	
	public void kickUSer(String userID) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
			
		bbbGW.eject(meetingID, userID.toString());
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
