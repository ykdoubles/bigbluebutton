package org.bigbluebutton.conference;

import org.bigbluebutton.conference.service.chat.ChatMessageVO;

public interface IBigBlueButtonGateway {
	// Meeting
	void createMeeting(String meetingID, String meetingName, String voiceBridge, Boolean recorded);
	void endMeeting(String meetingID);
	
	// Users
	void joinUser(String meetingID, String userID, String username, String role, String externUserID, Boolean raiseHand, Boolean presenter, Boolean hasStream);
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
	void sharePresentation(String meetingID, String presentationID, Boolean share);
	void sendCursorUpdate(String meetingID, Double xPercent, Double yPercent);
	void resizeAndMoveSlide(String meetingID, Double xOffset, Double yOffset, Double widthRatio, Double heightRatio);
	
	// Voice
	void sendVoiceUsers(String meetingID, String requesterID);
	void muteAll(String meetingID, Boolean mute);
	void isRoomMuted(String meetingID, String requesterID);
	void mute(String meetingID, String userID, Boolean mute);
	void lock(String meetingID, String userID, Boolean lock);
	void eject(String meetingID, String userID);
}
