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
package org.bigbluebutton.webconference.voice.freeswitch;

import org.freeswitch.esl.client.inbound.InboundConnectionFailure;
import org.freeswitch.esl.client.manager.ManagerConnection;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class ConnectionManager {
    private static Logger log = Red5LoggerFactory.getLogger(ConnectionManager.class, "bigbluebutton");

    private ManagerConnection manager;
    private int connectFailedCount = 0;
    
    public void connect() {
    	try {
			manager.connect();
		} catch (InboundConnectionFailure e) {
			log.warn("Failed to establish an ESL connection");
			connectFailedCount++;
		}
    }
    
    private void scheduleRetry() {
    	
    }
    
    private void retry() {
    	connect();
    }
    
    public void setConnectionManager(ManagerConnection manager) {
    	this.manager = manager;
    }
}