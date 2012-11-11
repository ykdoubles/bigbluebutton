package org.bigbluebutton.conference.exceptions;

public class SamePresenterChangeException extends Exception {

	private static final long serialVersionUID = -8305226820859911585L;

	public SamePresenterChangeException(String reason) {
		super(reason);
	}
}
