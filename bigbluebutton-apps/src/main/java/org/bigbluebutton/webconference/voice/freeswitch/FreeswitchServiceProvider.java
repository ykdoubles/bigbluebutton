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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bigbluebutton.webconference.voice.commands.EjectVoiceUser;
import org.bigbluebutton.webconference.voice.commands.GetVoiceUsers;
import org.bigbluebutton.webconference.voice.commands.IVoiceCommand;
import org.bigbluebutton.webconference.voice.commands.MuteVoiceUser;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FreeswitchServiceProvider {
	private static Logger log = Red5LoggerFactory.getLogger(FreeswitchServiceProvider.class, "bigbluebutton");
	
	private static final int NTHREADS = 1;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	private BlockingQueue<IVoiceCommand> messages;
	private volatile boolean send = false;
	private Runnable sender;
	
	private FreeswitchApplication freeswitch;
	
	public void start() {
		send = true;
		sender = new Runnable() {
			public void run() {
				while (send) {
					IVoiceCommand msg;
					try {
						msg = messages.take();
						sendMessageServer(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		exec.execute(sender);
	}
	
	public void stop() {
		send = false;
	}
	
	public void sendMessage(IVoiceCommand message) {
		messages.offer(message);
	}
	
	private void sendMessageServer(IVoiceCommand msg) {
		if (msg instanceof GetVoiceUsers) {
			populateRoom((GetVoiceUsers) msg);
		} else if (msg instanceof EjectVoiceUser) {
			eject((EjectVoiceUser)msg);
		} else if (msg instanceof MuteVoiceUser) {
			
		}
	}
	
	private void record(String room, String meetingid){
    	freeswitch.record(room, meetingid);
    }

	private void broadcast(String room, String meetingid){
    	freeswitch.broadcast(room,meetingid);
    }
	
	private void eject(EjectVoiceUser msg) {
		//appDelegate.eject(room, participant);
	}

	private void mute(String room, Integer participant, Boolean mute) {
		freeswitch.mute(room, participant, mute);
	}

	private void populateRoom(GetVoiceUsers msg) {
		//appDelegate.populateRoom(room);
	}

	public void setFreeswitchApplication(FreeswitchApplication f) {
		freeswitch = f;		
    }

}
