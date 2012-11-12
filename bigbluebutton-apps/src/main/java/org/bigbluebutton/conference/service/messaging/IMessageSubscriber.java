package org.bigbluebutton.conference.service.messaging;

/**
 * Receive messages from Redis. Make sure that the implementer of this
 * interface doesn't block and prevent receiving messages.
 * 
 * @author ritzalam
 *
 */
public interface IMessageSubscriber {
	void receive(RedisMessage message);
}
