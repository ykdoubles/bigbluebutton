package org.bigbluebutton.conference.vo;

public class NewPresenterVO {
	public final String newPresenterUserID;
	public final String newPresenterName;
	public final boolean assignedByUser;
	public final String assignedByUserID;
	
	public NewPresenterVO(String newPresenterUserID, String newPresenterName
			, boolean assignedByUser, String assignedByUserID) {
		this.newPresenterUserID = newPresenterUserID;
		this.newPresenterName = newPresenterName;
		this.assignedByUser = assignedByUser;
		this.assignedByUserID = assignedByUserID;
	}
}
