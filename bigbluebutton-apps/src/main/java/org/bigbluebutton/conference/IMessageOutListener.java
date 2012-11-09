package org.bigbluebutton.conference;

import org.bigbluebutton.conference.messages.out.IMessageOut;

public interface IMessageOutListener {

	public void accept(IMessageOut message);
}
