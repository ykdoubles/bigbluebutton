package org.bigbluebutton.conference;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

public class ClientMessagingGateway implements IClientMessagingGateway {
	private static final int NTHREADS = 1;
	private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);
	
	
	
	@Override
	public void addMeetingScope(String meetingID, IScope scope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addUserConnection(String meetingID, IConnection conn,
			String userID) {
		// TODO Auto-generated method stub
		
	}

}
