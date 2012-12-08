package org.bigbluebutton.core.events
{
  import flash.events.Event;
  
  public class GrowlEvent extends Event
  {
    public static const NOTIFICATION_EVENT:String = "growl notification event";
    
    public var title:String;
    public var description:String
    public var action:String;
    public var color:int;
    public var notification:String;
      
    public function GrowlEvent(type:String, bubbles:Boolean=true, cancelable:Boolean=false)
    {
      super(type, bubbles, cancelable);
    }
  }
}