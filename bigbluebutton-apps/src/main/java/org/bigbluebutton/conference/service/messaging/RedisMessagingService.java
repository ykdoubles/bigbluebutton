package org.bigbluebutton.conference.service.messaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bigbluebutton.conference.service.messaging.participants.ParticipantsBridgeSender;
import org.bigbluebutton.conference.service.participants.ParticipantsApplication;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisMessagingService implements MessagingService{

	private static Logger log = Red5LoggerFactory.getLogger(RedisMessagingService.class, "bigbluebutton");
	
	private JedisPool redisPool;
	private final Executor exec = Executors.newSingleThreadExecutor();
	private Runnable pubsubListener;
	
	private final Set<MessageListener> listeners = new HashSet<MessageListener>();
	
	private ParticipantsApplication participantsApplication;

	public RedisMessagingService(){
		
	}
	
	@Override
	public void start() {
		log.debug("Starting redis pubsub...");		
		final Jedis jedis = redisPool.getResource();
		try {
			pubsubListener = new Runnable() {
			    public void run() {
			    	//jedis.psubscribe(new PubSubListener(), MessagingConstants.BIGBLUEBUTTON_PATTERN);
			    	jedis.psubscribe(new PubSubListener(), "*");
			    }
			};
			exec.execute(pubsubListener);
		} catch (Exception e) {
			log.error("Error subscribing to channels: " + e.getMessage());
		}
	}

	@Override
	public void stop() {
		try {
			redisPool.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
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
	
	public Jedis createRedisClient(){
		return redisPool.getResource();
	}
	public void dropRedisClient(Jedis jedis){
		redisPool.returnResource(jedis);
	}
	public void setParticipantsApplication(ParticipantsApplication pa){
		this.participantsApplication = pa;
	}

	@Override
	public void addListener(MessageListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(MessageListener listener) {
		listeners.remove(listener);
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
			
			if(channel.equalsIgnoreCase(MessagingConstants.SYSTEM_CHANNEL)){
				HashMap<String,String> map = gson.fromJson(message, new TypeToken<Map<String, String>>() {}.getType());
				String meetingId = map.get("meetingId");
				String messageId = map.get("messageId");
				if(messageId != null){
					if(MessagingConstants.END_MEETING_REQUEST_EVENT.equalsIgnoreCase(messageId)){
						for (MessageListener listener : listeners) {
							listener.endMeetingRequest(meetingId);
						}
					}
				}
			}
			else if(channel.equalsIgnoreCase(MessagingConstants.PRESENTATION_CHANNEL)){
				HashMap<String,String> map = gson.fromJson(message, new TypeToken<Map<String, String>>() {}.getType());
				for (MessageListener listener : listeners) {
					listener.presentationUpdates(map);
				}
			}
			else if(channel.equalsIgnoreCase(MessagingConstants.BIGBLUEBUTTON_BRIDGE)){
				JsonParser parser = new JsonParser();
			    JsonObject array = parser.parse(message).getAsJsonObject();
			    String meetingId = gson.fromJson(array.get("meetingID"), String.class);
			    String messageName = gson.fromJson(array.get("messageName"), String.class);
			    
			    JsonObject params = array.getAsJsonObject("params");
			    
				if(messageName.equalsIgnoreCase(ParticipantsBridgeSender.USER_JOIN)){
					
					
					long red5id = 0;
					//if(user.containsKey("red5id"))
						red5id = Long.parseLong(gson.fromJson(params.get("red5id"), String.class));
					String username = gson.fromJson(params.get("username"), String.class);
					String role = gson.fromJson(params.get("role"), String.class);
					String externalUserID = gson.fromJson(params.get("externalUserID"), String.class);
					
					Map<String, Boolean> status = new HashMap<String, Boolean>();
					status.put("raiseHand", false);
					status.put("presenter", false);
					status.put("hasStream", false);
					
					log.debug("checking: "+meetingId+" "+red5id+" "+username);
					participantsApplication.participantJoin(meetingId, red5id, username, role, externalUserID, status);
				}else if(messageName.equalsIgnoreCase(ParticipantsBridgeSender.USER_LEFT)){
					
					long red5id = 0;
					//if(user.containsKey("red5id"))
					red5id = Long.parseLong(gson.fromJson(params.get("red5id"), String.class));

					//TODO: we should use just one userid, generated by the API or by redis
					String externalUserID = gson.fromJson(params.get("externalUserID"), String.class);
					
					
					participantsApplication.participantLeft(meetingId, red5id);
				}

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
