package org.bigbluebutton.live

object MessageVO {

  case class PublicChatMessage(meetingID : String, chatType : String, 
		fromUserID : String, fromUsername : String, fromColor : String,
		fromTime : String,  fromTimezoneOffset : Long, fromLang : String, 	 
		toUserID: String, toUsername : String, message : String )
  case class PrivateChatMessage(meetingID : String, chatType : String,   
		fromUserID : String, fromUsername : String, fromColor : String,
		fromTime : Double, fromTimezoneOffset : Long, fromLang : String,	  
		toUserID : String, toUsername : String, message : String )
}