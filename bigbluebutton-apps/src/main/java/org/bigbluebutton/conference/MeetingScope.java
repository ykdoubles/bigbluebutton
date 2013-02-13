package org.bigbluebutton.conference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bigbluebutton.conference.messages.BroadcastMessage;
import org.bigbluebutton.conference.messages.IMessage;
import org.bigbluebutton.conference.messages.UserMessage;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;

public class MeetingScope {
	private ISharedObject whiteboardSO;
	private ISharedObject presentationSO;
	
	private final IScope scope;
	private final String meetingID;
	
	private final Map<String, IConnection> conns;

	public MeetingScope(String meetingID, IScope scope, ISharedObject whiteBoardSO, ISharedObject presentationSO) {
		this.meetingID = meetingID;
		this.scope = scope;
		this.whiteboardSO = whiteBoardSO;
		this.presentationSO = presentationSO;
		
		conns = new ConcurrentHashMap<String, IConnection>();
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	private void sendMessageToUser(UserMessage message) {
		IConnection conn = conns.get(message.getDest());
		if (conn != null) {
			if (conn.isConnected()) {
				List<Object> params = new ArrayList<Object>();
				params.add(message.getMessageName());
				params.add(message.getMessage());
				ServiceUtils.invokeOnConnection(conn, "onMessageFromServer", params.toArray());
			}
		}
	}
	
	private void sendMessageUsingSharedObject(IMessage message) {
		whiteboardSO.sendMessage("logout", new ArrayList());
	}
	
	private void broadcastMessage(BroadcastMessage message) {
		List<Object> params = new ArrayList<Object>();
		params.add(message.getMessageName());
		params.add(message.getMessage());
		ServiceUtils.invokeOnAllConnections(scope, "onMessageFromServer", params.toArray());				
	}
	
	public void addConnection(String userID, IConnection conn) {
		conns.put(userID, conn);
	}
	
	public void removeConnection(String userID) {
		conns.remove(userID);
	}
	
	public void sendMessage(IMessage message) {
		if (message instanceof BroadcastMessage) {
			broadcastMessage((BroadcastMessage)message);
		} else if (message instanceof UserMessage){
			sendMessageToUser((UserMessage) message);
		} else {
			sendMessageUsingSharedObject(message);
		}
	}
}
