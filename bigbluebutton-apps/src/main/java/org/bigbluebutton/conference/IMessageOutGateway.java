package org.bigbluebutton.conference;

import org.bigbluebutton.conference.messages.out.IMessageOut;

public interface IMessageOutGateway {

	public void accept(final IMessageOut message);

}
