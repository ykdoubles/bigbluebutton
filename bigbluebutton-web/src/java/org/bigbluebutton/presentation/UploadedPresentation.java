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

package org.bigbluebutton.presentation;

import java.io.File;

public final class UploadedPresentation {
	private final String presentationID;
	private final String meetingID;
	private final String presentationName;
	private File uploadedFile;
	private String originalFilename;
	private String fileType = "unknown";
	private int numberOfPages = 0;
	private boolean lastStepSuccessful = false;
	
	public UploadedPresentation(String meetingID, String presentationName) {
		this.presentationID = Long.toString(System.currentTimeMillis());
		this.meetingID = meetingID;
		this.presentationName = presentationName;
	}

	public String getPresentationID(){
		return presentationID;
	}

	public String getMeetingID(){
		return meetingID;
	}

	public String getPresentationName(){
		return presentationName;
	}

	public void setOriginalFilename(String originalFilename){
		this.originalFilename = originalFilename;
	}

	public String getOriginalFilename(){
		return originalFilename;
	}

	public File getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(File uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public boolean isLastStepSuccessful() {
		return lastStepSuccessful;
	}

	public void setLastStepSuccessful(boolean lastStepSuccessful) {
		this.lastStepSuccessful = lastStepSuccessful;
	}
	
	
}
