package org.bigbluebutton.main.model.users.services
{
  import com.asfusion.mate.events.Dispatcher;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.common.Role;
  import org.bigbluebutton.core.BBB;
  import org.bigbluebutton.core.EventConstants;
  import org.bigbluebutton.core.UsersUtil;
  import org.bigbluebutton.core.events.CoreEvent;
  import org.bigbluebutton.core.managers.UserManager;
  import org.bigbluebutton.main.events.MadePresenterEvent;
  import org.bigbluebutton.main.events.ParticipantJoinEvent;
  import org.bigbluebutton.main.events.PresenterStatusEvent;
  import org.bigbluebutton.main.model.users.BBBUser;
  import org.bigbluebutton.main.model.users.Conference;
  import org.bigbluebutton.main.model.users.IMessageListener;
  
  public class MessageReceiver implements IMessageListener
  {
    public function MessageReceiver()
    {
      BBB.initConnectionManager().addMessageListener(this);
    }
    
    
    public function onMessage(messageName:String, message:Object):void
    {
      switch (messageName) {
        case "UserStatusChangeCommand":
          handleUserStatusChangeCommand(message);
          break;	
        case "UserJoinedCommand":
          handleUserJoinedChangeCommand(message);
          break;	
        case "UserLeftCommand":
          handleUserLeftCommand(message);
          break;
        case "AssignPresenterCommand":
          handleAssignPresenterCommand(message);
          break;
        
        default:
          //   LogUtil.warn("Cannot handle message [" + messageName + "]");
      }
    }
    
    private function handleAssignPresenterCommand(message:Object):void {
      assignPresenterCallback(message.newPresenterUserID, message.assignedByUserID);
    }
    
    private function assignPresenterCallback(newPresenterUserID:String, assignedByUserID:String):void {
      LogUtil.debug("assignPresenterCallback " + newPresenterUserID + ", " + assignedByUserID + "]");
      var dispatcher:Dispatcher = new Dispatcher();
      var meeting:Conference = UserManager.getInstance().getConference();
      if (meeting.amIThisUser(newPresenterUserID)) {
        meeting.setMePresenter(true);				
        var e:MadePresenterEvent = new MadePresenterEvent(MadePresenterEvent.SWITCH_TO_PRESENTER_MODE);
        e.userid = newPresenterUserID;
        e.presenterName = UsersUtil.getUserName(newPresenterUserID);
        e.assignerBy = assignedByUserID;
        
        dispatcher.dispatchEvent(e);		
        
        var roleEvent:CoreEvent = new CoreEvent(EventConstants.NEW_ROLE);
        roleEvent.message.role = Role.PRESENTER;
        dispatcher.dispatchEvent(roleEvent);
        
      } else {				
        meeting.setMePresenter(false);
        var viewerEvent:MadePresenterEvent = new MadePresenterEvent(MadePresenterEvent.SWITCH_TO_VIEWER_MODE);
        viewerEvent.userid = newPresenterUserID;
        viewerEvent.presenterName = UsersUtil.getUserName(newPresenterUserID);
        viewerEvent.assignerBy = assignedByUserID;
        
        dispatcher.dispatchEvent(viewerEvent);
        
        var newRoleEvent:CoreEvent = new CoreEvent(EventConstants.NEW_ROLE);
        newRoleEvent.message.role = Role.VIEWER;
        dispatcher.dispatchEvent(newRoleEvent);
      }
    }
    
    private function handleUserLeftCommand(message:Object):void {
      var user:BBBUser = UserManager.getInstance().getConference().getUser(message.userID);
      
      var dispatcher:Dispatcher = new Dispatcher();
      var joinEvent:ParticipantJoinEvent = new ParticipantJoinEvent(ParticipantJoinEvent.PARTICIPANT_JOINED_EVENT);
      joinEvent.userID = user.userID;
      joinEvent.join = false;
      dispatcher.dispatchEvent(joinEvent);	
      
      UserManager.getInstance().getConference().removeUser(message.userID);
    }
    
    private function handleUserJoinedChangeCommand(message:Object):void {
      var user:BBBUser = new BBBUser();
      var joinedUser:Object = message.user;
      user.userID = joinedUser.userid;
      user.name = joinedUser.name;
      user.role = joinedUser.role;
      user.externUserID = joinedUser.externUserID;
            
      UserManager.getInstance().getConference().addUser(user);
      userStatusChanged(user.userID, "hasStream", joinedUser.status.hasStream);
      userStatusChanged(user.userID, "presenter", joinedUser.status.presenter);
      userStatusChanged(user.userID, "raiseHand", joinedUser.status.raiseHand);
      
      var dispatcher:Dispatcher = new Dispatcher();
      var joinEvent:ParticipantJoinEvent = new ParticipantJoinEvent(ParticipantJoinEvent.PARTICIPANT_JOINED_EVENT);
      joinEvent.userID = user.userID;
      joinEvent.join = true;
      dispatcher.dispatchEvent(joinEvent);
    }
    
    private function handleUserStatusChangeCommand(message:Object):void {   
      userStatusChanged(message.userID, message.statusName, message.statusValue);
    }
    
    private function userStatusChanged(userID:String, statusName:String, statusValue:Object):void {
      LogUtil.debug("**** Received status change [" + userID + "," + statusName + "," + statusValue + "]")			
      UserManager.getInstance().getConference().newUserStatus(userID, statusName, statusValue);
      
      if (statusName == "presenter") {
        var e:PresenterStatusEvent = new PresenterStatusEvent(PresenterStatusEvent.PRESENTER_NAME_CHANGE);
        e.userID = userID;
        var dispatcher:Dispatcher = new Dispatcher();
        dispatcher.dispatchEvent(e);
      }      
    }
    
  }
}