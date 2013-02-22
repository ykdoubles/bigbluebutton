package org.bigbluebutton.live

object MessageVO {
  sealed trait ValueObject
  case class ChatMessageVO(meetingID : String, chatType : String, fromUserID : String, fromUsername : String, 
		  					fromColor : String, fromTime : String, fromTimezoneOffset : String, fromLang : String,
		  					toUserID : String, toUsername : String, message : String) extends ValueObject
  case class UserVO(meetingID : String, userID : String, username : String, role : String, 
		  			externUserID : String, raiseHand : Boolean, presenter : Boolean, hasStream : Boolean) extends ValueObject
}


object MessageIn {
  import MessageVO._
  
  sealed trait InMessage
  case class JoinUser(user : UserVO) extends InMessage  
  case class LeaveUser(meetingID : String, userID : String) extends InMessage
  case class SendUsers(meetingID : String, requesterUserID : String) extends InMessage
  case class GetCurrentPresenter(meetingID : String, requesterUserID : String) extends InMessage
  case class AssignPresenter(meetingID : String, newPresenterUserID : String, name : String, assignedBy : String)
  case class CreateMeeting(meetingID : String) extends InMessage
  case class EndMeeting(meetingID : String) extends InMessage
  case class SendChatMessage(chatMessage : ChatMessageVO) extends InMessage
  case class SendChatHistory(meetingID : String, requesterUserID : String) extends InMessage
  
}

object MessageOut {
  import MessageVO._
  
  sealed trait OutMessage
  case class UserJoined(meetingID : String, name : String) extends OutMessage
  case class UserLeft(meetingID : String, userID : String) extends OutMessage
  case class MeetingCreated(meetingID : String) extends OutMessage
  case class MeetingEnded(meetingID : String) extends OutMessage
  
}