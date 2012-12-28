package org.bigbluebutton.conference.service.presentation;

import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.bigbluebutton.conference.service.messaging.IMessagePublisher;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class PresentationMessagePublisher implements IMessageOutListener {

private static Logger log = Red5LoggerFactory.getLogger(PresentationMessagePublisher.class, "bigbluebutton");
	
	private IMessagePublisher publisher;
	
	@Override
	public void accept(IMessageOut message) {
		if(message == null){
			throw new IllegalArgumentException();
		}
		
		/*if(message instanceof PublicChatMessageSent){
			handlePublicChatMessageSent((PublicChatMessageSent)message);
		}*/
	}
	
	public void setMessagePublisher(IMessagePublisher pub) {
		if( pub == null )
			throw new IllegalArgumentException();
		publisher = pub;
	}
}
