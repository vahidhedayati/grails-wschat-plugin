package grails.plugin.wschat.file

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import groovy.json.JsonBuilder

import javax.websocket.Session


class WsFileService extends WsChatConfService {

	static transactional  =  false

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

	void verifyFileAction(Session userSession,String message) {
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
				destroyCamUser(username)
			}else if (cmessage.startsWith("ENTERROOM")) {
				def json  =  new JsonBuilder()
				json {
					//delegate.type "roomCreated"
					delegate.type "offer"
					delegate.payload "${username}"
				}
				wsChatMessagingService.jsonmessageUser(userSession,json.toString())
			} else if (cmessage.startsWith("GETROOM")) {
				// Offer is coming from client so direct it to owner
			def json  =  new JsonBuilder()
			json {
				delegate.type "GETROOM"
				delegate.payload "${camuser}"
			}			
			wsChatMessagingService.jsonmessageUser(userSession,json.toString())
			}else{
				if (camuser.equals(realCamUser+":"+realCamUser)) {
					wsChatMessagingService.jsonmessageOther(userSession,message,realCamUser,true)
				}else{
					wsChatMessagingService.jsonmessageOwner(userSession,message,realCamUser,true)
				}
			}
		}
	}

	void discoCam(Session userSession) {
		String user  =  userSession.userProperties.get("camusername") as String
		String camuser  =  userSession.userProperties.get("camuser") as String
		if (user && camuser && camuser.endsWith(':'+user) && (camuser != user+":"+user)) {
			fileroomUsers.each { String chuser, Session crec ->
				if (crec && crec.isOpen()) {
					if (chuser && chuser.startsWith(user)) {
						def myMsg1 = [:]
						myMsg1.put("system","disconnect")
						wsChatMessagingService.messageUser(crec,myMsg1)
						destroyFileUser(chuser)
					}
				}
			}
		}
		destroyCamUser(user)
	}
}
