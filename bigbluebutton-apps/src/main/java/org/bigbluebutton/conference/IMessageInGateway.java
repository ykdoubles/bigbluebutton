package org.bigbluebutton.conference;

import org.bigbluebutton.conference.messages.in.IMessageIn;

public interface IMessageInGateway {	
	
	public void accept(final IMessageIn message);
		
}
