package org.bigbluebutton.conference.vo;

import java.util.Collection;

public class UserVO {
	public final String internalUserID;
	public final String externalUserID;
	public final String name;
	public final String role;
	public final Collection<StatusVO> status;
	
	public UserVO(String intUserID, String extUserID, String role, String name, Collection<StatusVO> status) {
		this.internalUserID = intUserID;
		this.externalUserID = extUserID;
		this.role = role;
		this.name = name;
		this.status = status;
	}
	
}
