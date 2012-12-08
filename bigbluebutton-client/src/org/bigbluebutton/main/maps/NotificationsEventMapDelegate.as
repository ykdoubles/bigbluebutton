package org.bigbluebutton.main.maps
{
  import com.asfusion.mate.events.Dispatcher;
  import com.notifications.Notification;
  
  import flash.external.ExternalInterface;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.core.EventConstants;
  import org.bigbluebutton.core.UsersUtil;
  import org.bigbluebutton.core.events.CoreEvent;
  import org.bigbluebutton.core.events.GrowlEvent;
  import org.bigbluebutton.core.managers.UserManager;
  import org.bigbluebutton.main.events.BBBEvent;
  import org.bigbluebutton.main.events.UserJoinedEvent;
  import org.bigbluebutton.main.events.UserLeftEvent;
  import org.bigbluebutton.main.model.users.BBBUser;

  public class NotificationsEventMapDelegate
  {   
    public function handleSwitchToNewRoleEvent(event:CoreEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.NEW_ROLE;
      payload.role = event.message.role;
         
    }

    public function handleStartPrivateChatEvent(event:CoreEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.START_PRIVATE_CHAT;
      payload.chatWith = UsersUtil.internalUserIDToExternalUserID(event.message.chatWith);
              
    }
    
    public function handleGetMyRoleResponse(event:CoreEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.GET_MY_ROLE_RESP;
      payload.myRole = event.message.myRole;
              
    }

    public function handleUserJoinedVoiceEvent(event:BBBEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.USER_JOINED_VOICE;
      payload.userID = UsersUtil.internalUserIDToExternalUserID(event.payload.userID);
      
      
    }
    
    public function handleUserVoiceMutedEvent(event:BBBEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.USER_MUTED_VOICE;
      payload.userID = UsersUtil.internalUserIDToExternalUserID(event.payload.userID);
      payload.muted = event.payload.muted;
      
      
    }
    
    public function handleUserVoiceLockedEvent(event:BBBEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.USER_LOCKED_VOICE;
      payload.userID = UsersUtil.internalUserIDToExternalUserID(event.payload.userID);
      payload.locked = event.payload.locked;
      
      
    }
    
    public function handleUserVoiceLeftEvent(event:BBBEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.USER_LEFT_VOICE;
      payload.userID = UsersUtil.internalUserIDToExternalUserID(event.payload.userID);
      
      
    }
    
    public function handleSwitchedLayoutEvent(layoutID:String):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.SWITCHED_LAYOUT;
      payload.layoutID = layoutID;
      
    }
        
    public function handleNewPublicChatEvent(event:CoreEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.NEW_PUBLIC_CHAT;
      payload.chatType = event.message.chatType;      
      payload.fromUsername = event.message.fromUsername;
      payload.fromColor = event.message.fromColor;
      payload.fromLang = event.message.fromLang;
      payload.fromTime = event.message.fromTime;      
      payload.fromTimezoneOffset = event.message.fromTimezoneOffset;
      payload.message = event.message.message;
      
      // Need to convert the internal user id to external user id in case the 3rd-party app passed 
      // an external user id for it's own use.
      payload.fromUserID = UsersUtil.internalUserIDToExternalUserID(event.message.fromUserID);
      
      var title:String = "Public chat message from " + event.message.fromUsername;
      var message:String = event.message.message;
//      notify(message, title);
      
      var gEvent:GrowlEvent = new GrowlEvent(GrowlEvent.NOTIFICATION_EVENT);
      gEvent.title = title;
      gEvent.color = 0x00CCEE;
      gEvent.action = "Click Me";
      gEvent.description = message;
      gEvent.notification = message;
      
      var gDispatcher:Dispatcher = new Dispatcher();
//      gDispatcher.dispatchEvent(gEvent);
    }
    
    public function handleNewPrivateChatEvent(event:CoreEvent):void {
      var payload:Object = new Object();
      payload.eventName = EventConstants.NEW_PRIVATE_CHAT;
      payload.chatType = event.message.chatType;      
      payload.fromUsername = event.message.fromUsername;
      payload.fromColor = event.message.fromColor;
      payload.fromLang = event.message.fromLang;
      payload.fromTime = event.message.fromTime;    
      payload.fromTimezoneOffset = event.message.fromTimezoneOffset;
      payload.toUsername = event.message.toUsername;
      payload.message = event.message.message;
      
      // Need to convert the internal user id to external user id in case the 3rd-party app passed 
      // an external user id for it's own use.
      payload.fromUserID = UsersUtil.internalUserIDToExternalUserID(event.message.fromUserID);
      payload.toUserID = UsersUtil.internalUserIDToExternalUserID(event.message.toUserID);
      
      var title:String = "Private chat message from " + event.message.fromUsername;
      var message:String = event.message.message;
//      notify(message, title);
    }
        
    public function handleUserJoinedEvent(event:UserJoinedEvent):void {
      var payload:Object = new Object();
      var user:BBBUser = UserManager.getInstance().getConference().getUser(event.userID);
      
      if (user == null) {
        LogUtil.warn("[ExternalApiCall:handleParticipantJoinEvent] Cannot find user with ID [" + event.userID + "]");
        return;
      }
      
      payload.eventName = EventConstants.USER_JOINED;
      payload.userID = user.userID;
      payload.userName = user.name;        
      
      var title:String = "New user joined";
      var message:String = "User " + user.name + " has joined.";
//      notify(message, title);
    }    

    public function handleUserLeftEvent(event:UserLeftEvent):void {
      var payload:Object = new Object();
      var user:BBBUser = UserManager.getInstance().getConference().getUser(event.userID);
      
      if (user == null) {
        LogUtil.warn("[ExternalApiCall:handleParticipantJoinEvent] Cannot find user with ID [" + event.userID + "]");
        return;
      }
      
      payload.eventName = EventConstants.USER_LEFT;
      payload.userID = user.userID;
      
      var title:String = "User left";
      var message:String = "User " + user.name + " has left.";
//      notify(message, title);        
    }  
    
    private function notify(message:String, title:String, iconClass:Class=null):void {
      Notification.show(message, title, 2500, "bottomright", iconClass, true);      
    }
  }
}