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

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.freeswitch.esl.client.manager.ManagerConnection;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;

/**
 *
 * @author leif
 */
public class FreeswitchHeartbeatMonitor implements IHearbeatListener {
    private static Logger log = Red5LoggerFactory.getLogger(FreeswitchHeartbeatMonitor.class, "bigbluebutton");

    public static final String EVENT_HEARTBEAT = "HEARTBEAT";

    private final Executor exec = Executors.newSingleThreadExecutor();
    private Runnable monitorProcess;
    private volatile boolean running = false;

    private static final long DEFAULT_INTERVAL = 20 * 1000L;
    private long interval = DEFAULT_INTERVAL;
    private long lastHeartbeat = System.currentTimeMillis();
    private long maxHeartbeatInterval = 25 * 1000L;
    private int intervalLoopCount = 0;
    
    private final ManagerConnection connection;
    private final FreeswitchApplication eventListner;

    public FreeswitchHeartbeatMonitor(ManagerConnection connection, FreeswitchApplication eventListner) {
    	this.connection = connection;
        this.eventListner = eventListner;
    	log.info("Freeswitch HeartbeatMonitor Created");
    }

    public void start() {
        log.info("HeartbeatMonitor Starting");
        if (!running) {
            running = true;
            monitorProcess = new Runnable() {
                public void run() {
                    monitorFreeswitchServer();
                }
            };
            exec.execute(monitorProcess);
        }	   
    }

    public void stop() {
        running = false;
    }

    private void monitorFreeswitchServer() {
        long timeDiff;
        
        while(running) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) { }

            if(!running) {
                break;
            }

            //Check to see if update happened recently
            timeDiff = System.currentTimeMillis() - lastHeartbeat;
            if(timeDiff > maxHeartbeatInterval) {
                //if not reconnect
                try {
                    Client c = connection.getESLClient();
                    log.info("HeartbeatMonitor did not get a heartbeat event in time... reconnecting");
                    if(c.canSend()) { //Otherwise disconnect will throw ISE
                        log.info("Logging off fom [" + connection.toString() + "]");
                        connection.disconnect();
                    }
                    log.info("Logging in as [" + connection.getPassword() + "] to [" + connection.getHostname() + ":" + connection.getPort() + "]");
                    try {
                        connection.connect();
                        eventListner.startup(); //Re-call startup to setup eventListner and filters...
                        lastHeartbeat = System.currentTimeMillis(); //Reset 
                    } catch ( InboundConnectionFailure ce ) {
                        log.error( "HeartbeatMonitor Connect to FreeSwitch ESL socket failed, will retry...", ce );
                    }
                } catch (IllegalStateException ise) {
                    log.error( "HeartbeatMonitor Ex", ise);
                } catch (Exception e) {
                    log.error( "HeartbeatMonitor Ex", e);
                }
            }

            if(intervalLoopCount == 5) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lastHeartbeat);

                log.debug("HeartbeatMonitor running... last HB [{}] diff [{}]", cal.getTime(), timeDiff);
                intervalLoopCount = 0;
            }
            intervalLoopCount++;
        }
    }

    public void heartbeatReceived() {
            lastHeartbeat = System.currentTimeMillis();
    }
}
