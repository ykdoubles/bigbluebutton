package org.bigbluebutton.conference;

import org.bigbluebutton.conference.ClientMessage;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.testng.Assert;

public class ClientMessageMatcher implements IArgumentMatcher {
	
	private ClientMessage expected;
	
	public ClientMessageMatcher(ClientMessage cm){
		this.expected = cm;
	}
	
	public static ClientMessage eqClientMessage(ClientMessage cm) {
	    EasyMock.reportMatcher(new ClientMessageMatcher(cm));
	    return null;
	}

	@Override
	public void appendTo(StringBuffer buf) {
		buf.append("eqClientMessage:");
		buf.append(expected);
	}

	@Override
	public boolean matches(Object arg0) {
		if (! (arg0 instanceof ClientMessage)) { 
			return false; 
		}
		
		ClientMessage actual = (ClientMessage) arg0;
		
		/*
		 * Checking fields
		 * */
		Assert.assertEquals(actual.getType(), expected.getType());
		Assert.assertEquals(actual.getDest(), expected.getDest());
		Assert.assertEquals(actual.getMessageName(), expected.getMessageName());
		Assert.assertEquals(actual.getMessage(), expected.getMessage());
		
		return true;
	}

}
