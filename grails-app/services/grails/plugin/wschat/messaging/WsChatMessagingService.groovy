package grails.plugin.wschat.messaging

import grails.converters.JSON
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.listeners.ChatSessions

import javax.websocket.Session


class WsChatMessagingService extends WsChatConfService  implements ChatSessions {
	def wsChatUserService

	def messageUser(Session userSession,Map msg) {
		def myMsgj=msg as JSON
		userSession.basicRemote.sendText(myMsgj as String)
	}

	def privateMessage(String user,Map msg,Session userSession) {
		def myMsg=[:]
		def myMsgj=msg as JSON
		String urecord=userSession.userProperties.get("username") as String
		Boolean found=false
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							Boolean sendIt=checkPM(urecord,user)
							Boolean sendIt2=checkPM(user,urecord)
							found=true
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
		if (found==false) {
			myMsg.put("message","Error: ${user} not found - unable to send PM")
			messageUser(userSession,myMsg)
		}
	}


	Boolean checkPM(String username, String urecord) {
		Boolean result=true
		if (dbSupport()) {
			ChatBlockList.withTransaction {
				def found=ChatBlockList.findByChatuserAndUsername(wsChatUserService.currentUser(username),urecord)
				if (found) {
					result=false
				}
			}
		}
		return result
	}


	def broadcast2all(Map msg) {
		def myMsgj=msg as JSON
		try {
			synchronized (chatroomUsers) {
				Iterator<Session> iterator=chatroomUsers?.iterator()
				if (iterator) {
					while (iterator?.hasNext()) {
						def crec=iterator?.next()
						if (crec.isOpen())  {
							crec.basicRemote.sendText(myMsgj as String);
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	def broadcast(Session userSession,Map msg) {
		def myMsgj=msg as JSON
		String room = userSession.userProperties.get("room") as String
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
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
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("camuser").toString()
						def cmuser=crec.userProperties.get("camusername").toString()
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
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("camuser").toString()
						def cmuser=crec.userProperties.get("camusername").toString()
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


}
