package org.bigbluebutton.webconference.voice.freeswitch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bigbluebutton.conference.IBigBlueButtonGateway;
import org.bigbluebutton.webconference.voice.events.VoiceEvent;
import org.bigbluebutton.webconference.voice.events.VoiceEventListener;
import org.bigbluebutton.webconference.voice.events.VoiceRecordingStartedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserJoinedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserLeftEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserLockedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserMutedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserTalkingEvent;

public class VoiceEventHandler implements VoiceEventListener {
	private static final int NTHREADS = 1;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	private BlockingQueue<VoiceEvent> messages;
	private volatile boolean send = false;
	private Runnable sender;
	
	private IBigBlueButtonGateway bbbGW;
	
	@Override
	public void handleConferenceEvent(VoiceEvent event) {
		messages.offer(event);
	}
	
	public void start() {
		send = true;
		sender = new Runnable() {
			public void run() {
				while (send) {
					VoiceEvent msg;
					try {
						msg = messages.take();
						sendMessageToBBB(msg);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		exec.execute(sender);
	}
	
	private void sendMessageToBBB(VoiceEvent msg) {
		if (msg instanceof VoiceUserJoinedEvent) {
//			bbbGW.voiceUserJoined();
		} else if (msg instanceof VoiceUserLeftEvent) {
			
		} else if (msg instanceof VoiceUserLockedEvent) {
			
		} else if (msg instanceof VoiceUserMutedEvent) {
			
		} else if (msg instanceof VoiceUserTalkingEvent) {
			
		} else if (msg instanceof VoiceRecordingStartedEvent) {
			
		}
	}
	
	public void stop() {
		send = false;
	}
	
	public void setBigBlueButtonGateway(IBigBlueButtonGateway bbbGW) {
		this.bbbGW = bbbGW;
	}

}
