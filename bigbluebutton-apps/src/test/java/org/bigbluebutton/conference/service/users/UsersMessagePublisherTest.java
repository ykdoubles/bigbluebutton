package org.bigbluebutton.conference.service.users;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class UsersMessagePublisherTest {
	private UsersMessagePublisher publisher;
	
	@BeforeTest
	public void init(){
		publisher = new UsersMessagePublisher();
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void SetMessagePublisher_WhenParamIsNull_ShouldThrowException(){
		publisher.setMessagePublisher(null);
	}
	
}
