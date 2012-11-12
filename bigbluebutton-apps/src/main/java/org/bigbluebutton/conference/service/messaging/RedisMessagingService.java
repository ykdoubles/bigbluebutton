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
	private static final BlockingQueue<RedisMessage> messages = new LinkedBlockingQueue<RedisMessage>();
	
	private Runnable messageReceiver, messagePublisher;
	private volatile boolean sendMessage = false;
	private Set<IMessageSubscriber> subscribers;

	public void start() {
		log.debug("Starting redis pubsub...");		
		startMessageReceiver();
		startMessagePublisher();
	}

	public void stop() {
		try {
			sendMessage = false;
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
						RedisMessage rm = messages.take();
						publishMessage(rm);
					} catch (InterruptedException e) {
						log.error("Interrupted Exception while trying to publish message.");
					}
				}
			}
		};
		sendExec.execute(messagePublisher);	
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
	
	public boolean send(String channel, String message) {
		RedisMessage redisMessage = new RedisMessage(channel, message);
		return messages.offer(redisMessage);
	}
	
	private void publishMessage(RedisMessage message) {
		Jedis jedis = redisPool.getResource();
		try {
			jedis.publish(message.channel, message.message);
		} catch(Exception e){
			log.warn("Cannot publish the message to redis", e);
		} finally{
			redisPool.returnResource(jedis);
		}		
	}

	public void setMessageSubscribers(Set<IMessageSubscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public void setRedisPool(JedisPool redisPool){
		this.redisPool = redisPool;
	}

	/**
	 * Redis Message
	 *
	 */
	private class RedisMessage {
		public final String channel;
		public final String message;
		
		public RedisMessage(String channel, String message) {
			this.channel = channel;
			this.message = message;
		}
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
			
			for (IMessageSubscriber subscriber : subscribers) {
				subscriber.receive(channel, msg);
			}
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
