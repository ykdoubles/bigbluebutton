package org.bigbluebutton.live

import scala.actors.Actor
import scala.actors.Actor._

import org.bigbluebutton.live.MessageIn._
import org.bigbluebutton.conference.IClientMessagingGateway

object Meeting {
  case class MeetingVO(meetingID : String, meetingName : String, voiceBridge : String, recorded : Boolean)
}

class Meeting(meetingVO : Meeting.MeetingVO, voiceGW : IVoiceGateway, recorderGW : IRecordingGateway, clientGW : IClientMessagingGateway) extends Actor {
  
  private val users = new Users
 
  def act() = {
    loop {
      react {
        case createMeeting : CreateMeeting => handleCreateMeetingMessage(createMeeting)
        case endMeeting : EndMeeting => handleEndMeetingMessage(endMeeting)
        case joinUser : JoinUser => handleJoinUserMessage(joinUser)
        case leaveUser : LeaveUser => handleLeaveUserMessage(leaveUser)
        case assignPresenter : AssignPresenter => handleAssignPresenterMessage(assignPresenter)
        case sendUsers : SendUsers => handleSendUsersMessage(sendUsers)
        case setUserStatus : SetUserStatus => handleSetUserStatusMessage(setUserStatus)
        case sendPublicChatHistory : SendPublicChatHistory => handleSendPublicChatHistoryMessage(sendPublicChatHistory)
        case sendPublicMessage : SendPublicMessage => handleSendPublicMessageMessage(sendPublicMessage)
        case sendPrivateMessage : SendPrivateMessage => handleSendPrivateMessageMessage(sendPrivateMessage)
        case sendCurrentLayout : SendCurrentLayout => handleSendCurrentLayoutMessage(sendCurrentLayout)
        case unlockLayout : UnlockLayout => handleUnlockLayoutMessage(unlockLayout)
        case lockLayout : LockLayout => handleLockLayoutMessage(lockLayout)
        case removePresentation : RemovePresentation => handleRemovePresentationMessage(removePresentation)
        case sendPresentationInfo : SendPresentationInfo => handleSendPresentationInfoMessage(sendPresentationInfo)
        case gotoSlide : GotoSlide => handleGotoSlideMessage(gotoSlide)
        case sharePresentation: SharePresentation => handleSharePresentationMessage(sharePresentation)
        case sendCursorUpdate : SendCursorUpdate => handleSendCursorUpdateMessage(sendCursorUpdate)
        case resizeAndMoveSlide : ResizeAndMoveSlide => handleResizeAndMoveSlideMessage(resizeAndMoveSlide)
        case getCurrentPresenter : GetCurrentPresenter => handleGetCurrentPresenterMessage(getCurrentPresenter)
        case sendVoiceUsers : SendVoiceUsers => handleSendVoiceUsersMessage(sendVoiceUsers)
        case muteAll : MuteAll => handleMuteAllMessage(muteAll)
        case isRoomMuted : IsRoomMuted => handleIsRoomMutedMessage(isRoomMuted)
        case mute : Mute => handleMuteMessage(mute)
        case lock : Lock => handleLockMessage(lock)
        case eject : Eject => handleEjectMessage(eject)
        case sendAnnotation : SendAnnotation => handleSendAnnotationMessage(sendAnnotation)
        case changePage : ChangePage => handleChangePageMessage(changePage)
        case sendAnnotationHistory : SendAnnotationHistory => handleSendAnnotationHistoryMessage(sendAnnotationHistory)
        case clearAnnotations : ClearAnnotations => handleClearAnnotationsMessage(clearAnnotations)
        case undoAnnotation : UndoAnnotation => handleUndoAnnotationMessage(undoAnnotation)
        case toggleGrid : ToggleGrid => handleToggleGridMessage(toggleGrid)
        case setActivePresentation : SetActivePresentation => handleSetActivePresentationMessage(setActivePresentation)
        case enableWhiteboard : EnableWhiteboard => handleEnableWhiteboardMessage(enableWhiteboard)
        case isWhiteboardEnabled : IsWhiteboardEnabled => handleIsWhiteboardEnabledMessage(isWhiteboardEnabled)
      }
	}
  }
  
  private def handleCreateMeetingMessage(msg : CreateMeeting) : Unit = {
	  voiceGW.getVoiceUsers(meetingVO.voiceBridge);
  }
  
  private def handleEndMeetingMessage(msg : EndMeeting) : Unit = {
	  if (meetingVO.recorded) {
	    recorderGW.stopRecording(meetingVO.meetingID);
	  }
  }
	
  private def handleJoinUserMessage(msg : JoinUser) : Unit = {
	  users.addUser(msg.userID, msg.username, msg.role, msg.externUserID)
	  //clientGW 
  }
	
  private def handleLeaveUserMessage(msg : LeaveUser) : Unit = {
	  users.removeUser(msg.userID);
  }
	
	private def handleAssignPresenterMessage(msg : AssignPresenter) : Unit = {
	  
	}
	
	private def handleSendUsersMessage(msg : SendUsers) : Unit = {
	  
	}
	
	private def handleSetUserStatusMessage(msg : SetUserStatus) : Unit = {
	  
	}
	
	private def handleSendPublicChatHistoryMessage(msg : SendPublicChatHistory) : Unit = {
	  
	}
	
	private def handleSendPublicMessageMessage(msg : SendPublicMessage) : Unit = {
	  
	}
	
	private def handleSendPrivateMessageMessage(msg : SendPrivateMessage) : Unit = {
	  
	}
	
	private def handleSendCurrentLayoutMessage(msg : SendCurrentLayout) : Unit = {
	  
	}
	
	private def handleUnlockLayoutMessage(msg : UnlockLayout) : Unit = {
	  
	}
	
	private def handleLockLayoutMessage(msg : LockLayout) : Unit = {
	  
	}
	
	private def handleRemovePresentationMessage(msg : RemovePresentation) : Unit = {
	  
	}
	
	private def handleSendPresentationInfoMessage(msg : SendPresentationInfo) : Unit = {
	  
	}
	
	private def handleGotoSlideMessage(msg : GotoSlide) : Unit = {
	  
	}
	
	private def handleSharePresentationMessage(msg : SharePresentation) : Unit = {
	  
	}
	
	private def handleSendCursorUpdateMessage(msg : SendCursorUpdate) : Unit = {
	  
	}
	
	private def handleResizeAndMoveSlideMessage(msg : ResizeAndMoveSlide) : Unit = {
	  
	}
	
	private def handleGetCurrentPresenterMessage(msg : GetCurrentPresenter) : Unit = {
	  
	}
	
	private def handleSendVoiceUsersMessage(msg : SendVoiceUsers) : Unit = {
	  
	}
	
	private def handleMuteAllMessage(msg : MuteAll) : Unit = {
	  
	}
	
	private def handleIsRoomMutedMessage(msg : IsRoomMuted) : Unit = {
	  
	}
	
	private def handleMuteMessage(msg : Mute) : Unit = {
	  
	}
	
	private def handleLockMessage(msg : Lock) : Unit = {
	  
	}
	
	private def handleEjectMessage(msg : Eject) : Unit = {
	  
	}
	
	private def handleSendAnnotationMessage(msg : SendAnnotation) : Unit = {
	  
	}
	
	private def handleChangePageMessage(msg : ChangePage) : Unit = {
	  
	}
	
	private def handleSendAnnotationHistoryMessage(msg : SendAnnotationHistory) : Unit = {
	  
	}
	
	private def handleClearAnnotationsMessage(msg : ClearAnnotations) : Unit = {
	  
	}
	
	private def handleUndoAnnotationMessage(msg : UndoAnnotation) : Unit = {
	  
	}
	
	private def handleToggleGridMessage(msg : ToggleGrid) : Unit = {
	  
	}
	
	private def handleSetActivePresentationMessage(msg : SetActivePresentation) : Unit = {
	  
	}
	
	private def handleEnableWhiteboardMessage(msg : EnableWhiteboard) : Unit = {
	  
	}
	
	private def handleIsWhiteboardEnabledMessage(msg : IsWhiteboardEnabled) : Unit = {
	  
	}

}