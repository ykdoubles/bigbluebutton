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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bigbluebutton.conference.vo.StatusVO;


public class User {
	public final String internalUserID;
	public final String name;
	public final String externalUserID;
	
	public String role = "VIEWER";	
	private final Map<String, StatusVO> statusMap;
	private final Map<String, StatusVO> unmodifiableStatusMap;
	
	public User(String internalUserID, String name, String role, String externalUserID, StatusVO status) {
		this.internalUserID = internalUserID;
		this.name = name;
		this.role = role;
		this.externalUserID = externalUserID;
		
		this.statusMap = new ConcurrentHashMap<String, StatusVO>();
		statusMap.put(status.name, status);
		
		unmodifiableStatusMap = Collections.unmodifiableMap(statusMap);
	}
	
	public boolean isModerator() {
		return "MODERATOR".equals(role);
	}
	
	public Collection<StatusVO> getStatus() {
		return unmodifiableStatusMap.values();
	}
	
	public String getRole() {
		return role;
	}
			
	public void setStatus(StatusVO status) {
		statusMap.put(status.name, status);
	}
	
	public void removeStatus(String statusName) {
		statusMap.remove(statusName);
	}

}