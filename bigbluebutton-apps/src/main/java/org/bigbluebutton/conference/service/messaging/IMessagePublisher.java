package org.bigbluebutton.conference.service.messaging;

public interface IMessagePublisher {
	public boolean send(RedisMessage message);
}
