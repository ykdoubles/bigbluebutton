package org.bigbluebutton.conference.service.users;

import org.bigbluebutton.conference.service.messaging.RedisMessage;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.testng.Assert;

public class RedisMessageMatcher implements IArgumentMatcher{

	private RedisMessage expected;
	
	public RedisMessageMatcher(RedisMessage rm){
		this.expected = rm;
	}
	
	public static RedisMessage eqRedisMessage(RedisMessage rm) {
	    EasyMock.reportMatcher(new RedisMessageMatcher(rm));
	    return null;
	}

	@Override
	public void appendTo(StringBuffer buf) {
		buf.append("eqRedisMessage:");
		buf.append(expected);
	}

	@Override
	public boolean matches(Object arg0) {
		if (! (arg0 instanceof RedisMessage)) { 
			return false; 
		}
		
		RedisMessage actual = (RedisMessage) arg0;
		
		/*
		 * Checking fields
		 * */
		Assert.assertEquals(actual.channel, expected.channel);
		Assert.assertEquals(actual.message, expected.message);
		
		return true;
	}
}
