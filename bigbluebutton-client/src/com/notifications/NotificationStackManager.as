/**
 * @author Arnaud FOUCAL - http://afoucal.free.fr - afoucal@free.fr
 * 
 * @licence
 * This file is part of the component called Flex Notification.
 * It is delivered under the CC Attribution 3.0 Unported licence (http://creativecommons.org/licenses/by/3.0/) and under the following condition:
 *
 *		Mention the name and the url of the author in a part of your product that is visible to the user ("About"/"Credits" section, documentation...)
 *	
 * This condition can be waived:
 * - if you get permission from the copyright holder and purchase a non restrictive licence.
 * - if you contribute to the projet by sharing your enhancements.
 * Please contact me at afoucal@free.fr.
 * 
 */


package com.notifications
{
	
	public class NotificationStackManager 
	{
		private static var _instance:NotificationStackManager;

		
		public function NotificationStackManager( type:PrivateStackManager ) 
		{
			if ( type == null )
				throw new Error("Error: NotificationStackManager is a singleton.");
		}
		
		
		/**
		 * Get the instance of the Singleton so that properties and methods can be called
		 */
		public static function getInstance():NotificationStackManager
		{
			if (_instance == null)
				_instance = new NotificationStackManager(new PrivateStackManager());
			
			return _instance;
		}
		
		
		/**
		 * The number of currently displayed Notification
		 */
		private var _count:int = 0;
		public function get count():int { return _count; }
		
		
		/**
		 * The level of the last Notification in the stack
		 * The property is reset if Notification goes out of the parent
		 */
		private var _stackTop:int = 1;
		public function get stackTop():int { return _stackTop; }
		public function set stackTop(value:int):void 
		{
			// only increments the stackTop.
			// used by the Notification when a new notification is added to the stack
			if (value >= _stackTop )
				_stackTop++;
		}

		
		/**
		 * Add a notificaton to the counters count and stackTop
		 * @param	notification
		 */
		public function add(notification:Notification):void 
		{
			_count++;
		}
		
		
		/**
		 * Remove a notification the main counter 'count'.
		 * If count is equal to zero, the stack is reset
		 */
		public function remove():void 
		{
			_count--;
			
			// reset the top of the stack when all notifications have been removed
			if ( _count <= 0 )
				resetStack();
		}
		
		
		/**
		 * Reset the stack
		 */
		public function resetStack() :void
		{
			_stackTop = 1;
		}
		
	}
	
}

internal class PrivateStackManager
{
	public function PrivateStackManager()	{}
}