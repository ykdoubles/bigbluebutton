package org.bigbluebutton.conference.service.messaging;

public interface MessagingService {
	public void start();
	public void stop();
	public void send(String channel, String message);
	public void addListener(IMessageSubscriber listener);
	public void removeListener(IMessageSubscriber listener);
}
