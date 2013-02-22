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
package org.bigbluebutton.conference;

import org.red5.server.api.Red5;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.slf4j.Logger;

public class BigBlueButtonApplication extends MultiThreadedApplicationAdapter {
	private static Logger log = Red5LoggerFactory.getLogger(BigBlueButtonApplication.class, "bigbluebutton");

	private IBigBlueButtonGateway bbbGW;
	private IClientMessagingGateway clientGW;
	
	private static final String USERS_SO = "participantsSO"; 
	private static final String LAYOUT_SO = "layoutSO"; 
	private static final String POLL_SO = "pollSO"; 
	private static final String PRESENTATION_SO = "presentationSO"; 
	private static final String VOICE_SO = "meetMeUsersSO";
	
	@Override
    public boolean appStart(IScope app) {
        log.debug("Starting BigBlueButton "); 
        return super.appStart(app);
    }
        
	@Override
    public boolean roomStart(IScope meeting) {
		String meetingID = meeting.getName();
    	log.debug("Starting meeting [" + meetingID + "].");
    	
    	if (!hasSharedObject(meeting, USERS_SO)) {
    		if (! createSharedObject(meeting, USERS_SO, false)) {   
    			log.error("Failed to create users shared object for meeting id = [" + meetingID + "]"); 			
    		}    		
    	}  	
    	
    	if (!hasSharedObject(meeting, LAYOUT_SO)) {
    		if (! createSharedObject(meeting, LAYOUT_SO, false)) {   
    			log.error("Failed to create layout shared object for meeting id = [" + meetingID + "]"); 			
    		}    		
    	}  
    	
    	if (!hasSharedObject(meeting, POLL_SO)) {
    		if (! createSharedObject(meeting, POLL_SO, false)) {   
    			log.error("Failed to create poll shared object for meeting id = [" + meetingID + "]"); 			
    		}    		
    	}  
    	
    	if (!hasSharedObject(meeting, PRESENTATION_SO)) {
    		if (! createSharedObject(meeting, PRESENTATION_SO, false)) {   
    			log.error("Failed to create presentation shared object for meeting id = [" + meetingID + "]"); 			
    		}    		
    	}  
    	
    	if (!hasSharedObject(meeting, VOICE_SO)) {
    		if (! createSharedObject(meeting, VOICE_SO, false)) {   
    			log.error("Failed to create voice shared object for meeting id = [" + meetingID + "]"); 			
    		}    		
    	}  
    	    	
    	ISharedObject usersSO = getSharedObject(meeting, USERS_SO);
    	ISharedObject layoutSO = getSharedObject(meeting, LAYOUT_SO);
    	ISharedObject pollSO = getSharedObject(meeting, POLL_SO);
    	ISharedObject presentationSO = getSharedObject(meeting, PRESENTATION_SO);
    	ISharedObject voiceSO = getSharedObject(meeting, VOICE_SO);
    	
    	clientGW.addScope(meetingID, meeting, usersSO, layoutSO, pollSO, presentationSO, voiceSO);
    	    	
    	return super.roomStart(meeting);
    }	
	
	@Override
    public void roomStop(IScope meeting) {
		String meetingID = meeting.getName();
		
    	log.debug("Stopping meeting [" + meetingID + "].");
    	   	
    	bbbGW.endMeeting(meetingID);
    	clientGW.removeScope(meetingID);
    	
    	super.roomStop(meeting);
    }
    
	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {
        String remoteHost = Red5.getConnectionLocal().getRemoteAddress();
        int remotePort = Red5.getConnectionLocal().getRemotePort();
        String username = ((String) params[0]).toString();
        String role = ((String) params[1]).toString();
        String internalMeetingID = ((String)params[2]).toString();

        /*
         * Convert the id to Long because it gets converted to ascii decimal
         * equivalent (i.e. zero (0) becomes 48) if we don't.
         */
        long clientID = Long.parseLong(Red5.getConnectionLocal().getClient().getId());
        String meetingName = ((String)params[3]).toString();
        log.info("[clientid=" + clientID + "] connected from " + remoteHost + ":" + remotePort + ".");
        
        String voiceBridge = ((String) params[4]).toString();

		boolean recorded = (Boolean)params[5];
		log.debug("record value - [" + recorded + "]"); 

    	String externalUserID = ((String) params[6]).toString();
    	String internalUserID = ((String) params[7]).toString();
    	    				
    	BigBlueButtonSession bbbSession = new BigBlueButtonSession(internalMeetingID, meetingName, clientID, internalUserID,  username, role, 
    			internalMeetingID, internalMeetingID, voiceBridge, recorded, externalUserID);
        connection.setAttribute(Constants.SESSION, bbbSession);        
        
        String debugInfo = "internalUserID=" + internalUserID + ",username=" + username + ",role=" +  role + ",internalMeetingID=" + internalMeetingID + "," + 
        					"session=" + internalMeetingID + ",voiceConf=" + voiceBridge + ",room=" + internalMeetingID + ",externalUserid=" + externalUserID;
		log.debug("User [{}] connected to room [{}]", debugInfo, internalMeetingID); 
		
		bbbGW.createMeeting(internalMeetingID, meetingName, voiceBridge, recorded);
		
		bbbGW.joinUser(internalMeetingID, internalUserID, username, role, externalUserID, false, false, false);
		clientGW.addConnection(internalMeetingID, bbbSession.getInternalUserID(), connection);
		
        super.roomConnect(connection, params);
        
        
    	return true;
	}

	@Override
	public void roomDisconnect(IConnection conn) {

    	BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
    	clientGW.removeConnection(bbbSession.getMeetingID(), getBbbSession().getInternalUserID());
    	
		super.roomDisconnect(conn);
	}
	
	public String getMyUserId() {
		BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
		assert bbbSession != null;
		return bbbSession.getInternalUserID();
	}
				
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
	
	public void setBigBlueButtonGateway(IBigBlueButtonGateway bbbGW) {
		this.bbbGW = bbbGW;
	}
	
	public void setClientMessagingGateway(IClientMessagingGateway clientGW) {
		this.clientGW = clientGW;
	}
}
