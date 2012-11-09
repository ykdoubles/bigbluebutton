package org.bigbluebutton.conference.service.messaging;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.messages.out.MeetingEnded;
import org.bigbluebutton.conference.messages.out.MeetingStarted;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;

public class PublishMessageService implements IMessageOutListener {
	private static Logger log = Red5LoggerFactory.getLogger(PublishMessageService.class, "bigbluebutton");
	
	private final Executor exec = Executors.newSingleThreadExecutor();
	private static final BlockingQueue<IMessageOut> messages = new LinkedBlockingQueue<IMessageOut>();
	
	private JedisPool redisPool;
	private volatile boolean sendMessage = false;
	
	public void start() {
		log.info("Starting Redis Publishing Message Service");
		
		sendMessage = true;
		
		Runnable sender = new Runnable() {
			public void run() {
				while (sendMessage) {
					try {
						IMessageOut m = messages.take();
						processMessage(m);
					} catch (InterruptedException e) {
						log.error("Interrupted exception while sending messages.");
					}					
				}
			}
		};
		exec.execute(sender);
	}
	
	public void stop() {
		log.info("Stopping Redis Publishing Message Service");
		
		sendMessage = false;
	}
	
	@Override
	public void accept(IMessageOut message) {
		try {
			messages.put(message);
		} catch (InterruptedException e) {
			log.error("Interrupted exception while receiving message.");
		}
	}
	
	private void processMessage(IMessageOut message) {
		if (message instanceof MeetingStarted) {
			handleMeetingStarted((MeetingStarted) message);
		} else if (message instanceof MeetingEnded)	{
			handleMeetingEnded((MeetingEnded) message);
		}
	}
	
	private void handleMeetingEnded(MeetingEnded message) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.MEETING_ENDED_EVENT);
		
		Gson gson = new Gson();
		send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
	}
	
	private void handleMeetingStarted(MeetingStarted message) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("meetingId", message.meetingID);
		map.put("messageId", MessagingConstants.MEETING_STARTED_EVENT);
		
		Gson gson = new Gson();
		send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
	}

	private void send(String channel, String message) {
		Jedis jedis = redisPool.getResource();
		try {
			jedis.publish(channel, message);
		} catch(Exception e){
			log.warn("Cannot publish the message to redis", e);
		}finally{
			redisPool.returnResource(jedis);
		}
	}
	
	public void setRedisPool(JedisPool redisPool){
		this.redisPool = redisPool;
	}
}
