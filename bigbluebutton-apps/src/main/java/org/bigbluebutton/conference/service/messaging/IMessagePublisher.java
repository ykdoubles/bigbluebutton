package org.bigbluebutton.conference.service.messaging;

public interface IMessagePublisher {
	
	public void send(String channel, String message);
}
