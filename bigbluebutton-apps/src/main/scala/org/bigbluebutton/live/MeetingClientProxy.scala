package org.bigbluebutton.live

import scala.actors.Actor
import scala.actors.Actor._

class MeetingClientProxy extends Actor {
  	def act() = {
	  loop {
	    react {
	      case _ => // do nothing
	    }
	  }
  	}
}