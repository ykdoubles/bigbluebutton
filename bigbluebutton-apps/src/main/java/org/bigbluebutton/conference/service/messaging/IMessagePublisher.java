package org.bigbluebutton.conference.service.messaging;

public interface IMessagePublisher {
	public boolean send(String channel, String message);
}
