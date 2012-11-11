package org.bigbluebutton.conference.vo;

import org.bigbluebutton.conference.Role;

public class UserVO {
	public final String intUserID;
	public final String extUserID;
	public final String name;
	public final String role;
	
	public boolean presenter = false;
	public boolean hasHandRaised = false;	
	public boolean hasVideo = false;
	public String videoStreamName = null;
	public boolean hasAudio = false;
	public String audioStreamName = null;
	public int audioPin = 0;
	
	public UserVO(String intUserID, String extUserID, String role, String name) {
		this.intUserID = intUserID;
		this.extUserID = extUserID;
		this.role = role;
		this.name = name;
	}
	
	public boolean isModerator() {
		return Role.MODERATOR.equals(role);
	}
	
}
