package org.bigbluebutton.conference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bigbluebutton.conference.messages.ClientMessage;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;

public class MeetingScope {
	private ConcurrentHashMap<String, ISharedObject> whiteboardSOs;
	private ConcurrentHashMap<String, ISharedObject> presentationSOs;
	
	private final IScope scope;
	private final String meetingID;
	
	private final Map<String, IConnection> conns;

	public MeetingScope(String meetingID, IScope scope) {
		this.meetingID = meetingID;
		this.scope = scope;
		conns = new ConcurrentHashMap<String, IConnection>();
		whiteboardSOs = new ConcurrentHashMap<String, ISharedObject>();
		presentationSOs = new ConcurrentHashMap<String, ISharedObject>();
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public void broadcastMessage(ClientMessage message) {
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
	
	public void sendMessage(ClientMessage message) {
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
}
