package org.bigbluebutton.main.events
{
	import flash.events.Event;

	public class RecordStatusEvent extends Event
	{
		public static const RECORD_STATUS_EVENT:String = "RECORD_STATUS_EVENT";
		
		public var isRecording:Boolean; 
		
		public function RecordStatusEvent(type:String)
		{
			super(type, true, false);
		}
		
	}
}