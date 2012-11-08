package org.bigbluebutton.main.model.users.services
{
  import org.bigbluebutton.core.BBB;
  import org.bigbluebutton.core.managers.ConnectionManager;

  public class MessageSender
  {

    public function assignPresenter(userid:String, name:String, assignedBy:String):void {
      trace("Assigning presenter to [" + name + "]");
      var message:Object = new Object();
      message["newPresenterUserID"] = userid;
      message["assigneByUserID"] = assignedBy;
      
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.assignPresenter", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        },
        message
      );
    }
    
    public function queryForParticipants():void {  
      trace("queryForParticipants");
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.getParticipants", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        }
      );
    }  
    
    public function raiseHand(userID:String, raise:Boolean):void {
      trace("raise hand");
      var message:Object = new Object();
      message["userID"] = userID;
      message["statusName"] = "raiseHand";
      message["statusValue"] = raise;
      
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.setParticipantStatus", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        },
        message
      );
    }
    
    public function addStream(userID:String, streamName:String):void {
      trace("addStream");
      var message:Object = new Object();
      message["userID"] = userID;
      message["statusName"] = "hasStream";
      message["statusValue"] = "true,stream=" + streamName;
      
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.setParticipantStatus", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        },
        message
      );
    }
    
    public function removeStream(userID:String, streamName:String):void {
      trace("removeStream");
      var message:Object = new Object();
      message["userID"] = userID;
      message["statusName"] = "hasStream";
      message["statusValue"] = "false,stream=" + streamName;
      
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.setParticipantStatus", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        },
        message
      );      
    }
    
    public function kickUser(userID:String):void {
      trace("kickUser");
      var message:Object = new Object();
      message["userID"] = userID;
      
      var _nc:ConnectionManager = BBB.initConnectionManager();
      _nc.sendMessage("participants.kickUser", 
        function(result:String):void { // On successful result
          trace(result); 
        },	                   
        function(status:String):void { // status - On error occurred
          trace(status); 
        },
        message
      );      
    }    
    
  }
}