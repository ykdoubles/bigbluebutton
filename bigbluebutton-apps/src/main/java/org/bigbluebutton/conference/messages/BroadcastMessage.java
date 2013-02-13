package org.bigbluebutton.conference.messages;

import java.util.Map;

public class BroadcastMessage implements IMessage {

	private final String dest;
	private final String messageName;
	private final Map<String, Object> message;
	
	public BroadcastMessage (String dest, String messageName, Map<String, Object> message) {
		this.dest = dest;
		this.messageName = messageName;
		this.message = message;
	}

	public String getDest() {
		return dest;
	}

	public String getMessageName() {
		return messageName;
	}

	public Map<String, Object> getMessage() {
		return message;
	}
}
