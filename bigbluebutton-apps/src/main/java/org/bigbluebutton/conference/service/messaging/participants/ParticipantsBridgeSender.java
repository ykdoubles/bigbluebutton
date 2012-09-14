package org.bigbluebutton.conference.service.messaging.participants;

import java.util.ArrayList;
import java.util.HashMap;

import org.bigbluebutton.conference.Participant;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.MessagingService;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;


public class ParticipantsBridgeSender{
	
	private MessagingService messagingService;

	public static final String USER_JOIN = "userJoinMsg";
	public static final String USER_LEFT = "userLeftMsg";
	
	public ParticipantsBridgeSender(){
		
	}
	
	public void participantStatusChange(Participant p, String status,
			Object value) {
		// TODO Auto-generated method stub
		
	}
	
	public void participantJoined(String meetingID, long internalUserID, String username, String role, String externalUserID) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("meetingID", meetingID);
		map.put("messageName", ParticipantsBridgeSender.USER_JOIN);
		
		HashMap<String,String> user = new HashMap<String, String>();
		user.put("internalUserID", Long.toString(internalUserID));
		user.put("username", username);
		user.put("role", role);
		user.put("externalUserID", externalUserID);
		
		map.put("params",user);
		
		Gson gson = new Gson();
		
		messagingService.send(MessagingConstants.BIGBLUEBUTTON_BRIDGE, gson.toJson(map));
		
		//temporary solution for integrate with the html5 client
		Jedis jedis = messagingService.createRedisClient();
		jedis.sadd("meeting:"+meetingID+":users", Long.toString(internalUserID));
		HashMap<String,String> temp_user = new HashMap<String, String>();
		temp_user.put("internalUserID", Long.toString(internalUserID));
		temp_user.put("username", username);
		temp_user.put("role", role);
		
		//variables needed by node.js
		temp_user.put("meetingID", meetingID);
		temp_user.put("refreshing", "false");
		temp_user.put("dupSess", "false");
		temp_user.put("sockets", "0");
		temp_user.put("pubID", Long.toString(internalUserID));
		
		//variables needed by red5
		temp_user.put("externalUserID", externalUserID);
		
		jedis.hmset("meeting:"+meetingID+":user:"+internalUserID, temp_user);
		messagingService.dropRedisClient(jedis);
	}

	public void participantLeft(String meetingID, long internalUserID) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("meetingID", meetingID);
		map.put("messageName", ParticipantsBridgeSender.USER_LEFT);
		
		HashMap<String,String> user = new HashMap<String, String>();
		user.put("internalUserID", Long.toString(internalUserID));
		
		map.put("params",user);
		
		Gson gson = new Gson();
		
		messagingService.send(MessagingConstants.BIGBLUEBUTTON_BRIDGE, gson.toJson(map));
		
		//TODO: temp solution
		Jedis jedis = messagingService.createRedisClient();
		jedis.srem("meeting:"+meetingID+":users", Long.toString(internalUserID));
		jedis.del("meeting:"+meetingID+":user:"+internalUserID);
		messagingService.dropRedisClient(jedis);
	}

	public void assignPresenter(ArrayList<String> presenter) {
		// TODO Auto-generated method stub
		
	}

	public void endAndKickAll() {
		// TODO Auto-generated method stub
		
	}
	

	public MessagingService getMessagingService() {
		return messagingService;
	}

	public void setMessagingService(MessagingService messagingService) {
		this.messagingService = messagingService;
	}

}
