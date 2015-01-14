package grails.plugin.wschat.cam

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions
import groovy.json.JsonBuilder

import javax.websocket.Session


class WsCamService extends WsChatConfService  implements ChatSessions {

	def wsChatMessagingService

	String realCamUser(String camuser) {
		String realCamUser
		if (camuser) {
			if (camuser.indexOf(':')>-1) {
				realCamUser = camuser.substring(0,camuser.indexOf(':'))
			}
		}
		return realCamUser
	}

	void verifyCamAction(Session userSession,String message) {
		def myMsg = [:]
		String username = userSession.userProperties.get("camusername") as String
		String camuser = userSession.userProperties.get("camuser") as String
		def payload
		def cmessage
		def croom
		String realCamUser = realCamUser(camuser)
		Boolean isuBanned = false
		if (username)  {
			def data = JSON.parse(message)
			// authentication stuff - system calls
			if (data) {
				cmessage = data.type
				croom = data.roomId
				payload = data.payload
			}else{
				cmessage = message
			}

			if (cmessage.startsWith("DISCO:-")) {
				camsessions.remove(userSession)
			}else if (cmessage.startsWith("createRoom")) {
				def json  =  new JsonBuilder()
				json {
					delegate.type "roomCreated"
					delegate.payload "${username}"
				}
				wsChatMessagingService.jsonmessageUser(userSession,json.toString())
			} else if (cmessage.startsWith("offer")) {
				// Offer is coming from client so direct it to owner
				wsChatMessagingService.jsonmessageOwner(userSession,message,realCamUser)
			}else{
				// Message all others related to a msg coming from owner
				if (camuser.equals(realCamUser+":"+realCamUser)) {
					wsChatMessagingService.jsonmessageOther(userSession,message,realCamUser)
				}else{
					wsChatMessagingService.jsonmessageOwner(userSession,message,realCamUser)
				}
			}
		}
	}

	void discoCam(Session userSession) {
		String user  =  userSession.userProperties.get("camusername") as String
		String camuser  =  userSession.userProperties.get("camuser") as String
		if (user && camuser && camuser.endsWith(':'+user) && (camuser != user+":"+user)) {
			try {
				synchronized (camsessions) {
					camsessions?.each { crec->
						if (crec && crec?.isOpen()) {
							String chuser = crec?.userProperties.get("camuser") as String
							if (chuser && chuser.startsWith(user)) {
								def myMsg1 = [:]
								myMsg1.put("system","disconnect")
								wsChatMessagingService.messageUser(crec,myMsg1)
								camsessions.remove(crec)
							}
						}
					}
				}
			} catch (Throwable e) {
				log.error ("discoCam failed", e)
			}
		}
		try {
			camsessions.remove(userSession)
		} catch (Throwable e) {	}
	}
}
