package org.bigbluebutton.conference;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersManager {
	String curPresenterUserID = "";
	String presenterAssignedBy = "";
	
	private Map<String, User> users;
	private Map<String, User> unmodifiableMap;
	
	public UsersManager() {
		users = new ConcurrentHashMap<String, User>();
		unmodifiableMap = Collections.unmodifiableMap(users);
	}
	
	public void addUser(User user) {
		synchronized (this) {
//			users.put(user.getInternalUserID(), user);
		}
	}
	
	public User removeUser(String userID) {
		synchronized (this) {
			return users.remove(userID);
		}
	}
	
	public boolean changeUserStatus(String userid, String status, Object value) {
		synchronized (this) {
			User p = users.get(userid);
			if (p != null) {
//				p.setStatus(status, value);
				return true;
			}
			return false;
		}
	}
	
	public Map<String, User> getUsers() {
		return unmodifiableMap;
	}
	
	public int getNumberOfModerators() {
		int sum = 0;
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isModerator()) {
				sum++;
			}
		} 
		return sum;
	}
	
	public Map<String, String> getCurrentPresenter() {
		Map<String, String> curPres =  new HashMap<String, String>();
		curPres.put("presenterUserID", curPresenterUserID);
		curPres.put("assignedBy", presenterAssignedBy);
		curPres.put("presenterName", "");
		
		User user = getUser(curPresenterUserID);
		if (user != null) {
//			curPres.put("presenterName", user.getName());
		}

		
		return curPres;
	}
	
	public boolean assignPresenter(String newPresenterUserID, String assignedByUserID) {
		synchronized (this) {
			User user = getUser(newPresenterUserID);
			if (user != null) {
				curPresenterUserID = newPresenterUserID;
				presenterAssignedBy = assignedByUserID;	
				return true;
			}
			return false;			
		}
	}
	
	public User getUser(String userID) {
		return users.get(userID);
	}
}
