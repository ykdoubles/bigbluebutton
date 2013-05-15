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
package org.bigbluebutton.web.services

import java.util.concurrent.*;
import java.io.FileOutputStream;
import java.lang.InterruptedException
import org.bigbluebutton.presentation.DocumentConversionService
import org.bigbluebutton.presentation.UploadedPresentation

class PresentationService {

    static transactional = false
	DocumentConversionService documentConversionService
	def presentationDir
	def testConferenceMock
	def testRoomMock
	def testPresentationName
	def testUploadedPresentation
	def defaultUploadedPresentation

	public static String SLIDE_RESOURCE = "slide";
	public static String THUMBNAIL_RESOURCE = "thumbnail";
	public static String TEXTFILE_RESOURCE = "textfile";
	
    def deletePresentation = {meetingID, filename ->
    		def directory = new File(roomDirectory(meetingID).absolutePath + File.separatorChar + filename)
    		deleteDirectory(directory) 
	}
	
	def deleteDirectory = {directory ->
		log.debug "delete = ${directory}"
		/**
		 * Go through each directory and check if it's not empty.
		 * We need to delete files inside a directory before a
		 * directory can be deleted.
		**/
		File[] files = directory.listFiles();				
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				deleteDirectory(files[i])
			} else {
				files[i].delete()
			}
		}
		// Now that the directory is empty. Delete it.
		directory.delete()	
	}
	
	def listPresentations = {meetingID ->
		def presentationsList = []
		def directory = roomDirectory(meetingID)
		log.debug "directory ${directory.absolutePath}"
		if( directory.exists() ){
			directory.eachFile(){ file->
				System.out.println(file.name)
				if( file.isDirectory() )
					presentationsList.add( file.name )
			}
		}	
		return presentationsList
	}
	
	public File uploadedPresentationDirectory(String meetingID, String presentationID) {
		File dir = new File(roomDirectory(meetingID).absolutePath + File.separatorChar + presentationID)
		println "Uploaded presentation ${presentationID} for meeting ${meetingID} to dir ${dir.absolutePath}"

		/* If the presentation name already exist, delete it. We should provide a check later on to notify user
			that there is already a presentation with that name. */
		if (dir.exists()) deleteDirectory(dir)		
		
		dir.mkdirs()

		assert dir.exists()
		return dir
	}

	/**
	* meetingID: Internal MeetingID
	* presentationName: Name of the presentation
	*/
	public UploadedPresentation storePresentation(String meetingID, String originalFilename, File uploadFile){
		UploadedPresentation uploadedPres = createUploadedPresentation(meetingID, originalFilename);
		uploadFile.transferTo(uploadedPres.getUploadedFile());
		return uploadedPres;
	}

	public UploadedPresentation storePresentation(String meetingID, String originalFilename, byte[] rawData){
		UploadedPresentation uploadedPres = createUploadedPresentation(meetingID, originalFilename);

      	FileOutputStream fos = new FileOutputStream(uploadedPres.getUploadedFile());
    	fos.write(rawData);
    	fos.flush();
    	fos.close();

    	return uploadedPres;
	}

	public UploadedPresentation storePresentation(String meetingID, String originalFilename, String url){
		UploadedPresentation uploadedPres = createUploadedPresentation(meetingID, originalFilename);

      	BufferedOutputStream out = null;
      	try {
	      out = new BufferedOutputStream(new FileOutputStream(uploadedPres.getUploadedFile()));
	      out << new URL(url).openStream();
	    } finally {
	      if (out != null) {
	        out.close();
	      }
	    }

    	return uploadedPres;
	}

	private UploadedPresentation createUploadedPresentation(String meetingID, String originalFilename){
		String presentationName = getNameWithoutExtension(originalFilename);
		String extension = getExtensionType(originalFilename);

		UploadedPresentation uploadedPres = new UploadedPresentation(meetingID, presentationName);
      	uploadedPres.setOriginalFilename(originalFilename);

      	File uploadDir = uploadedPresentationDirectory(uploadedPres.getMeetingID(), uploadedPres.getPresentationID());
      	File presFile = new File(uploadDir.absolutePath + File.separatorChar + uploadedPres.getPresentationID() + "." + extension);
      	uploadedPres.setUploadedFile(presFile);

      	presentationsByName.put(presentationName,presentationID);
      	return uploadedPres;
	}

	private String getNameWithoutExtension(String filename){
		return filename.substring(0, filename.lastIndexOf("."));
	}

	private String getExtensionType(String filename){
		return filename.substring(filename.lastIndexOf(".") + 1);
	}
	
	def processUploadedPresentation = {uploadedPres ->	
		// Run conversion on another thread.
		new Timer().runAfter(1000) 
		{
			documentConversionService.processDocument(uploadedPres)
		}
	}
 	

	public File showResource(String meetingID, String presentationID, String slideNumber, String type){
		String resourceURL = roomDirectory(meetingID).absolutePath + File.separatorChar + presentationID + File.separatorChar;
		if(type.equalsIgnoreCase(SLIDE_RESOURCE)){
			resourceURL = resourceURL + "slide-${slideNumber}.swf";
		}else if(type.equalsIgnoreCase(THUMBNAIL_RESOURCE)){
			resourceURL = resourceURL + "thumbnails" + File.separatorChar + "thumb-${slideNumber}.png";
		}else if(type.equalsIgnoreCase(TEXTFILE_RESOURCE)){
			resourceURL = resourceURL + "textfiles" + File.separatorChar + "slide-${slideNumber}.txt"
		}else{
			log.debug("Unknown resource type");
			return null;
		}

		return new File(resourceURL);
	}
	
	def numberOfThumbnails = {meetingID, name ->
		def thumbDir = new File(roomDirectory(room).absolutePath + File.separatorChar + name + File.separatorChar + "thumbnails")
		thumbDir.listFiles().length
	}
	
	def numberOfTextfiles = {conf, room, name ->
		log.debug roomDirectory(room).absolutePath + File.separatorChar + name + File.separatorChar + "textfiles"
		def textfilesDir = new File(roomDirectory(room).absolutePath + File.separatorChar + name + File.separatorChar + "textfiles")
		textfilesDir.listFiles().length
	}
	
	def roomDirectory = { meetingID ->
		return new File(presentationDir + File.separatorChar + meetingID + File.separatorChar + meetingID)
	}

	def testConversionProcess() {
		File presDir = new File(roomDirectory(testRoomMock).absolutePath + File.separatorChar + testPresentationName)
		
		if (presDir.exists()) {
			File pres = new File(presDir.getAbsolutePath() + File.separatorChar + testUploadedPresentation)
			if (pres.exists()) {
				UploadedPresentation uploadedPres = new UploadedPresentation(testConferenceMock, testRoomMock, testPresentationName);
				uploadedPres.setUploadedFile(pres);
				// Run conversion on another thread.
				new Timer().runAfter(1000) 
				{
					documentConversionService.processDocument(uploadedPres)
				}
			} else {
				log.error "${pres.absolutePath} does NOT exist"
			}			
		} else {
			log.error "${presDir.absolutePath} does NOT exist."
		}
		
	}
	
}	

/*** Helper classes **/
import java.io.FilenameFilter;
import java.io.File;
class PngFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".png"));
    }
}
