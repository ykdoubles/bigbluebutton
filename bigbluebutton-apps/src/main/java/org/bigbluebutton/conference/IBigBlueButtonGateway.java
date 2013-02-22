package org.bigbluebutton.conference;

import org.bigbluebutton.conference.service.chat.ChatMessageVO;
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation;

public interface IBigBlueButtonGateway {
	// Meeting
	void createMeeting(String meetingID, String meetingName, String voiceBridge, boolean recorded);
	void endMeeting(String meetingID);
	
	// Users
	void joinUser(String meetingID, String userID, String username, String role, String externUserID, boolean raiseHand, boolean presenter, boolean hasStream);
	void leaveUser(String meetingID, String userID);
	void assignPresenter(String meetingID, String newPresenterUserID, String newPresenterName, String assignedBy);
	void sendUsers(String meetingID, String requesterUserID);
	void setUserStatus(String meetingID, String userID, String statusName, Object statusValue);
	
	// Chat
	void sendPublicChatHistory(String meetingID, String requesterID);
	void sendPublicMessage(String meetingID, ChatMessageVO message);
	void sendPrivateMessage(String meetingID, ChatMessageVO message);

	// Layout
	void sendCurrentLayout(String meetingID, String requestedUserID);
	void unlockLayout(String meetingID);
	void lockLayout(String meetingID, String userID, String layoutID);
	
	// Presentation
	void removePresentation(String meetingID, String presentationID);
	void sendPresentationInfo(String meetingID, String requesterID);
	void gotoSlide(String meetingID, int slideNum);
	void sharePresentation(String meetingID, String presentationID, boolean share);
	void sendCursorUpdate(String meetingID, double xPercent, double yPercent);
	void resizeAndMoveSlide(String meetingID, double xOffset, double yOffset, double widthRatio, double heightRatio);
	
	// Voice
	void sendVoiceUsers(String meetingID, String requesterID);
	void muteAll(String meetingID, boolean mute);
	void isRoomMuted(String meetingID, String requesterID);
	void mute(String meetingID, String userID, boolean mute);
	void lock(String meetingID, String userID, boolean lock);
	void eject(String meetingID, String userID);
	
	// Whiteboard
	void sendAnnotation(String meetingID, Annotation a);
	void changePage(String meetingID, int pageNum);
	void sendAnnotationHistory(String meetingID, String requesterID);
	void clearAnnotations(String meetingID);
	void undoAnnotation(String meetingID);
	void toggleGrid(String meetingID);
	void setActivePresentation(String meetingID, String presentationID, int numberOfSlides);
	void enableWhiteboard(String meetingID, boolean enable);
	void isWhiteboardEnabled(String meetingID, String requesterID);
}
