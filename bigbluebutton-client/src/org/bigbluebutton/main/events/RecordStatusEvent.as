package org.bigbluebutton.main.events
{
	import flash.events.Event;

	public class RecordStatusEvent extends Event
	{
		public static const RECORD_STATUS_EVENT:String = "RECORD_STATUS_EVENT";
		public static const UPDATE_RECORD_STATUS:String = "UPDATE_RECORD_STATUS";
		
		public var status:Boolean; 
		
		public function RecordStatusEvent(type:String)
		{
			super(type, true, false);
		}
		
	}
}