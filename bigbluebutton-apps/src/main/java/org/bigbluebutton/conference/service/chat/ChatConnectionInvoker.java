package org.bigbluebutton.conference.service.chat;

import org.bigbluebutton.conference.IConnectionInvokerService;
import org.bigbluebutton.conference.IMessageOutListener;
import org.bigbluebutton.conference.messages.out.IMessageOut;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ChatConnectionInvoker implements IMessageOutListener {
	
	private static Logger log = Red5LoggerFactory.getLogger(ChatConnectionInvoker.class, "bigbluebutton");
	
	private IConnectionInvokerService connInvokerService;

	@Override
	public void accept(IMessageOut message) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void setConnInvokerService(IConnectionInvokerService cis){
		if(cis == null)
			throw new IllegalArgumentException();
		this.connInvokerService = cis;
	}
}
