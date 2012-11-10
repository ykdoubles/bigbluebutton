package org.bigbluebutton.conference.imp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.bigbluebutton.conference.IMessageInGateway;
import org.bigbluebutton.conference.MeetingsManager;
import org.bigbluebutton.conference.messages.in.IMessageIn;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class MessageInGateway implements IMessageInGateway {
	private static Logger log = Red5LoggerFactory.getLogger(MessageInGateway.class, "bigbluebutton");
	
	private static final Executor exec = Executors.newSingleThreadExecutor();	
	private static final BlockingQueue<IMessageIn> messages = new LinkedBlockingQueue<IMessageIn>();
	
	private MeetingsManager meetingsManager;
	private volatile boolean sendMessage = false;
	
	public void accept(final IMessageIn message) {
		try {
			messages.put(message);
		} catch (InterruptedException e) {
			log.error("Interrupted exception while accepting messages.");
		}
	}
	
	public void stop() {
		log.info("Stopping Message IN Gateway");
		
		sendMessage = false;
	}
	
	public void start() {
		log.info("Starting Message IN Gateway");
		
		sendMessage = true;
		
		Runnable sender = new Runnable() {
			public void run() {
				while (sendMessage) {
					try {
						IMessageIn m = messages.take();
						meetingsManager.accept(m);
					} catch (InterruptedException e) {
						log.error("Interrupted exception while sending messages.");
					}					
				}
			}
		};
		exec.execute(sender);
	}
	
	public void setMeetingsManager(MeetingsManager m) {
		meetingsManager = m;
	}
	
}
