package org.bigbluebutton.conference.service.messaging;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
	private final Executor exec = Executors.newSingleThreadExecutor();
	private Runnable pubsubListener;
	
	private Set<IMessageSubscriber> subscribers;

	public void start() {
		log.debug("Starting redis pubsub...");		
		final Jedis jedis = redisPool.getResource();
		try {
			pubsubListener = new Runnable() {
			    public void run() {
			    	jedis.psubscribe(new PubSubListener(), MessagingConstants.BIGBLUEBUTTON_PATTERN);       			
			    }
			};
			exec.execute(pubsubListener);
		} catch (Exception e) {
			log.error("Error subscribing to channels: " + e.getMessage());
		}
	}

	public void stop() {
		try {
			redisPool.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String channel, String message) {
		Jedis jedis = redisPool.getResource();
		try {
			jedis.publish(channel, message);
		} catch(Exception e){
			log.warn("Cannot publish the message to redis", e);
		}finally{
			redisPool.returnResource(jedis);
		}
	}

	public void setMessageSubscribers(Set<IMessageSubscriber> subscribers) {
		this.subscribers = subscribers;
	}
	
	public void setRedisPool(JedisPool redisPool){
		this.redisPool=redisPool;
	}

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
			log.debug("Message Received in channel: " + channel);
			Gson gson = new Gson();
			Map<String,String> msg = gson.fromJson(message, new TypeToken<Map<String, String>>() {}.getType());
			
			for (IMessageSubscriber subscriber : subscribers) {
				subscriber.receive(msg);
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
