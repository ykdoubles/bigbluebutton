package org.bigbluebutton.conference;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.bigbluebutton.conference.messages.AddConnectionMessage;
import org.bigbluebutton.conference.messages.IMessage;
import org.bigbluebutton.conference.messages.UserMessage;
import org.red5.server.api.IConnection;

public class ClientMessagingGateway { //implements IClientMessagingGateway {
	private static final int NTHREADS = 1;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	private final Map<String, MeetingScope> meetingScopes;

	private BlockingQueue<IMessage> messages;
	private volatile boolean send = false;
	private Runnable sender;

	public ClientMessagingGateway() {
		meetingScopes = new ConcurrentHashMap<String, MeetingScope>();
		messages = new LinkedBlockingQueue<IMessage>();
	}
	
	public void removeMeetingScope(String meetingID) {
		meetingScopes.remove(meetingID);
	}
	
	public void removeUserConnection(String meetingID, String userID) {
		
	}
	
	public void sendMessage(IMessage message) {
		messages.offer(message);
	}
	
	private void sendMessageToClient(UserMessage message) {
		MeetingScope meeting = meetingScopes.get(message.getDest());
		if (meeting != null) {
			meeting.sendMessage(message);
		}
	}
	
	public void start() {
		send = true;
		sender = new Runnable() {
			public void run() {
				while (send) {
					IMessage msg;
					try {
						msg = messages.take();
						sendMessageToClient((UserMessage) msg);
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

}
