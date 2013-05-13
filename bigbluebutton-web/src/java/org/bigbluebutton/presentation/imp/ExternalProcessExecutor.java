/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
* 
* Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 3.0 of the License, or (at your option) any later
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

package org.bigbluebutton.presentation.imp;

import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

/**
 * A wrapper class the executes an external command. 
 * 
 * See http://kylecartmell.com/?p=9
 * 
 * @author Richard Alam
 *
 */
public class ExternalProcessExecutor {
	private static Logger log = LoggerFactory.getLogger(ExternalProcessExecutor.class);
	
	public boolean exec(ArrayList<String> command, long timeoutMillis) {
        Timer timer = null;
        Process p = null;
        try {
            timer = new Timer(true);
            InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread());
            timer.schedule(interrupter, timeoutMillis);
            String[] final_command = command.toArray(new String[command.size()]);
            p = Runtime.getRuntime().exec(final_command);
            p.waitFor();
            return true;
        } catch(Exception e) {
        	log.info("TIMEDOUT excuting : " + command.toArray(new String[command.size()]));
            p.destroy();
        } finally {
            timer.cancel();     // If the process returns within the timeout period, we have to stop the interrupter
                                // so that it does not unexpectedly interrupt some other code later.

            Thread.interrupted();   // We need to clear the interrupt flag on the current thread just in case
                                    // interrupter executed after waitFor had already returned but before timer.cancel
                                    // took effect.
                                    //
                                    // Oh, and there's also Sun bug 6420270 to worry about here.
        }  
		return false;
	}
	

	class InterruptTimerTask extends TimerTask {
	    private Thread thread;

	    public InterruptTimerTask(Thread t) {
	        this.thread = t;
	    }

	    public void run() {
	        thread.interrupt();
	    }

	}
}
