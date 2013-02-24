package org.bigbluebutton.live

class User(val userID : String, val username : String, val role : String, val externUserID : String) {

  var isPresenter : Boolean = false
  var voiceUserID : String = ""
  var raisedHand : Boolean = false
  var hasStream : String = ""
}