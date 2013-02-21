package org.bigbluebutton.live

object MessageIn {
  sealed trait InMessage
  case class JoinUser(meetingID : String, name : String) extends InMessage  
  case class LeaveUser(meetingID : String, userID : String) extends InMessage
  case class CreateMeeting(meetingID : String) extends InMessage
  case class EndMeeting(meetingID : String) extends InMessage
}

object MessageOut {
  sealed trait OutMessage
  case class UserJoined(meetingID : String, name : String) extends OutMessage
  case class UserLeft(meetingID : String, userID : String) extends OutMessage
  case class MeetingCreated(meetingID : String) extends OutMessage
  case class MeetingEnded(meetingID : String) extends OutMessage
  
}