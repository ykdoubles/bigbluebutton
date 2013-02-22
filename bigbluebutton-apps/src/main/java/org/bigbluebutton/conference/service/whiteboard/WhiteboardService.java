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
package org.bigbluebutton.conference.service.whiteboard;

import java.util.ArrayList;
import java.util.Map;

import org.bigbluebutton.conference.BigBlueButtonSession;
import org.bigbluebutton.conference.Constants;
import org.bigbluebutton.conference.IBigBlueButtonGateway;
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.Red5;
import org.red5.server.api.scope.IScope;
import org.slf4j.Logger;

public class WhiteboardService {

	private static Logger log = Red5LoggerFactory.getLogger(WhiteboardService.class, "bigbluebutton");
	
	private IBigBlueButtonGateway bbbGW;
		
	public void sendAnnotation(Map<String, Object> annotation) {
//		for (Map.Entry<String, Object> entry : annotation.entrySet()) {
//		    String key = entry.getKey();
//		    Object value = entry.getValue();
		    
//		    if (key.equals("points")) {
//		    	String points = "points=[";
//		    	ArrayList<Double> v = (ArrayList<Double>) value;
//		    	log.debug(points + pointsToString(v) + "]");
//		    } else {
//		    	log.debug(key + "=[" + value + "]");
//		    }
//		}
		
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		
		
		Annotation a = new Annotation(annotation);
		
		bbbGW.sendAnnotation(meetingID, a);
	}
	
	private String pointsToString(ArrayList<Double> points){
    	String datapoints = "";
    	for (Double i : points) {
    		datapoints += i + ",";
    	}
    	// Trim the trailing comma
//    	log.debug("Data Point = " + datapoints);
    	return datapoints.substring(0, datapoints.length() - 1);

//		application.sendShape(shape, type, color, thickness, fill, fillColor, transparency, id, status);

	}
	
	public void setActivePage(String page){		
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		Integer pageNum = new Integer(page);
		bbbGW.changePage(meetingID, pageNum);
	}
	
	public void requestAnnotationHistory(Map<String, Object> message) {
		log.info("WhiteboardApplication - requestAnnotationHistory");
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		String requesterID = getMyUserId();
		bbbGW.sendAnnotationHistory(meetingID, requesterID);
	}
		
	public void clear() {
		log.info("WhiteboardApplication - Clearing board");
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		bbbGW.clearAnnotations(meetingID);
	}
	
	public void undo() {
		log.info("WhiteboardApplication - Deleting last graphic");
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		bbbGW.undoAnnotation(meetingID);
	}
	
	public void toggleGrid() {
		log.info("WhiteboardApplication - Toggling grid mode");
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();
		bbbGW.toggleGrid(meetingID);
	}
	
	public void setActivePresentation(Map<String, Object> message) {		
		log.info("WhiteboardApplication - Setting active presentation: " + (String)message.get("presentationID"));
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();		
		bbbGW.setActivePresentation(meetingID, (String)message.get("presentationID"), (Integer) message.get("numberOfSlides"));
	}
	
	public void enableWhiteboard(Map<String, Object> message) {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();	
		log.info("WhiteboardApplication - Setting whiteboard enabled: " + (Boolean)message.get("enabled"));
		Boolean enable = (Boolean)message.get("enabled");
		bbbGW.enableWhiteboard(meetingID, enable);
	}
	
	public void isWhiteboardEnabled() {
		IScope scope = Red5.getConnectionLocal().getScope();
		String meetingID = scope.getName();	
		String requesterID = getMyUserId();
		bbbGW.isWhiteboardEnabled(meetingID, requesterID);
	}
	
	public void setBigBlueButtonGateway(IBigBlueButtonGateway bbbGW) {
		this.bbbGW = bbbGW;
	}
	
	private String getMyUserId() {
		BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
		assert bbbSession != null;
		return bbbSession.getInternalUserID();
	}
	
}
