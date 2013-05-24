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
package org.bigbluebutton.web.controllers

import grails.converters.*
import org.bigbluebutton.web.services.PresentationService
import org.bigbluebutton.presentation.UploadedPresentation
import java.util.Hashtable;
import org.apache.commons.lang.StringUtils;

class PresentationController {
  PresentationService presentationService
  
  def index = {
    println 'in PresentationController index'
    render(view:'upload-file') 
  }
  
  def list = {						      				
    def f = confInfo()
    println "conference info ${f.meeting_id}"
    def presentationsList = presentationService.listPresentations(f.meeting_id)

    if (presentationsList) {
      withFormat {				
        xml {
          render(contentType:"text/xml") {
            conference(meetingID:f.meeting_id) {
              presentations {
                for (s in presentationsList) {
                  presentation(name:s)
                }
              }
            }
          }
        }
      }
    } else {
      render(view:'upload-file')
    }
  }

  def delete = {		
    def filename = params.presentation_id
    def f = confInfo()
    presentationService.deletePresentation(f.meeting_id, filename)
    flash.message = "file ${filename} removed" 
    redirect( action:list )
  }

  def upload = {		
    println 'PresentationController:upload'
    def file = request.getFile('fileUpload')
		if(file && !file.empty) {
			flash.message = 'Your file has been uploaded'

			def presentationName = params.presentation_name
      def originalFilename = file.getOriginalFilename()
      
      log.debug "Uploaded presentation name : $presentationName"
      UploadedPresentation uploadedPres = presentationService.storePresentation(params.meetingID, originalFilename, file.getBytes());
			presentationService.processUploadedPresentation(uploadedPres)							             			     	
		} else {
			flash.message = 'file cannot be empty'
		}
    //redirect( action:list)
    return [];
  }

  def testConversion = {
    presentationService.testConversionProcess();
  }

  //TODO: Needs update to use meetingID
  //handle external presentation server 
  def delegate = {		
    println '\nPresentationController:delegate'
    
    def presentation_name = request.getParameter('presentation_name')
    def conference = request.getParameter('conference')
    def room = request.getParameter('room')
    def returnCode = request.getParameter('returnCode')
    def totalSlides = request.getParameter('totalSlides')
    def slidesCompleted = request.getParameter('slidesCompleted')
    
    presentationService.processDelegatedPresentation(conference, room, presentation_name, returnCode, totalSlides, slidesCompleted)
    redirect( action:list)
  }
  
  def showSlide = {
    def presentationID;
    def meetingID;
    def slideNumber;

    if (!StringUtils.isEmpty(params.presentation_id)&&!StringUtils.isEmpty(params.meeting_id)&&!StringUtils.isEmpty(params.slide_number)) {
      presentationID = params.presentation_id
      meetingID = params.meeting_id
      slideNumber = params.slide_number
    }else if (!StringUtils.isEmpty(params.conference)&&!StringUtils.isEmpty(params.room)&&!StringUtils.isEmpty(params.id)&&!StringUtils.isEmpty(params.presentation_name)) {
      meetingID = params.room;
      presentationID = presentationService.getPresentationIDByName(params.presentation_name);
      slideNumber = params.id;
    }else {
      System.out.println("Incorrect parameters for getting slide");
      return null;
    }

    InputStream is = null;
    try {
      def pres = presentationService.showResource(meetingID, presentationID, slideNumber, PresentationService.SLIDE_RESOURCE);
      if (pres.exists()) {
        def bytes = pres.readBytes()
        response.addHeader("Cache-Control", "no-cache")
        response.contentType = 'application/x-shockwave-flash'
        response.outputStream << bytes;
      }	
    } catch (IOException e) {
      System.out.println("Error reading file.\n" + e.getMessage());
    }
    
    return null;
  }
  
  def showThumbnail = {
    def presentationID;
    def meetingID;
    def slideNumber;

    if (!StringUtils.isEmpty(params.presentation_id)&&!StringUtils.isEmpty(params.meeting_id)&&!StringUtils.isEmpty(params.slide_number)) {
      presentationID = params.presentation_id
      meetingID = params.meeting_id
      slideNumber = params.slide_number
    }else if (!StringUtils.isEmpty(params.conference)&&!StringUtils.isEmpty(params.room)&&!StringUtils.isEmpty(params.id)&&!StringUtils.isEmpty(params.presentation_name)) {
      meetingID = params.room;
      presentationID = presentationService.getPresentationIDByName(params.presentation_name);
      slideNumber = params.id;
    }else {
      System.out.println("Incorrect parameters for getting thumbnail");
      return null;
    }

    InputStream is = null;
    try {
      def pres = presentationService.showResource(meetingID, presentationID, slideNumber, PresentationService.THUMBNAIL_RESOURCE);
      if (pres.exists()) {
        def bytes = pres.readBytes()
        response.addHeader("Cache-Control", "no-cache")
        response.contentType = 'image'
        response.outputStream << bytes;
      } else {
        println "$pres does not exist."
      }
    } catch (IOException e) {
      println("Error reading file.\n" + e.getMessage());
    }
    
    return null;
  }
  
  def showTextfile = {
	  def presentationID;
    def meetingID;
    def slideNumber;

    if (!StringUtils.isEmpty(params.presentation_id)&&!StringUtils.isEmpty(params.meeting_id)&&!StringUtils.isEmpty(params.slide_number)) {
      presentationID = params.presentation_id
      meetingID = params.meeting_id
      slideNumber = params.slide_number
    }else if (!StringUtils.isEmpty(params.conference)&&!StringUtils.isEmpty(params.room)&&!StringUtils.isEmpty(params.id)&&!StringUtils.isEmpty(params.presentation_name)) {
      meetingID = params.room;
      presentationID = presentationService.getPresentationIDByName(params.presentation_name);
      slideNumber = params.id;
    }else {
      System.out.println("Incorrect parameters for getting textfile");
      return null;
    }

	  InputStream is = null;
	  try {
		def pres = presentationService.showResource(meetingID, presentationID, slideNumber, PresentationService.TEXTFILE_RESOURCE);
		if (pres.exists()) {
		  def bytes = pres.readBytes()
		  response.addHeader("Cache-Control", "no-cache")
		  response.contentType = 'plain/text'
		  response.outputStream << bytes;
		} else {
		  println "$pres does not exist."
		}
	  } catch (IOException e) {
		println("Error reading file.\n" + e.getMessage());
	  }
	  
	  return null;
  }

  def numberOfSlides = {
    def presentationID;
    def meetingID;
    //for backup compability
    boolean oldAPI = false;
    
    if (!StringUtils.isEmpty(params.presentation_id)&&!StringUtils.isEmpty(params.meeting_id)) {
      presentationID = params.presentation_id
      meetingID = params.meeting_id
    }else if (!StringUtils.isEmpty(params.conference)&&!StringUtils.isEmpty(params.room)&&!StringUtils.isEmpty(params.presentation_name)) {
      meetingID = params.room;
      presentationID = presentationService.getPresentationIDByName(params.presentation_name);
      oldAPI = true;
    }else {
      System.out.println("Incorrect parameters for getting slides");
      return null;
    }
    
    def numThumbs = presentationService.numberOfThumbnails(meetingID, presentationID)

    response.addHeader("Cache-Control", "no-cache")
    if(oldAPI){
      withFormat {						
        xml {
          render(contentType:"text/xml") {
            conference(id:meetingID, room:meetingID) {
              presentation(name:params.presentation_name) {
                slides(count:numThumbs) {
                  for (def i = 1; i <= numThumbs; i++) {
                    slide(number:"${i}", name:"slide/${i}", thumb:"thumbnail/${i}", textfile:"textfile/${i}")
                  }
                }
              }
            }
          }
        }
      }
    }else {
      withFormat {            
        xml {
          render(contentType:"text/xml") {
            meeting(id:meetingID) {
              presentation(id:presentationID) {
                slides(count:numThumbs) {
                  for (def i = 1; i <= numThumbs; i++) {
                    slide(number:"${i}", name:"slide/${i}", thumb:"thumbnail/${i}", textfile:"textfile/${i}")
                  }
                }
              }
            }
          }
        }
      } 
    }


  }
    
  def numberOfThumbnails = {
    def filename = params.presentation_name
    def f = confInfo()
    def numThumbs = presentationService.numberOfThumbnails(f.conference, f.room, filename)
      withFormat {				
        xml {
          render(contentType:"text/xml") {
            conference(id:f.conference, room:f.room) {
              presentation(name:filename) {
                thumbnails(count:numThumbs) {
                  for (def i=0;i<numThumbs;i++) {
                      thumb(name:"thumbnails/${i}")
                    }
                }
              }
            }
          }
        }
      }		
  }
  
  def numberOfTextfiles = {
	  def filename = params.presentation_name
	  def f = confInfo()
	  def numFiles = presentationService.numberOfTextfiles(f.conference, f.room, filename)
		withFormat {
		  xml {
			render(contentType:"text/xml") {
			  conference(id:f.conference, room:f.room) {
				presentation(name:filename) {
				  textfiles(count:numFiles) {
					for (def i=0;i<numFiles;i++) {
						textfile(name:"textfiles/${i}")
					  }
				  }
				}
			  }
			}
		  }
		}
	}
  
  def confInfo = {
      def fname = session["fullname"]
      def rl = session["role"]
      def mid = session["room"]
      println "Conference info: ${mid}"
    return [meeting_id:mid]
  }
}

