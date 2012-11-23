package org.bigbluebutton.conference;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bigbluebutton.conference.exceptions.PresenterChangeException;
import org.bigbluebutton.conference.exceptions.SamePresenterChangeException;
import org.bigbluebutton.conference.vo.NewPresenterVO;
import org.bigbluebutton.conference.vo.UserVO;

public class UsersManager {
	String curPresenterUserID = "";
	String presenterAssignedBy = "";
		
	private final Map<String, User> users = new ConcurrentHashMap<String, User>();
		
	/**
     * Adds the new user that just joined.
     * 
     * @param newUser
     * 			The user that has joined.
     * 
     * @return The new user.
     */
	public void addUser(UserVO newUser) {
		User us = new User(newUser.intUserID, newUser.name, newUser.role, newUser.extUserID);			
		users.put(us.intUserID, us);		
		//return toUserVO(us);
	}

	/**
     * Removes the new user that just left.
     * 
     * @param userID
     * 			The user that has left.
     * 
     * @return The user that left. Null if the user cannot be found.
     */
	public UserVO removeUser(String userID) {
		User us = users.remove(userID);
		if (us != null) {
			return toUserVO(us);
		}
		return null;
	}
	
	/**
     * Make someone presenter.
     * 
     * @param newPresenterUserID
     * 			The user that is the new presenter.
     * 
     * @param assignedByUser
     * 			If this assignment is by another user or by the system.
     * 
     * @param assignedByUserID
     * 			The user that assigned a new presenter.
     * 
     * @return The user that is presenter. Throws exception if it fails to set a presenter.
     */	
	public NewPresenterVO makePresenter(String newPresenterUserID, boolean assignedByUser, String assignedByUserID) 
			throws SamePresenterChangeException, PresenterChangeException {		
		if (curPresenterUserID.equals(newPresenterUserID)) {
			throw new SamePresenterChangeException("New presenter is already presenter [" + newPresenterUserID + "]");
		}
		
		User newPresenter = getUser(newPresenterUserID);
		User oldPresenter = getUser(curPresenterUserID);
		
		if (newPresenter != null) {
			newPresenter.becomePresenter();
			oldPresenter.becomeViewer();
			curPresenterUserID = newPresenterUserID;
			presenterAssignedBy = assignedByUserID;	
			return new NewPresenterVO(curPresenterUserID, newPresenter.name, assignedByUser, assignedByUserID);
		}	
		
		throw new PresenterChangeException("Failed to assign presenter");
	}

	/**
     * Make someone presenter.
     *
     * @return The user that is presenter. Null if it can't set a presenter.
     */
	public NewPresenterVO makeAModeratorPresenter() {
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isModerator()) {
				curPresenterUserID = part.intUserID;
				presenterAssignedBy = curPresenterUserID;
				return new NewPresenterVO(curPresenterUserID, part.name, false, presenterAssignedBy);
			}
		}
		
		return null;
	}
	
	public boolean raiseHand(String userID, boolean raised) {
		User u = getUser(userID);
		if (u != null) {
			if (raised) {
				u.raiseHand();
			} else {
				u.lowerHand();
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasVideo(String userID, boolean hasVideo, String streamName) {
		User u = getUser(userID);
		if (u != null) {
			if (hasVideo) {
				u.addVideo(streamName);
			} else {
				u.removeVideo();
			}
			return true;
		}
		
		return false;
	}
	
	public boolean hasAudio(String userID, boolean hasAudio, String streamName) {
		User u = getUser(userID);
		if (u != null) {
			if (hasAudio) {
				u.addAudio(streamName);
			} else {
				u.removeAudio();
			}
			return true;
		}
		
		return false;		
	}
		
	public Collection<UserVO> getUsers() {
		Map<String, UserVO> us = new HashMap<String, UserVO>();
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			us.put(part.intUserID, toUserVO(part));
		}		
		
		return us.values();
	}
	
	public int getNumModerators() {
		int num = 0;
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isModerator()) {
				num++;
			}
		}
		return num;
	}
	
	public boolean hasPresenter() {
		for (Iterator<User> it = users.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isPresenter()) {
				return true;
			}
		}
		return false;
	}
	
	private User getUser(String userID) {
		return users.get(userID);
	}

	private UserVO toUserVO(User u) {
		UserVO uvo = new UserVO(u.intUserID, u.name, u.extUserID, u.role);
		uvo.presenter = u.isPresenter();
		uvo.hasAudio = u.hasAudio();
		uvo.hasVideo = u.hasVideo();
		uvo.audioStreamName = u.getAudioStreamName();
		uvo.videoStreamName = u.getVideoStreamName();
		uvo.hasHandRaised = u.hasHandRaised();
		
		return uvo;
	}
}
