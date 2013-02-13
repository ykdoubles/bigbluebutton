package org.bigbluebutton.conference.messages;

import org.red5.server.api.IConnection;

public class AddConnectionMessage implements IMessage {
	private final String meetingID;
	private final String userID;
	private final IConnection conn;
	
	public AddConnectionMessage(String meetingID, String userID, IConnection conn) {
		this.meetingID = meetingID;
		this.userID = userID;
		this.conn = conn;
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public IConnection getConnection() {
		return conn;
	}
}
