package grails.plugin.wschat.messaging

import grails.converters.JSON
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.ChatMessage
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.OffLineMessage
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional

import javax.websocket.Session


class WsChatMessagingService extends WsChatConfService {

	def sendMsg(Session userSession,String msg) throws Exception {
		try {
			if (userSession && userSession.isOpen()) {
				String urecord = userSession.userProperties.get("username") as String
				log.debug "sendMsg ${urecord}: ${msg}"
				boolean isEnabled = boldef(config.dbstore_user_messages)
				if (isEnabled) {
					persistMessage(msg ,urecord)
				}
				userSession.basicRemote.sendText(msg)
			}
		} catch (Exception e) {
			//e.printStackTrace()
		}
	}

	def messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}

	def privateMessage(String user,Map msg,Session userSession) {
		def myMsg = [:]
		def myMsgj = msg as JSON
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,user,urecord)
			persistMessage(myMsgj as String,urecord,urecord)
		}
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (cuser.equals(user)) {
					boolean sendIt = checkPM(urecord,user)
					boolean sendIt2 = checkPM(user,urecord)
					found = true
					if (sendIt&&sendIt2) {
						crec.basicRemote.sendText(myMsgj as String)
						myMsg.put("message","--> PM sent to ${user}")
						messageUser(userSession,myMsg)
					}else{
						myMsg.put("message","--> PM NOT sent to ${user}, you have been blocked !")
						messageUser(userSession,myMsg)
					}
				}
			}
		}
		if (found == false) {
			verifyOfflinePM(user, myMsgj as String, userSession, urecord)

		}
	}

	@Transactional
	Boolean checkPM(String username, String urecord) {
		boolean result = true
		if (hasDBSupport()) {
			def found = ChatBlockList.findByChatuserAndUsername(currentUser(username),urecord)
			if (found) {
				result = false
			}
		}
		return result
	}


	def broadcast2all(Map msg) {
		def myMsgj = msg as JSON
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				crec.basicRemote.sendText(myMsgj as String);
			}
		}
	}

	def broadcast(Session userSession,Map msg) {
		def myMsgj = msg as JSON
		String room = userSession.userProperties.get("room") as String
		String urecord = userSession.userProperties.get("username") as String
		boolean isEnabled = boldef(config.dbstore_room_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,urecord)
		}
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
				crec.basicRemote.sendText(myMsgj as String);
			}
		}
	}

	def jsonmessageUser(Session userSession,String msg) {
		userSession.basicRemote.sendText(msg as String)
	}

	def jsonmessageOther(Session userSession,String msg,String realCamUser, boolean fileUser=null) {
		def uList = camNames
		if (fileUser) {
			uList = fileroomUsers
		}
		uList.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def cmuser = crec.userProperties.get("camusername").toString()
				String camuser = crec.userProperties.get("camuser") as String
				if ((camuser.startsWith(realCamUser+":"))&&(!camuser.toString().endsWith(realCamUser))) {
					crec.basicRemote.sendText(msg as String)
				}
			}
		}
	}

	def jsonmessageOwner(Session userSession,String msg,String realCamUser, boolean fileUser=null) {
		def uList = camNames
		if (fileUser) {
			uList = fileroomUsers
		}
		uList.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def cmuser = crec.userProperties.get("camusername").toString()
				String camuser = crec.userProperties.get("camuser") as String
				if ((camuser.startsWith(realCamUser+":"))&&(camuser.toString().endsWith(realCamUser))) {
					crec.basicRemote.sendText(msg as String)
				}
			}
		}
	}

	@Transactional
	private void verifyOfflinePM(String user,String message,Session userSession,String username) {
		boolean isEnabled = boldef(config.offline_pm)
		if (isEnabled) {
			def chat = ChatUser.findByUsername(user)
			if (chat) {
				def cm = new OffLineMessage(user: username, contents: message, offlog: chat?.offlog, readMsg: false)
				if (!cm.save(flush:true)) {
					log.error "verifyOfflinePM issue:  ${cm.errors}"
				}
				messageUser(userSession,["message": "--> OFFLINE MSG sent to ${user}"])
			} else{
				messageUser(userSession,["message": "Error: ${user} not found - unable to send PM"])
			}
		}else{
			messageUser(userSession,["message": "Error: ${user} not found :- unable to send PM"])
		}
	}

	@Transactional
	private void persistMessage(String message, String user, String username=null) {
		boolean isEnabled = boldef(config.dbstore)
		if (isEnabled) {
			def chat = ChatUser.findByUsername(user)
			def cm = new ChatMessage(user: username, contents: message, log: chat?.log)
			if (!cm.save(flush:true)) {
				log.error "Persist Message issue: ${cm.errors}"
			}
		}
	}

	private Boolean boldef(def input) {
		boolean isEnabled = false
		if (input) {
			if (input instanceof String) {
				isEnabled = isConfigEnabled(input)
			}else{
				isEnabled = input
			}
		}
		return isEnabled
	}
}
