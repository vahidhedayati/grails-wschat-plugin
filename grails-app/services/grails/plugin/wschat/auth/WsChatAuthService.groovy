package grails.plugin.wschat.auth

import grails.plugin.wschat.ChatAuthLogs
import grails.plugin.wschat.ChatBanList
import grails.plugin.wschat.ChatLog
import grails.plugin.wschat.ChatPermissions
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.OffLineMessage
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional

import java.text.SimpleDateFormat

import javax.websocket.Session

@Transactional
class WsChatAuthService extends WsChatConfService   {

	def wsChatMessagingService
	def wsChatUserService
	def wsChatRoomService

	private void verifyOffLine(Session userSession, String username) {
		def chat = ChatUser?.findByUsername(username)
		def pms=OffLineMessage?.findAllByOfflog(chat.offlog)
		if (pms) {
			pms.each { aa->
				wsChatMessagingService.sendMsg(userSession,aa?.contents)
			}
			pms*.delete()
		}
	}

	@Transactional
	public Map addUser(String username) {
		String defaultPermission = config.defaultperm  ?: defaultPerm
		def perm,user
		perm = ChatPermissions.findByName(defaultPermission)
		if (!perm) {
			perm = ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
		}
		user = ChatUser.findByUsername(username)
		if (!user) {
			def addlog = addLog()
			user = ChatUser.findOrSaveWhere(username:username, permissions:perm, log: addlog, offlog: addlog).save(flush:true)
		}
		return [ user:user, perm:perm ]
	}

	@Transactional
	private ChatLog addLog() {
		ChatLog logInstance = new ChatLog(messages: [])
		if (!logInstance.save(flush:true)) {
			log.debug "${logInstance.errors}"
		}
		return logInstance
	}

	@Transactional
	Map validateLogin(String username) {
		def defaultPerm = 'user'
		def au=addUser(username)
		def user=au.user
		def perm=au.perm
		def logit = new ChatAuthLogs()
		logit.username = username
		logit.loggedIn = true
		logit.loggedOut = false
		if (!logit.save(flush:true)) {
			log.error "${logit.errors}"
		}
		defaultPerm = user.permissions.name as String
		[permission: defaultPerm, user: user]
	}

	@Transactional
	void validateLogOut(String username) {
		def logit = new ChatAuthLogs()
		logit.username = username
		logit.loggedIn = false
		logit.loggedOut = true
		if (!logit.save(flush:true)) {
			log.error "${logit.errors}"
		}
	}


	public void connectUser(String message,Session userSession,String room) {
		def myMsg = [:]
		Boolean isuBanned = false
		String connector = "CONN:-"
		def user
		def username = message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')

		userSession.userProperties.put("username", username)
		isuBanned = isBanned(username)
		if (isuBanned){
			wsChatMessagingService.messageUser(userSession,["isBanned":"user ${username} is banned being disconnected"])
			return
		}
		def userRec = validateLogin(username)
		def userLevel = userRec.permission
		user = userRec.user
		userSession.userProperties.put("userLevel", userLevel)
		String rooma = userSession?.userProperties?.get("room")
		if (loggedIn(username)==false) {
			chatroomUsers.putIfAbsent(username, ["${room}":userSession])
		} else {
			Map<String,Session> records= chatroomUsers.get(username)
			Session crec = records.find{it.key==room}?.value
			if (crec) {
				wsChatMessagingService.messageUser(userSession, ["message":"${username} is already loggged in to ${room}, action denied"])
				return
			} else {
				records << ["${room}":userSession]
			}
		}
		Boolean useris = isAdmin(userSession)
		wsChatMessagingService.messageUser(userSession, ["isAdmin":useris as String])
		def myMsg2 = [:]
		myMsg2.put("currentRoom", "${room}")
		wsChatMessagingService.messageUser(userSession,myMsg2)
		wsChatUserService.sendUsers(userSession,username,room)
		String sendjoin = config.send.joinroom  ?: 'yes'
		wsChatRoomService.sendRooms(userSession)
		if (sendjoin == 'yes') {
			wsChatMessagingService.broadcast(userSession,["message": "${username} has joined ${room}"])
		}
		verifyOffLine(userSession,username)
	}


	@Transactional
	Boolean isBanned(String username) {
		Boolean yesis = false
		def now = new Date()
		def current  =  new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss').format(now)
		def found = ChatBanList.findAllByUsernameAndPeriodGreaterThan(username,current)
		if (found) {
			yesis = true
		}
		return yesis
	}

	Boolean loggedIn(String user) {
		return chatUserExists(user)
	}
}
