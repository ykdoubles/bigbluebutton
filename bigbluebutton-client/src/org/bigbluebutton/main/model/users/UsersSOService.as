/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
*
* Copyright (c) 2010 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 2.1 of the License, or (at your option) any later
* version.
*
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* 
*/
package org.bigbluebutton.main.model.users {
	import com.asfusion.mate.events.Dispatcher;
	
	import flash.events.AsyncErrorEvent;
	import flash.events.NetStatusEvent;
	import flash.net.NetConnection;
	import flash.net.Responder;
	import flash.net.SharedObject;
	
	import org.bigbluebutton.common.LogUtil;
	import org.bigbluebutton.common.Role;
	import org.bigbluebutton.core.BBB;
	import org.bigbluebutton.core.EventConstants;
	import org.bigbluebutton.core.UsersUtil;
	import org.bigbluebutton.core.events.CoreEvent;
	import org.bigbluebutton.core.managers.ConnectionManager;
	import org.bigbluebutton.core.managers.UserManager;
	import org.bigbluebutton.main.events.BBBEvent;
	import org.bigbluebutton.main.events.LogoutEvent;
	import org.bigbluebutton.main.events.MadePresenterEvent;
	import org.bigbluebutton.main.events.ParticipantJoinEvent;
	import org.bigbluebutton.main.events.PresenterStatusEvent;
	import org.bigbluebutton.main.model.ConferenceParameters;
	import org.bigbluebutton.main.model.users.events.ConnectionFailedEvent;
	import org.bigbluebutton.main.model.users.events.RoleChangeEvent;

	public class UsersSOService {
		public static const NAME:String = "ViewersSOService";
		public static const LOGNAME:String = "[ViewersSOService]";
		
		private var _participantsSO : SharedObject;
		private static const SO_NAME : String = "participantsSO";
		private static const STATUS:String = "_STATUS";
		
    private var _connectionManager:ConnectionManager;
        
		private var _room:String;
		private var _applicationURI:String;
		
		private var dispatcher:Dispatcher;
				
		public function UsersSOService(uri:String) {			
			_applicationURI = uri;
      _connectionManager = BBB.initConnectionManager();
      _connectionManager.setUri(uri);
			dispatcher = new Dispatcher();
		}
		
				
		public function kickUser(userid:String):void{
			_participantsSO.send("kickUserCallback", userid);
		}
		
		public function kickUserCallback(userid:String):void{
			if (UserManager.getInstance().getConference().amIThisUser(userid)){
				dispatcher.dispatchEvent(new LogoutEvent(LogoutEvent.USER_LOGGED_OUT));
			}
		}
		
	}
}