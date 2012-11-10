package org.bigbluebutton.conference.imp;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.bigbluebutton.conference.IMessageOutGateway;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class MessageOutGateway implements IMessageOutGateway {
	private static Logger log = Red5LoggerFactory.getLogger(MessageOutGateway.class, "bigbluebutton");
	
	private static final Executor exec = Executors.newSingleThreadExecutor();	
	private static final BlockingQueue<IMessageOut> messages = new LinkedBlockingQueue<IMessageOut>();
	
	private Set<IMessageOutListener> listeners;
	
	private volatile boolean sendMessage = false;
	
	public void accept(final IMessageOut message) {
		try {
			messages.put(message);
		} catch (InterruptedException e) {
			log.error("Interrupted exception while accepting messages.");
		}
	}
	
	public void stop() {
		log.info("Stopping Message OUT Gateway");
		sendMessage = false;
	}
	
	public void start() {
		log.info("Starting Message OUT Gateway");
		
		sendMessage = true;
		
		Runnable sender = new Runnable() {
			public void run() {
				while (sendMessage) {					
					try {
						IMessageOut m = messages.take();
						sendMessage(m);
					} catch (InterruptedException e) {
						log.error("Interrupted exception while sending messages.");
					}					
				}
			}
		};
		exec.execute(sender);
	}
	
	private void sendMessage(IMessageOut m) {
		Iterator<IMessageOutListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			IMessageOutListener l = (IMessageOutListener) iter.next();
			l.accept(m);
		}
	}
	
	public void setMessageOutListeners(Set<IMessageOutListener> listeners) {
		this.listeners = listeners;
	}
}
