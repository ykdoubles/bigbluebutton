package org.bigbluebutton.conference.red5.adapter.participants;

import java.util.ArrayList;

import org.bigbluebutton.conference.Participant;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.so.ISharedObject;
import org.slf4j.Logger;

public class ParticipantsAdapter {
	private static Logger log = Red5LoggerFactory.getLogger( ParticipantsAdapter.class, "bigbluebutton" );
	
	private ISharedObject so;	
	
	public ParticipantsAdapter(ISharedObject so) {
		this.so = so; 
	}
	
	@SuppressWarnings({ "rawtypes" })
	public void endAndKickAll() {
		so.sendMessage("logout", new ArrayList());
	}

	public void assignPresenter(ArrayList<String> presenter) {
		log.debug("calling assignPresenterCallback " + presenter.get(0) + ", " + presenter.get(1) + " " + presenter.get(2));
		so.sendMessage("assignPresenterCallback", presenter);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void participantJoined(Participant p) {
		log.debug("A participant has joined " + p.getInternalUserID());
		ArrayList args = new ArrayList();
		args.add(p.toMap());
		log.debug("Sending participantJoined " + p.getExternalUserID() + " to client.");
		so.sendMessage("participantJoined", args);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void participantLeft(Participant p) {
		ArrayList args = new ArrayList();
		args.add(p.getInternalUserID());
		so.sendMessage("participantLeft", args);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void participantStatusChange(Participant p, String status, Object value) {
		log.debug("A participant's status has changed " + p.getInternalUserID() + " " + status + " " + value);
		ArrayList args = new ArrayList();
		args.add(p.getInternalUserID());
		args.add(status);
		args.add(value);
		so.sendMessage("participantStatusChange", args);
	}
	
}
