package org.bigbluebutton.live

import org.bigbluebutton.conference.IBigBlueButtonGateway
import org.bigbluebutton.conference.service.chat.ChatMessageVO
import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation

class BigBlueButtonGateway extends IBigBlueButtonGateway {
  
  // Meeting
  def createMeeting(meetingID : String, meetingName : String, voiceBridge : String, recorded : Boolean) : Unit = {
    
  }
  
  def endMeeting(meetingID : String) : Unit = {
    
  }
	
	// Users
  def joinUser(meetingID : String, userID : String, username : String, role : String, externUserID : String, 
		  		raiseHand : Boolean, presenter : Boolean, hasStream : Boolean) : Unit = {
    
  }
  
  def leaveUser(meetingID : String, userID : String) : Unit = {
    
  }
  
  def assignPresenter(meetingID : String, newPresenterUserID : String, newPresenterName : String, assignedBy : String) : Unit = {
    
  }
  
  def sendUsers(meetingID : String, requesterUserID : String) : Unit = {
    
  }
  
  def setUserStatus(meetingID : String, userID : String, statusName : String, statusValue : Object) : Unit = {
    
  }
	
	// Chat
  def sendPublicChatHistory(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  def sendPublicMessage(meetingID : String, message : ChatMessageVO) : Unit = {
    
  }
  
  def sendPrivateMessage(meetingID : String, message : ChatMessageVO) : Unit = {
    
  }

	// Layout
  def sendCurrentLayout(meetingID : String, requestedUserID : String) : Unit = {
    
  }
  
  def unlockLayout(meetingID : String) : Unit = {
    
  }
  
  def lockLayout(meetingID : String, userID : String, layoutID : String) : Unit = {
    
  }
	
	// Presentation
  def removePresentation(meetingID : String, presentationID : String) : Unit = {
    
  }
  
  def sendPresentationInfo(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  def gotoSlide(meetingID : String, slideNum : Int) : Unit = {
    
  }
  
  def sharePresentation(meetingID : String, presentationID : String, share : Boolean) : Unit = {
    
  }
  
  def sendCursorUpdate(meetingID : String, xPercent : Double, yPercent : Double) : Unit = {
    
  }
  
  def resizeAndMoveSlide(meetingID : String, xOffset : Double, yOffset : Double, widthRatio : Double, heightRatio : Double) : Unit = {
    
  }
	
	// Voice
  def sendVoiceUsers(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  def muteAll(meetingID : String, mute : Boolean) : Unit = {
    
  }
  
  def isRoomMuted(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  def mute(meetingID : String, userID : String, mute : Boolean) : Unit = {
    
  }
  
  def lock(meetingID : String,  userID : String, lock : Boolean) : Unit = {
    
  }
  
  def eject(meetingID : String, userID : String) : Unit = {
    
  }
	
	// Whiteboard
  def sendAnnotation(meetingID : String, a : Annotation) : Unit = {
    
  }
  
  def changePage(meetingID : String, pageNum : Int) : Unit = {
    
  }
  
  def sendAnnotationHistory(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  def clearAnnotations(meetingID : String) : Unit = {
    
  }
  
  def undoAnnotation(meetingID : String) : Unit = {
    
  }
  
  def toggleGrid(meetingID : String) : Unit = {
    
  }
  
  def setActivePresentation(meetingID : String, presentationID : String, numberOfSlides : Int) : Unit = {
    
  }
  
  def enableWhiteboard(meetingID : String, enable : Boolean) : Unit = {
    
  }
  
  def isWhiteboardEnabled(meetingID : String, requesterID : String) : Unit = {
    
  }
  
  
}

