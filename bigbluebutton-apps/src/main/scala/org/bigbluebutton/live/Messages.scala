package org.bigbluebutton.live

import org.bigbluebutton.conference.service.whiteboard.shapes.Annotation

object MessageValueObject {
  sealed trait ValueObject
  case class ChatMessageVO(meetingID : String, chatType : String, fromUserID : String, fromUsername : String, 
		  					fromColor : String, fromTime : String, fromTimezoneOffset : String, fromLang : String,
		  					toUserID : String, toUsername : String, message : String) extends ValueObject
  case class UserVO(meetingID : String, userID : String, username : String, role : String, 
		  			externUserID : String, raiseHand : Boolean, presenter : Boolean, hasStream : Boolean) extends ValueObject
}


object MessageIn {
  import MessageValueObject._
  
  sealed trait InMessage
  
  // Meeting
  case class CreateMeeting(meetingID : String, meetingName : String, voiceBridge : String, recorded : Boolean) extends InMessage
  case class EndMeeting(meetingID : String) extends InMessage
    
  // Users
  case class JoinUser(meetingID : String, userID : String, username : String, role : String, externUserID : String) extends InMessage
  case class LeaveUser(meetingID : String, userID : String) extends InMessage
  case class AssignPresenter(meetingID : String, newPresenterUserID : String, newPresenterName : String, assignedBy : String) extends InMessage
  case class SendUsers(meetingID : String, requesterUserID : String) extends InMessage
  case class SetUserStatus(meetingID : String, userID : String, statusName : String, statusValue : Object) extends InMessage

  // Chat
  case class SendPublicChatHistory(meetingID : String, requesterID : String) extends InMessage
  case class SendPublicMessage(meetingID : String, message : ChatMessageVO) extends InMessage
  case class SendPrivateMessage(meetingID : String, message : ChatMessageVO) extends InMessage

  // Layout
  case class SendCurrentLayout(meetingID : String, requestedUserID : String) extends InMessage
  case class UnlockLayout(meetingID : String) extends InMessage
  case class LockLayout(meetingID : String, userID : String, layoutID : String) extends InMessage

  // Presentation
  case class RemovePresentation(meetingID : String, presentationID : String) extends InMessage
  case class SendPresentationInfo(meetingID : String, requesterID : String) extends InMessage
  case class GotoSlide(meetingID : String, slideNum : Int) extends InMessage
  case class SharePresentation(meetingID : String, presentationID : String, share : Boolean) extends InMessage
  case class SendCursorUpdate(meetingID : String, xPercent : Double, yPercent : Double) extends InMessage
  case class ResizeAndMoveSlide(meetingID : String, xOffset : Double, yOffset : Double, widthRatio : Double, heightRatio : Double) extends InMessage  
  case class GetCurrentPresenter(meetingID : String, requesterUserID : String) extends InMessage

  // Voice
  case class SendVoiceUsers(meetingID : String, requesterID : String) extends InMessage
  case class MuteAll(meetingID : String, mute : Boolean) extends InMessage
  case class IsRoomMuted(meetingID : String, requesterID : String) extends InMessage
  case class Mute(meetingID : String, userID : String, mute : Boolean) extends InMessage  
  case class Lock(meetingID : String,  userID : String, lock : Boolean) extends InMessage
  case class Eject(meetingID : String, userID : String) extends InMessage
	
  // Whiteboard
  case class SendAnnotation(meetingID : String, a : Annotation) extends InMessage  
  case class ChangePage(meetingID : String, pageNum : Int) extends InMessage
  case class SendAnnotationHistory(meetingID : String, requesterID : String) extends InMessage
  case class ClearAnnotations(meetingID : String) extends InMessage
  case class UndoAnnotation(meetingID : String) extends InMessage  
  case class ToggleGrid(meetingID : String) extends InMessage  
  case class SetActivePresentation(meetingID : String, presentationID : String, numberOfSlides : Int) extends InMessage
  case class EnableWhiteboard(meetingID : String, enable : Boolean) extends InMessage
  case class IsWhiteboardEnabled(meetingID : String, requesterID : String) extends InMessage
  
}

object MessageOut {
  import MessageValueObject._
  
  sealed trait OutMessage
  case class UserJoined(meetingID : String, name : String) extends OutMessage
  case class UserLeft(meetingID : String, userID : String) extends OutMessage
  case class MeetingCreated(meetingID : String) extends OutMessage
  case class MeetingEnded(meetingID : String) extends OutMessage
  
}