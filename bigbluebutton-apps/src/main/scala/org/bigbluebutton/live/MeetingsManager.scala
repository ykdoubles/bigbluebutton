package org.bigbluebutton.live

import scala.actors.Actor
import scala.actors.Actor._

class MeetingsManager extends Actor {
  	def act() = {
	  loop {
	    react {
	      case _ => println("Do nothing")
	    }
	  }
  	}
}