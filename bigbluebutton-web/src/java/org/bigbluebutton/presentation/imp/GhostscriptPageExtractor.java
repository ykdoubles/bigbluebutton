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

import java.io.File;
import org.bigbluebutton.presentation.PageExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

public class GhostscriptPageExtractor implements PageExtractor {
	private static Logger log = LoggerFactory.getLogger(GhostscriptPageExtractor.class);
	
	private String ghostscript_exe;
	private String noPdfMarkWorkaround;
	
	public boolean extractPage(File presentationFile, File output, int page){		
		String first_page = "-dFirstPage=" + page;
		String last_page = "-dLastPage=" + page;		
		String destination = output.getAbsolutePath();
		String output_file = "-sOutputFile=" + destination;
		
		//extract that specific page and create a temp-pdf(only one page) with GhostScript
		ArrayList<String> command = new ArrayList<String>();
		command.add(ghostscript_exe);
		command.add("-sDEVICE=pdfwrite");
		command.add("-dNOPAUSE");
		command.add("-dQUIET");
		command.add("-dBATCH");
		command.add(first_page);
		command.add(last_page);
		command.add(output_file);
		command.add(noPdfMarkWorkaround);
		command.add(presentationFile.getAbsolutePath());

		log.debug("check command:" + command);
        System.out.println(command);
        return new ExternalProcessExecutor().exec(command, 60000);
	}	
	
	public void setGhostscriptExec(String exec) {
		ghostscript_exe = exec;
	}
	
	public void setNoPdfMarkWorkaround(String noPdfMarkWorkaround) {
		this.noPdfMarkWorkaround = noPdfMarkWorkaround;
	}
}
