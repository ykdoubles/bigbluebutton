package org.bigbluebutton.conference;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
 
public interface IClientMessagingGateway {
	void addScope(String meetingID, IScope scope, ISharedObject usersSO, ISharedObject layoutSO, ISharedObject pollSO, ISharedObject presentationSO, ISharedObject voiceSO);
	void removeScope(String meetingID);
	void addConnection(String meetingID, String userID, IConnection conn);
	void removeConnection(String meetingID, String userID);
}
