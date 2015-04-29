package grails.plugin.wschat.cam

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import groovy.json.JsonBuilder

import javax.websocket.Session


class WsCamService extends WsChatConfService {

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
				destroyCamUser(username)
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
			camNames.each { String chuser, Session crec ->
				if (crec && crec.isOpen()) {
					if (chuser && chuser.startsWith(user)) {
						def myMsg1 = [:]
						myMsg1.put("system","disconnect")
						wsChatMessagingService.messageUser(crec,myMsg1)
						destroyCamUser(chuser)
					}
				}
			}
		}
		destroyCamUser(user)
	}
	
	void addUser(String viewer, Session userSession){
		cleanUpSession(userSession)
		camUsers.putIfAbsent(viewer, userSession)
	}
	
	void cleanUpSession(useSession) {
		camNames.each { String chuser, Session crec ->
			if (!crec || !crec.isOpen()) {
				destroyFileUser(chuser)
			}
		}
	}

}
