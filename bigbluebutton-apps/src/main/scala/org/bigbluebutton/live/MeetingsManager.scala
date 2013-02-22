package org.bigbluebutton.live

import scala.actors.Actor
import scala.actors.Actor._
import org.bigbluebutton.live.MessageIn._
import scala.collection.immutable.HashMap

class MeetingsManager extends Actor {
  private val meetings = new HashMap[String, Meeting]
  
  def act() = {
    loop {
      react {
        case createMeeting : CreateMeeting => handleCreateMeetingMessage(createMeeting)
        case endMeeting : EndMeeting => handleEndMeetingMessage(endMeeting)
        case joinUser : JoinUser => handleJoinUserMessage(joinUser)
        case leaveUser : LeaveUser => handleLeaveUserMessage(leaveUser)
        case _ => println("Do nothing")
      }
    }
  }
 
  private def handleCreateMeetingMessage(msg : CreateMeeting) : Unit = {
    meetings.get(msg.meetingID) match {
      case Some(m) => m ! msg
      case None => {
  	      
      }
  	}
  }
  	
  private def handleEndMeetingMessage(endMeeting : EndMeeting) : Unit = {
  	  
  }
  	
  private def handleJoinUserMessage(joinUser : JoinUser) : Unit = {
  	  
  }
  	
  private def handleLeaveUserMessage(leaveUser : LeaveUser) : Unit = {
  	  
  }
}