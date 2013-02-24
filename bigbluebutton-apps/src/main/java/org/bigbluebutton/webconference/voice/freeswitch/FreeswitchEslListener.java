package org.bigbluebutton.webconference.voice.freeswitch;

import java.util.Iterator;
import java.util.Map;
import org.bigbluebutton.webconference.voice.events.VoiceEventListener;
import org.bigbluebutton.webconference.voice.events.VoiceUserJoinedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserLeftEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserMutedEvent;
import org.bigbluebutton.webconference.voice.events.VoiceUserTalkingEvent;
import org.bigbluebutton.webconference.voice.events.VoiceRecordingStartedEvent;
import org.freeswitch.esl.client.IEslEventListener;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FreeswitchEslListener implements IEslEventListener {
    private static Logger log = Red5LoggerFactory.getLogger(FreeswitchEslListener.class, "bigbluebutton");

    private static final String START_TALKING_EVENT = "start-talking";
    private static final String STOP_TALKING_EVENT = "stop-talking";
    private static final String START_RECORDING_EVENT = "start-recording";
    private static final String STOP_RECORDING_EVENT = "stop-recording";
    
    private IHearbeatListener hbListener;
    private VoiceEventListener voiceEventListener;
    
    private boolean debug = false;
    
    @Override
    public void eventReceived(EslEvent event) {
        if(event.getEventName().equals(FreeswitchHeartbeatMonitor.EVENT_HEARTBEAT)) {
        	if (hbListener != null) {
        		hbListener.heartbeatReceived();
        	}
        }
    }

    @Override
    public void conferenceEventJoin(String uniqueId, String confName, int confSize, EslEvent event) {
        Integer memberId = this.getMemberIdFromEvent(event);
        Map<String, String> headers = event.getEventHeaders();
        String callerId = this.getCallerIdFromEvent(event);
        String callerIdName = this.getCallerIdNameFromEvent(event);
        boolean muted = headers.get("Speak").equals("true") ? false : true; //Was inverted which was causing a State issue
        boolean speeking = headers.get("Talking").equals("true") ? true : false;

        VoiceUserJoinedEvent pj = new VoiceUserJoinedEvent(memberId, confName, callerId, callerIdName, muted, speeking);
        voiceEventListener.handleConferenceEvent(pj);
    }

    @Override
    public void conferenceEventLeave(String uniqueId, String confName, int confSize, EslEvent event) {
        Integer memberId = this.getMemberIdFromEvent(event);
        VoiceUserLeftEvent pl = new VoiceUserLeftEvent(memberId, confName);
        voiceEventListener.handleConferenceEvent(pl);
    }

    @Override
    public void conferenceEventMute(String uniqueId, String confName, int confSize, EslEvent event) {
        Integer memberId = this.getMemberIdFromEvent(event);
        VoiceUserMutedEvent pm = new VoiceUserMutedEvent(memberId, confName, true);
        voiceEventListener.handleConferenceEvent(pm);
    }

    @Override
    public void conferenceEventUnMute(String uniqueId, String confName, int confSize, EslEvent event) {
        Integer memberId = this.getMemberIdFromEvent(event);
        VoiceUserMutedEvent pm = new VoiceUserMutedEvent(memberId, confName, false);
        voiceEventListener.handleConferenceEvent(pm);
    }

    @Override
    public void conferenceEventAction(String uniqueId, String confName, int confSize, String action, EslEvent event) {
        Integer memberId = this.getMemberIdFromEvent(event);
        VoiceUserTalkingEvent pt;

        if(action == null) {
            if(debug) {
                Map<String, String> eventHeaders = event.getEventHeaders();
                StringBuilder sb = new StringBuilder("\n");
                for (Iterator it=eventHeaders.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry)it.next();
                    sb.append(entry.getKey());
                    sb.append(" => '");
                    sb.append(entry.getValue());
                    sb.append("'\n");
                }
                log.debug ("NULL Conference Action [{}] Headers:\n{}\nEND", confName, sb.toString());
            }
            return;
        }

        if (action.equals(START_TALKING_EVENT)) {
            pt = new VoiceUserTalkingEvent(memberId, confName, true);
            voiceEventListener.handleConferenceEvent(pt);        	
        } else if (action.equals(STOP_TALKING_EVENT)) {
            pt = new VoiceUserTalkingEvent(memberId, confName, false);
            voiceEventListener.handleConferenceEvent(pt);        	
        } else {
        	log.debug("Unknown conference Action [{}]", action);
        }
    }

    @Override
    public void conferenceEventTransfer(String uniqueId, String confName, int confSize, EslEvent event) {
        //Ignored, Noop
    }

    @Override
    public void conferenceEventThreadRun(String uniqueId, String confName, int confSize, EslEvent event) {
    	
    }
    
    //@Override
    public void conferenceEventRecord(String uniqueId, String confName, int confSize, EslEvent event) {
    	String action = event.getEventHeaders().get("Action");
    	
        if(action == null) {
            if (debug) {
                Map<String, String> eventHeaders = event.getEventHeaders();
                StringBuilder sb = new StringBuilder("\n");
                for (Iterator it=eventHeaders.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry)it.next();
                    sb.append(entry.getKey());
                    sb.append(" => '");
                    sb.append(entry.getValue());
                    sb.append("'\n");
                }
                log.debug ("NULL Conference Action [{}] Headers:\n{}\nEND", confName, sb.toString());
            }
            return;
        }
        
    	if (log.isDebugEnabled())
    		log.debug("Handling conferenceEventRecord " + action);
    	
    	if (action.equals(START_RECORDING_EVENT)) {
            VoiceRecordingStartedEvent sre = new VoiceRecordingStartedEvent(123, confName, true);
            sre.setRecordingFilename(getRecordFilenameFromEvent(event));
            sre.setTimestamp(getRecordTimestampFromEvent(event));
            
            if (log.isDebugEnabled())
            	log.debug("Processing conference event - action: {} time: {} file: {}", new Object[] {action,  sre.getTimestamp(), sre.getRecordingFilename()});
            
            voiceEventListener.handleConferenceEvent(sre);    		
    	} else if (action.equals(STOP_RECORDING_EVENT)) {
        	VoiceRecordingStartedEvent srev = new VoiceRecordingStartedEvent(123, confName, false);
            srev.setRecordingFilename(getRecordFilenameFromEvent(event));
            srev.setTimestamp(getRecordTimestampFromEvent(event));
            
            if (log.isDebugEnabled())
            	log.debug("Processing conference event - action: {} time: {} file: {}", new Object[] {action,  srev.getTimestamp(), srev.getRecordingFilename()});
            
            voiceEventListener.handleConferenceEvent(srev);    		
    	} else {
        	if (log.isDebugEnabled())
        		log.warn("Processing UNKNOWN conference Action {}", action);
    	}
    }

    @Override
    public void conferenceEventPlayFile(String uniqueId, String confName, int confSize, EslEvent event) {
        //Ignored, Noop
    }

    @Override
    public void backgroundJobResultReceived(EslEvent event) {
        log.debug( "Background job result received [{}]", event );
    }

    @Override
    public void exceptionCaught(ExceptionEvent e) {
        log.error( "FreeSwitch ESL Exception.", e );
    }

    private Integer getMemberIdFromEvent(EslEvent e) {
        return new Integer(e.getEventHeaders().get("Member-ID"));
    }

    private String getCallerIdFromEvent(EslEvent e) {
        return e.getEventHeaders().get("Caller-Caller-ID-Number");
    }

    private String getCallerIdNameFromEvent(EslEvent e) {
        return e.getEventHeaders().get("Caller-Caller-ID-Name");
    }
    
    private String getRecordFilenameFromEvent(EslEvent e) {
    	return e.getEventHeaders().get("Path");
    }
    
    private String getRecordTimestampFromEvent(EslEvent e) {
    	return e.getEventHeaders().get("Event-Date-Timestamp");
    }
    
    public void setVoiceEventListener(VoiceEventListener l) {
    	voiceEventListener = l;
    }
    
    public void setHeartbeatListener(IHearbeatListener l) {
    	hbListener = l;
    }
}
