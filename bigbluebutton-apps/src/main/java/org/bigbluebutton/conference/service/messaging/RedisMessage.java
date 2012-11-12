package org.bigbluebutton.conference.service.messaging;

import java.util.Map;

public class RedisMessage {
	public final String channel;
	public final Map<String, String> message;
	
	public RedisMessage(String channel, Map<String, String> message) {
		this.channel = channel;
		this.message = message;
	}
}