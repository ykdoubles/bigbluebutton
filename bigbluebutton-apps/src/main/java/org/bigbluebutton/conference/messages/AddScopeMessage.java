package org.bigbluebutton.conference.messages;

import org.red5.server.api.scope.IScope;

public class AddScopeMessage implements IMessage {

	private final String meetingID;
	private final IScope scope;
	
	public AddScopeMessage(String meetingID, IScope scope) {
		this.meetingID = meetingID;
		this.scope = scope;
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public IScope getScope() {
		return scope;
	}
}
