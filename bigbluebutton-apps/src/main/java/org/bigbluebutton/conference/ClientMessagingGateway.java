package org.bigbluebutton.conference;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.bigbluebutton.conference.messages.ClientMessage;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

public class ClientMessagingGateway implements IClientMessagingGateway {
	private static final int NTHREADS = 1;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	private final Map<String, MeetingScope> meetingScopes;

	private BlockingQueue<ClientMessage> messages;
	private volatile boolean send = false;
	private Runnable sender;
	
	public ClientMessagingGateway() {
		meetingScopes = new ConcurrentHashMap<String, MeetingScope>();
		messages = new LinkedBlockingQueue<ClientMessage>();
	}
	
	@Override
	public void addMeetingScope(String meetingID, IScope scope) {
		MeetingScope meeting = new MeetingScope(meetingID, scope);
		meetingScopes.put(meetingID, meeting);
	}

	@Override
	public void addUserConnection(String meetingID, IConnection conn, String userID) {

	}
	
	public void removeMeetingScope(String meetingID) {
		
	}
	
	public void removeUserConnection(String meetingID, String userID) {
		
	}
	
	public void sendMessage(ClientMessage message) {
		
	}
	
	private void sendMessageToClient(ClientMessage message) {
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
					ClientMessage msg;
					try {
						msg = messages.take();
						sendMessageToClient(msg);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
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
