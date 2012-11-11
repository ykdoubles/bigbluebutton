/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
*
* Copyright (c) 2010 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 2.1 of the License, or (at your option) any later
* version.
*
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* 
*/
package org.bigbluebutton.conference;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class User {
	private static Logger log = Red5LoggerFactory.getLogger(User.class, "bigbluebutton");
	
	public final String intUserID;
	public final String name;
	public final String extUserID;
	public final String role;
	
	private boolean presenter = false;
	private boolean hasHandRaised = false;
	
	private boolean hasVideo = false;
	private String videoStreamName = null;
	
	private boolean hasAudio = false;
	private String audioStreamName = null;
	
	private int audioPin = 0;
		
	public User(String intUserID, String name, String role, String extUserID) {
		this.intUserID = intUserID;
		this.name = name;
		this.role = role;
		this.extUserID = extUserID;
	}
	
	public boolean isModerator() {
		return Role.MODERATOR.equals(role);
	}
		
	public String getRole() {
		return role;
	}
			
	public boolean isPresenter() {
		return presenter;
	}
	
	public void becomePresenter() {
		presenter = true;
	}
	
	public void becomeViewer() {
		presenter = false;
	}

	public boolean hasAudio() {
		return hasAudio;
	}
	
	public void removeAudio() {
		hasAudio = false;
		audioStreamName = null;
	}
	
	public void addAudio(String streamName) {
		if (streamName != null && !streamName.isEmpty()) {
			audioStreamName = streamName;
			hasAudio = true;
		} else {
			removeAudio();
			log.error("Invalid audio stream name for user [" + intUserID + ", " + name + "]!");
		}
	}
	
	public String getAudioStreamName() {
		if (audioStreamName == null) return "";
		return audioStreamName;
	}
	
	public boolean hasVideo() {
		return hasVideo;
	}
	
	public void removeVideo() {
		hasVideo = false;
		videoStreamName = null;
	}
	
	public void addVideo(String streamName) {
		if (streamName != null && !streamName.isEmpty()) {
			videoStreamName = streamName;
			hasVideo = true;
		} else {
			log.error("Invalid video stream name for user [" + intUserID + ", " + name + "]!");
			removeVideo();
		}
	}
	
	public String getVideoStreamName() {
		if (videoStreamName == null) return "";
		
		return videoStreamName;
	}
	
	public void raiseHand() {
		hasHandRaised = true;
	}
	
	public void lowerHand() {
		hasHandRaised = false;
	}
	
	public boolean hasHandRaised() {
		return hasHandRaised;
	}
	
	public static class UserBuilder {
		private String intUserID;
		private String name;
		private String role;
		private String extUserID;
		private boolean presenter = false;
		private boolean hasVideo = false;
		private boolean hasAudio = false;
		private boolean hasHandRaised = false;
		
		private String videoStreamName = "";
		private String audioStreamName = "";
		private int audioPin = 0;
		
		public UserBuilder withIntUserID(String userID) {
			this.intUserID = userID;
			return this;
		}
		
		public UserBuilder withName(String name) {
			this.name = name;
			return this;
		}
		
		public UserBuilder withExtUserID(String userID) {
			this.extUserID = userID;
			return this;
		}
		
		public UserBuilder withRole(String role) {
			this.role = role;
			return this;
		}
		
		public User build() {
			return new User(intUserID, name, role, extUserID);
		}
	}
}