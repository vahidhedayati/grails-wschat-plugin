package grails.plugin.wschat.auth

import grails.plugin.wschat.ChatAuthLogs
import grails.plugin.wschat.ChatLog
import grails.plugin.wschat.ChatPermissions
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.OffLineMessage
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions
import grails.transaction.Transactional

import javax.websocket.Session

@Transactional
class WsChatAuthService extends WsChatConfService  implements ChatSessions  {

	def wsChatMessagingService
	def wsChatUserService
	def wsChatRoomService

	public void connectUser(String message,Session userSession,String room) {
		def myMsg = [:]
		Boolean isuBanned = false
		String connector = "CONN:-"
		def user
		def username = message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')

		if (loggedIn(username)==false) {


			userSession.userProperties.put("username", username)
			isuBanned = isBanned(username)
			if (!isuBanned){
				if (dbSupport()) {
					def userRec = validateLogin(username)
					def userLevel = userRec.permission
					user = userRec.user
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
					//wsChatMessagingService.messageUser(userSession,myMsg)
				}
				wsChatRoomService.sendRooms(userSession)


			}else{
				def myMsg1 = [:]
				myMsg1.put("isBanned", "user ${username} is banned being disconnected")
				wsChatMessagingService.messageUser(userSession,myMsg1)
				//chatroomUsers.remove(userSession)
			}
		}else{
			myMsg.put("message", "${username} is already loggged in elsewhere, action denied")
		}

		if ((myMsg)&&(!isuBanned)) {
			wsChatMessagingService.broadcast(userSession,myMsg)
		}
		if (dbSupport()) {
			verifyOffLine(userSession,username)
		}
	}

	private void verifyOffLine(Session userSession, String username) {
		OffLineMessage.withTransaction {
			def chat = ChatUser?.findByUsername(username)
			def pms=OffLineMessage?.findAllByOfflog(chat.offlog)
			if (pms) {
				pms.each { aa->
					wsChatMessagingService.sendMsg(userSession,aa?.contents)
				}

				pms*.delete()
			}
		}
	}

	public Map addUser(String username) {
		String defaultPermission = config.defaultperm  ?: defaultPerm
		def perm,user
		ChatPermissions.withTransaction {
			perm = ChatPermissions.findByName(defaultPermission)
			if (!perm) {
				perm = ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
			}
		}
		//ChatLog logInstance = addLog()
		ChatUser.withTransaction {
			user = ChatUser.findByUsername(username)
			if (!user) {
				def addlog = addLog()
				user = ChatUser.findOrSaveWhere(username:username, permissions:perm, log: addlog, offlog: addlog).save(flush:true)
			}
		}
		return [ user:user, perm:perm ]
	}

	private ChatLog addLog() {
		ChatLog.withTransaction {
			ChatLog logInstance = new ChatLog(messages: [])
			logInstance.save(flush: true)
			return logInstance
		}
	}

	Map validateLogin(String username) {
		def defaultPerm = 'user'
		def user
		if (dbSupport()) {
			def au=addUser(username)
			user=au.user
			def perm=au.perm

			ChatAuthLogs.withTransaction {
				def logit = new ChatAuthLogs()
				logit.username = username
				logit.loggedIn = true
				logit.loggedOut = false
				logit.save(flush:true)
			}
			//return user.permissions.name as String
			defaultPerm = user.permissions.name as String
		}

		[permission: defaultPerm, user: user]
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
			ChatAuthLogs.withTransaction {
				def logit = new ChatAuthLogs()
				logit.username = username
				logit.loggedIn = false
				logit.loggedOut = true
				logit.save(flush:true)
			}
		}
	}
}
