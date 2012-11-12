package org.bigbluebutton.conference.vo;

import java.util.Map;
import org.bigbluebutton.conference.Role;

public class UserVO {
	public final String intUserID;
	public final String extUserID;
	public final String name;
	public final String role;
	
	public boolean presenter = false;
	public boolean hasHandRaised = false;	
	public boolean hasVideo = false;
	public String videoStreamName = "";
	public boolean hasAudio = false;
	public String audioStreamName = "";
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
	
	public static final void toMap(UserVO u, Map<String, Object> dest) {
		assert dest != null;
		
		dest.put("intUserID", u.intUserID);
		dest.put("extUserID", u.extUserID);
		dest.put("name", u.name);
		dest.put("role", u.role);
		dest.put("presenter", u.presenter);
		dest.put("hasHandRaised", u.hasHandRaised);
		dest.put("hasVideo", u.hasVideo);
		dest.put("hasAudio", u.hasAudio);
		dest.put("audioStreamName", u.audioStreamName);
		dest.put("videoStreamName", u.videoStreamName);
	}
	
}
