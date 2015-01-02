package grails.plugin.wschat.messaging

import grails.converters.JSON
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.ChatMessage
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions

import javax.websocket.Session


class WsChatMessagingService extends WsChatConfService  implements ChatSessions {

	def messageUser(Session userSession,Map msg) {
		def myMsgj = msg as JSON
		String urecord = userSession.userProperties.get("username") as String
		boolean isEnabled = boldef(config.dbstore_user_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,urecord)
		}
		try {
			userSession.basicRemote.sendText(myMsgj as String)
		} catch (IOException e) {
		}

	}

	def privateMessage(String user,Map msg,Session userSession) {
		def myMsg = [:]
		def myMsgj = msg as JSON
		String urecord = userSession.userProperties.get("username") as String
		Boolean found = false
		
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,user,urecord)
		}
		
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							Boolean sendIt = checkPM(urecord,user)
							Boolean sendIt2 = checkPM(user,urecord)
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
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		if (found == false) {
			myMsg.put("message","Error: ${user} not found - unable to send PM")
			messageUser(userSession,myMsg)
		}
	}


	Boolean checkPM(String username, String urecord) {
		Boolean result = true
		if (dbSupport()) {
			ChatBlockList.withTransaction {
				def found = ChatBlockList.findByChatuserAndUsername(currentUser(username),urecord)
				if (found) {
					result = false
				}
			}
		}
		return result
	}


	def broadcast2all(Map msg) {
		def myMsgj = msg as JSON
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						crec.basicRemote.sendText(myMsgj as String);
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
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
		
		
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
						crec.basicRemote.sendText(myMsgj as String);
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}
	
	def jsonmessageUser(Session userSession,String msg) {
		userSession.basicRemote.sendText(msg as String)
	}

	def jsonmessageOther(Session userSession,String msg,String realCamUser) {
		try {
			synchronized (camsessions) {
				camsessions?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("camuser").toString()
						def cmuser = crec.userProperties.get("camusername").toString()
						if ((cuser.startsWith(realCamUser+":"))&&(!cuser.toString().endsWith(realCamUser))) {
							crec.basicRemote.sendText(msg as String)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def jsonmessageOwner(Session userSession,String msg,String realCamUser) {
		try {
			synchronized (camsessions) {
				camsessions?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("camuser").toString()
						def cmuser = crec.userProperties.get("camusername").toString()
						if ((cuser.startsWith(realCamUser+":"))&&(cuser.toString().endsWith(realCamUser))) {
							crec.basicRemote.sendText(msg as String)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}


	private void persistMessage(String message, String user, String username=null) {
		boolean isEnabled = boldef(config.dbstore)
		if (isEnabled) {
			ChatMessage.withTransaction {
				def chat = ChatUser.findByUsername(user)
				new ChatMessage(user: username, contents: message, log: chat?.log).save(flush: true)
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
