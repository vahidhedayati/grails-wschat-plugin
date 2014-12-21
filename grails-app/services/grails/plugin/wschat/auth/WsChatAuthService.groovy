package grails.plugin.wschat.auth

import javax.websocket.Session;

import grails.plugin.wschat.ChatLogs
import grails.plugin.wschat.ChatPermissions
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions
import grails.transaction.Transactional

//@Transactional
class WsChatAuthService extends WsChatConfService  implements ChatSessions  {
	
	def wsChatMessagingService
	def wsChatUserService
	def wsChatRoomService
	
	public void connectUser(String message,Session userSession,String room) {
		def myMsg = [:]
		Boolean isuBanned = false
		String connector = "CONN:-"
		def username = message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')
		if (loggedIn(username) == false) {
			userSession.userProperties.put("username", username)
			isuBanned = isBanned(username)
			if (!isuBanned){
				if (dbSupport()) {
					def userLevel = validateLogin(username)
					userSession.userProperties.put("userLevel", userLevel)
					Boolean useris = isAdmin(userSession)
					def myMsg1 = [:]
					myMsg1.put("isAdmin", useris.toString())
					wsChatMessagingService.messageUser(userSession,myMsg1)
				}
				def myMsg2 = [:]
				myMsg2.put("currentRoom", "${room}")
				wsChatMessagingService.messageUser(userSession,myMsg2)
				wsChatUserService.sendUsers(userSession,username)
				String sendjoin = config.send.joinroom  ?: 'yes'
				if (sendjoin == 'yes') {
					myMsg.put("message", "${username} has joined ${room}")
					wsChatRoomService.sendRooms(userSession)
				}
			}else{
				def myMsg1 = [:]
				myMsg1.put("isBanned", "user ${username} is banned being disconnected")
				wsChatMessagingService.messageUser(userSession,myMsg1)
				//chatroomUsers.remove(userSession)
			}
		}
	}
	
	String validateLogin(String username) {
		def defaultPerm = 'user'
		if (dbSupport()) {

			String defaultPermission = config.defaultperm  ?: defaultPerm
			def perm,user
			ChatPermissions.withTransaction {
				perm = ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
			}
			ChatUser.withTransaction {
				user = ChatUser.findOrSaveWhere(username:username, permissions:perm).save(flush:true)
			}
			ChatLogs.withTransaction {
				def logit = new ChatLogs()
				logit.username = username
				logit.loggedIn = true
				logit.loggedOut = false
				logit.save(flush:true)
			}
			return user.permissions.name as String
		}else{
			return defaultPerm
		}
	}

	Boolean loggedIn(String user) {
		Boolean loggedin = false
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec && crec.isOpen()) {
						def cuser = crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							loggedin = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.info ("onMessage failed", e)
		}
		return loggedin
	}


	void validateLogOut(String username) {
		if (dbSupport()) {
			ChatLogs.withTransaction {
				def logit = new ChatLogs()
				logit.username = username
				logit.loggedIn = false
				logit.loggedOut = true
				logit.save(flush:true)
			}
		}
	}
}
