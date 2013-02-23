/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
* 
* Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 3.0 of the License, or (at your option) any later
* version.
* 
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
*
*/
package org.bigbluebutton.webconference.voice;

import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.webconference.voice.events.VoiceEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserJoinedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserLeftEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserLockedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserMutedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserTalkingEvent;
import org.bigbluebutton.webconference.voice.events.VoiceRecordingStartedEvent;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class VoiceEventRecorder {
	private static final Logger log = Red5LoggerFactory.getLogger(VoiceEventRecorder.class, "bigbluebutton");
	
	private RecorderApplication recorder;
	
	public void recordConferenceEvent(VoiceEvent event, String room) {
		if (event instanceof VoiceUserJoinedEvent) {
			recordParticipantJoinedEvent(event, room);
		} else if (event instanceof VoiceUserLeftEvent) {		
			recordParticipantLeftEvent(event, room);
		} else if (event instanceof VoiceUserMutedEvent) {
			recordParticipantMutedEvent(event, room);
		} else if (event instanceof VoiceUserTalkingEvent) {
			recordParticipantTalkingEvent(event, room);
		} else if (event instanceof VoiceUserLockedEvent) {
			recordParticipantLockedEvent(event, room);
		} else if (event instanceof VoiceRecordingStartedEvent) {
			recordStartRecordingEvent(event, room);
		} else {
			log.debug("Processing UnknownEvent " + event.getClass().getName() + " for room: " + event.getRoom() );
		}		
	}
	
	private void recordStartRecordingEvent(VoiceEvent event, String room) {
		VoiceRecordingStartedEvent sre = (VoiceRecordingStartedEvent) event;
		StartRecordingVoiceRecordEvent evt = new StartRecordingVoiceRecordEvent(sre.startRecord());
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setRecordingTimestamp(sre.getTimestamp());
		evt.setFilename(sre.getRecordingFilename());
		System.out.println("*** Recording voice " + sre.startRecord() + " timestamp: " + evt.toMap().get("recordingTimestamp") + " file: " + evt.toMap().get("filename"));
		recorder.record(room, evt);
	}
	
	private void recordParticipantJoinedEvent(VoiceEvent event, String room) {
		VoiceUserJoinedEvent pje = (VoiceUserJoinedEvent) event;

		ParticipantJoinedVoiceRecordEvent evt = new ParticipantJoinedVoiceRecordEvent();
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setParticipant(pje.getParticipantId().toString());
		evt.setCallerName(pje.getCallerIdName());
		evt.setCallerNumber(pje.getCallerIdName());
		evt.setMuted(pje.getMuted());
		evt.setTalking(pje.getSpeaking());
		evt.setLocked(pje.isLocked());
		
		recorder.record(room, evt);
	}

	private void recordParticipantLeftEvent(VoiceEvent event, String room) {
		ParticipantLeftVoiceRecordEvent evt = new ParticipantLeftVoiceRecordEvent();
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setParticipant(event.getParticipantId().toString());
		
		recorder.record(room, evt);
	}
	
	private void recordParticipantMutedEvent(VoiceEvent event, String room) {
		VoiceUserMutedEvent pme = (VoiceUserMutedEvent) event;
		
		ParticipantMutedVoiceRecordEvent evt = new ParticipantMutedVoiceRecordEvent();
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setParticipant(event.getParticipantId().toString());
		evt.setMuted(pme.isMuted());
		
		recorder.record(room, evt);
	}
	
	private void recordParticipantTalkingEvent(VoiceEvent event, String room) {
		VoiceUserTalkingEvent pte = (VoiceUserTalkingEvent) event;
	
		ParticipantTalkingVoiceRecordEvent evt = new ParticipantTalkingVoiceRecordEvent();
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setParticipant(event.getParticipantId().toString());
		evt.setTalking(pte.isTalking());
		
		recorder.record(room, evt);
	}
	
	private void recordParticipantLockedEvent(VoiceEvent event, String room) {
		VoiceUserLockedEvent ple = (VoiceUserLockedEvent) event;

		ParticipantLockedVoiceRecordEvent evt = new ParticipantLockedVoiceRecordEvent();
		evt.setMeetingId(room);
		evt.setTimestamp(System.currentTimeMillis());
		evt.setBridge(event.getRoom());
		evt.setParticipant(event.getParticipantId().toString());
		evt.setLocked(ple.isLocked());
		
		recorder.record(room, evt);
	}
	
	public void setRecorderApplication(RecorderApplication recorder) {
		this.recorder = recorder;
	}
}
