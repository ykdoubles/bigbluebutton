package org.bigbluebutton.conference;

import org.bigbluebutton.conference.messages.in.IMessageIn;

public class BigBlueButton {

	private IMessageInGateway gw;
	
	public void accept(final IMessageIn message) {
		gw.accept(message);
	}
	
	public void setMessageInGateway(IMessageInGateway gw) {
		this.gw = gw;
	}
}
