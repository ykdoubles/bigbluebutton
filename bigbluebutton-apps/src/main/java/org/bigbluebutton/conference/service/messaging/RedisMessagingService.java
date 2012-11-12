package org.bigbluebutton.conference.service.messaging;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisMessagingService implements IMessagePublisher {
	private static Logger log = Red5LoggerFactory.getLogger(RedisMessagingService.class, "bigbluebutton");
	
	private JedisPool redisPool;
	private final Executor receiveExec = Executors.newSingleThreadExecutor();
	private final Executor sendExec = Executors.newSingleThreadExecutor();
	private final Executor subscriberExec = Executors.newSingleThreadExecutor();
	
	private static final BlockingQueue<RedisMessage> messagesToSend = new LinkedBlockingQueue<RedisMessage>();
	private static final BlockingQueue<RedisMessage> messagesReceived = new LinkedBlockingQueue<RedisMessage>();
	
	private Runnable messageReceiver, messagePublisher, subscriberNotifier;
	private volatile boolean sendMessage = false;
	private volatile boolean notifySubscribers = false;
	private Set<IMessageSubscriber> subscribers;

	public void start() {
		log.debug("Starting redis pubsub...");		
		startMessageReceiver();
		startMessagePublisher();
		startSubscriberNotifier();
	}

	public void stop() {
		try {
			sendMessage = false;
			notifySubscribers = false;
			redisPool.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startMessagePublisher() {
		sendMessage = true;
		messagePublisher = new Runnable() {
			public void run() {
				while (sendMessage) {
					try {
						RedisMessage rm = messagesToSend.take();
						publishMessage(rm);
					} catch (InterruptedException e) {
						log.error("Interrupted Exception while trying to publish message.");
					}
				}
			}
		};
		sendExec.execute(messagePublisher);	
	}

	private void startSubscriberNotifier() {
		notifySubscribers = true;
		subscriberNotifier = new Runnable() {
			public void run() {
				while (notifySubscribers) {
					try {
						RedisMessage rm = messagesReceived.take();
						notifySubscribers(rm);
					} catch (InterruptedException e) {
						log.error("Interrupted Exception while trying to notify subscribers.");
					}
				}
			}
		};
		subscriberExec.execute(subscriberNotifier);	
	}
	
	private void startMessageReceiver() {
		final Jedis jedis = redisPool.getResource();

		messageReceiver = new Runnable() {
		    public void run() {
		    	jedis.psubscribe(new PubSubListener(), MessagingConstants.BIGBLUEBUTTON_PATTERN);       			
		    }
		};
		receiveExec.execute(messageReceiver);	
	}
	
	public boolean send(RedisMessage redisMessage) {
		return messagesToSend.offer(redisMessage);
	}
	
	private void publishMessage(RedisMessage message) {
		Jedis jedis = redisPool.getResource();
		try {
			Gson gson = new Gson();
			jedis.publish(message.channel, gson.toJson(message.message));
		} catch(Exception e){
			log.warn("Cannot publish the message to redis", e);
		} finally{
			redisPool.returnResource(jedis);
		}		
	}

	private void notifySubscribers(RedisMessage message) {
		for (IMessageSubscriber subscriber : subscribers) {
			subscriber.receive(message);
		}
	}
	
	public void setMessageSubscribers(Set<IMessageSubscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public void setRedisPool(JedisPool redisPool){
		this.redisPool = redisPool;
	}

	
	/**
	 * Listener for messages from Redis channels.
	 *
	 */
	private class PubSubListener extends JedisPubSub {		
		public PubSubListener() {
			super();			
		}

		@Override
		public void onMessage(String channel, String message) {
			// Not used.
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
			Gson gson = new Gson();
			Map<String, String> msg = gson.fromJson(message, new TypeToken<Map<String, String>>() {}.getType());			
			messagesReceived.offer(new RedisMessage(channel, msg));
		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {
			log.debug("Subscribed to the pattern: " + pattern);
		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {
			// Not used.
		}

		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
			// Not used.
		}

		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
			// Not used.
		}		
	}
}
