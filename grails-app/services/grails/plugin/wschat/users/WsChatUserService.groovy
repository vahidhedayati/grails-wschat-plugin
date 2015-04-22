package grails.plugin.wschat.users

import grails.converters.JSON
import grails.plugin.wschat.ChatBanList
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.ChatFriendList
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatUserProfile
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import javax.websocket.Session



class WsChatUserService extends WsChatConfService  {

	def wsChatMessagingService

	void kickUser(Session userSession,String username) {
		Boolean useris = isAdmin(userSession)
		if (useris) {
			logoutUser(userSession,username)
		}
	}

	public Map findaUser(String uid) {
		def returnResult=[:]
		def found=ChatUser.findByUsername(uid)
		if (found) {
			returnResult.put("status", "found")
			def foundProfile=ChatUserProfile?.findByChatuser(found)
			if (foundProfile) {
				if (foundProfile?.email)  {
					returnResult.put("email", foundProfile.email)
				}
			}

		}else{
			returnResult.put("status", "not_found")
		}
		return returnResult
	}

	public Map search(String mq) {
		def userList = ChatUser?.findAllByUsernameLike("%" + mq + "%", [max: 30])
		def uList = genAllUsers()
		if (!userList) {
			userList = ChatUserProfile.findAllByFirstNameLikeOrEmailLikeOrLastNameLike("%" + mq + "%", "%" + mq + "%", "%" + mq + "%", [max: 30])*.chatuser.unique()
		}
		return [userList:userList, uList:uList]
	}

	private void banUser(Session userSession,String username,String duration,String period) {
		Boolean useris = isAdmin(userSession)
		if (useris) {
			banthisUser(username,duration,period)
			logoutUser(userSession,username)
		}
	}

	private void logoutUser(String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def uList = []
				def finalList = [:]
				if (cuser.equals(username)) {
					def myMsg1 = [:]
					myMsg1.put("system","disconnect")
					wsChatMessagingService.messageUser(crec,myMsg1)
				}
			}
		}
	}


	private void logoutUser(Session userSession,String username) {
		def myMsg = [:]
		myMsg.put("message", "${username} about to be kicked off")
		wsChatMessagingService.broadcast(userSession,myMsg)
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def uList = []
				def finalList = [:]
				if (cuser.equals(username)) {
					def myMsg1 = [:]
					myMsg1.put("system","disconnect")
					wsChatMessagingService.messageUser(crec,myMsg1)
				}
			}
		}
	}


	Session usersSession(String username) {
		return getChatUser(username)
	}

	boolean findUser(String username) {
		return chatUserExists(username)
	}

	public ArrayList genAllUsers() {
		def uList = []
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				uList.add(cuser)
			}
		}
		return uList
	}

	def sendFlatUsers(Session userSession,String username) {
		userListGen(userSession, username, "flat")
	}

	def sendUsers(Session userSession,String username) {
		String uiterator = userSession.userProperties.get("username").toString()
		userListGen(userSession, username, "generic")
	}
	
	@Transactional
	private void userListGen(Session userSession,String username, String listType) {
		String room  =  userSession.userProperties.get("room") as String
		chatNames.each { String uiterator, Session crec2 ->
			if (crec2 && crec2.isOpen()) {
				def finalList = [:]
				def blocklist
				def friendslist
				if (dbSupport()) {
					blocklist = ChatBlockList.findAllByChatuser(currentUser(uiterator))
					friendslist = ChatFriendList.findAllByChatuser(currentUser(uiterator))
				}
				def	uList = genUserMenu(friendslist, blocklist, room, uiterator, listType)
				if (listType=="generic") {
					finalList.put("users", uList)
				}else{
					finalList.put("flatusers", uList)
				}
				sendUserList(uiterator,finalList)
			}
		}
	}


	private ArrayList genUserMenu(ArrayList friendslist, ArrayList blocklist, String room, String uiterator, String listType ) {
		def uList = []
		def vList = []
		chatNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def myUser = [:]
				vList.add(cuser)
				if (room.equals(crec.userProperties.get("room"))) {
					//def cuser = crec.userProperties.get("username").toString()
					def av = crec.userProperties.get("av").toString()
					def rtc = crec.userProperties.get("rtc").toString()
					def addav = ""
					if (listType=="generic") {
						if (av.equals("on")) {
							addav = "_av"
						}
						if (rtc.equals("on")) {
							addav = "_rtc"
						}
						if (cuser.equals(uiterator)) {
							myUser.put("owner${addav}", cuser)
							uList.add(myUser)
						}else{
							if ((blocklist)&&(blocklist.username.contains(cuser))) {
								myUser.put("blocked", cuser)
								uList.add(myUser)
							}else if  ((friendslist)&&(friendslist.username.contains(cuser))) {
								myUser.put("friends${addav}", cuser)
								uList.add(myUser)
							}else{
								myUser.put("user${addav}", cuser)
								uList.add(myUser)
							}
						}
					}else{
						myUser.put("users", cuser)
						uList.add(myUser)
					}
				}
			}
		}
		if (friendslist) {
			String method='offline_friends'
			friendslist.each { ChatFriendList fl->
				def myUser1 = [:]
				if (vList.contains(fl.username)) {
					method="online_friends"
				}
				myUser1.put(method, fl.username)
				uList.add(myUser1)
			}
		}

		return uList
	}


	Boolean validateAdmin(String username) {
		boolean useris = false
		def found=ChatUser.findByUsername(username)
		if (found) {
			if (found.permissions.name.toString().toLowerCase().startsWith('admin')) {
				useris = true
			}
		}
		return useris
	}


	private void sendUserList(String iuser,Map msg) {
		String sendUserList = config.send.userList  ?: 'yes'
		if ( sendUserList == 'yes') {
			def myMsgj = msg as JSON
			Session crec = getChatUser(iuser)
			if (crec && crec.isOpen()) {
				crec.basicRemote.sendText(myMsgj as String)
			}
		}
	}

	private void removeUser(String username) {
		destroyChatUser(username)
	}

	@Transactional
	private void unblockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			def found = ChatBlockList.findByChatuserAndUsername(cuser,urecord)
			found.delete(flush: true)
		}
	}

	@Transactional
	private void banthisUser(String username,String duration, String period) {
		def cc
		use(TimeCategory) {
			cc = new Date() +(duration as int)."${period}"
		}
		def current  =  new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cc)
		def found = ChatBanList.findByUsername(username)
		if (!found) {
			def newEntry = new ChatBanList()
			newEntry.username = username
			newEntry.period = current
			if (!newEntry.save(flush:true)) {
				if (config.debug == "on") {
					newEntry.errors.allErrors.each{println it}
				}
			}
		}else{
			found.period = current
			if (!found.save(flush:true)) {
				if (config.debug == "on") {
					found.errors.allErrors.each{println it}
				}
			}
		}
	}

	@Transactional
	private void blockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			def found = ChatBlockList.findByChatuserAndUsername(cuser,urecord)
			if (!found) {
				def newEntry = new ChatBlockList()
				newEntry.chatuser = cuser
				newEntry.username = urecord
				if (!newEntry.save(flush:true)) {
					if (config.debug == "on") {
						newEntry.errors.allErrors.each{println it}
					}
				}
			}
		}
	}

	@Transactional
	private void addUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			def found = ChatFriendList.findByChatuserAndUsername(cuser, urecord)

			if (!found) {
				def newEntry = new ChatFriendList()
				newEntry.chatuser = cuser
				newEntry.username = urecord
				if (!newEntry.save(flush:true)) {
					if (config.debug == "on") {
						newEntry.errors.allErrors.each{println it}
					}
				}
			}
		}
	}

	@Transactional
	private void removeUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser = currentUser(username)
			def found = ChatFriendList.findByChatuserAndUsername(cuser,urecord)
			found.delete(flush: true)
		}
	}



	private String getCurrentUserName(Session userSession) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			wsChatMessagingService.messageUser(userSession,myMsg)
			//chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}

}
