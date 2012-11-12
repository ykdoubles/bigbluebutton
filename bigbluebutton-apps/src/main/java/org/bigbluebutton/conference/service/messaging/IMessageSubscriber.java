package org.bigbluebutton.conference.service.messaging;

import java.util.Map;

public interface IMessageSubscriber {
	void receive(String channel, Map<String, String> message);
}
