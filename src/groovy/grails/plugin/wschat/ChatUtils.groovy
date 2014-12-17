package grails.plugin.wschat

import grails.converters.JSON
import groovy.json.JsonBuilder
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import javax.websocket.Session


class ChatUtils {
	//private static List camusers = Collections.synchronizedList(new ArrayList())
	//private static List camusers = Collections.synchronizedList(new ArrayList())
	//static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>())
	//static final Set<Session> camsessions = Collections.synchronizedSet(new HashSet<Session>())

	static final Set<Session> camsessions = ([] as Set).asSynchronized()
	static final Set<Session> chatroomUsers = ([] as Set).asSynchronized()

	//Map<String,String> chatroomUsers = new ConcurrentHashMap<String,String>()
	//List<String> chatroomUsers = new CopyOnWriteArrayList<String>()


	//GrailsApplication grailsApplication
	def config

	private String validateLogin(String username) {
		def defaultPerm='user'
		if (dbSupport()) {

			String defaultPermission=config.defaultperm  ?: defaultPerm
			def perm,user
			ChatPermissions.withTransaction {
				perm=ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
			}
			ChatUser.withTransaction {
				user=ChatUser.findOrSaveWhere(username:username, permissions:perm).save(flush:true)
			}
			ChatLogs.withTransaction {
				def logit=new ChatLogs()
				logit.username=username
				logit.loggedIn=true
				logit.loggedOut=false
				logit.save(flush:true)
			}
			return user.permissions.name as String
		}else{
			return defaultPerm
		}
	}

	private void validateLogOut(String username) {
		if (dbSupport()) {
			ChatLogs.withTransaction {
				def logit=new ChatLogs()
				logit.username=username
				logit.loggedIn=false
				logit.loggedOut=true
				logit.save(flush:true)
			}
		}
	}

	private void addRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			ChatRoomList.withTransaction {
				def nr=new ChatRoomList()
				nr.room=roomName
				if (!nr.save(flush:true)) {
					log.error "Error saving ${roomName}"
				}
			}
			ListRooms()
		}
	}

	private void delRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			try {
				Iterator<Session> iterator=chatroomUsers?.iterator()
				if (iterator) {
					while (iterator?.hasNext())  {
						def crec=iterator?.next()
						if (crec.isOpen() && roomName.equals(crec.userProperties.get("room"))) {
							def cuser=crec.userProperties.get("username").toString()
							//String croom = crec.userProperties.get("room") as String
							kickUser(userSession,cuser)
						}
					}
					ChatRoomList.withTransaction {
						def nr=ChatRoomList.findByRoom(roomName)
						if (nr) {
							nr.delete(flush: true)
						}
					}
					ListRooms()
				}
			} catch (IOException e) {
				log.error ("onMessage failed", e)
			}
		}
	}

	private void verifyAction(Session userSession,String message) {
		def myMsg=[:]
		String username=userSession.userProperties.get("username") as String
		String room = userSession.userProperties.get("room") as String
		String connector="CONN:-"
		Boolean isuBanned=false
		if (!username)  {
			if (message.startsWith(connector)) {
				username=message.substring(message.indexOf(connector)+connector.length(),message.length()).trim().replace(' ', '_').replace('.', '_')
				if (loggedIn(username)==false) {
					userSession.userProperties.put("username", username)
					isuBanned=isBanned(username)
					if (!isuBanned){
						if (dbSupport()) {
							def userLevel=validateLogin(username)
							userSession.userProperties.put("userLevel", userLevel)
							Boolean useris=isAdmin(userSession)
							def myMsg1=[:]
							myMsg1.put("isAdmin", useris.toString())
							messageUser(userSession,myMsg1)
						}
						def myMsg2=[:]
						myMsg2.put("currentRoom", "${room}")
						messageUser(userSession,myMsg2)
						sendUsers(userSession,username)
						String sendjoin=config.send.joinroom  ?: 'yes'
						if (sendjoin=='yes') {
						myMsg.put("message", "${username} has joined ${room}")
						sendRooms(userSession)
						}
					}else{
						def myMsg1=[:]
						myMsg1.put("isBanned", "user ${username} is banned being disconnected")
						messageUser(userSession,myMsg1)
						//chatroomUsers.remove(userSession)
					}
				}
			}
			if ((myMsg)&&(!isuBanned)) {
				broadcast(userSession,myMsg)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				String croom = userSession.userProperties.get("room") as String
				String cusername=userSession.userProperties.get("username") as String
				
				String dbsup=config.logleavers ?: 'no'
				
				if (dbsup=='yes') {
					log.error "Room: >${croom}< | User: >${username}< | Cuser: >${cusername}< "
				}
				
				
				userSession.close()
				removeUser(username)
				String bcasto=config.left.timeout  ?: '0'
				sleep(bcasto as int)
				sendUsersLoggedOut(croom,username)
			}else if (message.startsWith("/pm")) {
				def values=parseInput("/pm ",message)
				def user=values.user
				def msg=values.msg
				if (!user.equals(username)) {
					myMsg.put("msgFrom", username)
					myMsg.put("msgTo", user)
					myMsg.put("privateMessage", "${msg}")
					privateMessage(user,myMsg,userSession)
				}else{
					myMsg.put("message","Private message self?")
					messageUser(userSession,myMsg)
				}
			}else if (message.startsWith("/block")) {
				def values=parseInput("/block ",message)
				def user=values.user
				def person=values.msg
				blockUser(user,person)
				sendUsers(userSession,user)
			}else if (message.startsWith("/kickuser")) {
				def p1="/kickuser "
				def user=message.substring(p1.length(),message.length())
				kickUser(userSession,user)
			}else if (message.startsWith("/banuser")) {
				def values=parseBan("/banuser ",message)
				def user=values.user
				def duration=values.msg
				def period=values.msg2
				banUser(userSession,user,duration,period)
			}else if (message.startsWith("/unblock")) {
				def values=parseInput("/unblock ",message)
				def user=values.user
				def person=values.msg
				unblockUser(user,person)
				sendUsers(userSession,user)
			}else if (message.startsWith("/add")) {
				def values=parseInput("/add ",message)
				def user=values.user
				def person=values.msg
				addUser(user,person)
				sendUsers(userSession,user)
			}else if (message.startsWith("/removefriend")) {
				def values=parseInput("/removefriend ",message)
				def user=values.user
				def person=values.msg
				removeUser(user,person)
				sendUsers(userSession,user)
			}else if (message.startsWith("/joinRoom")) {
				def values=parseInput("/joinRoom ",message)
				def user=values.user
				def rroom=values.msg
				if (roomList().toMapString().contains(rroom)) {
					userSession.userProperties.put("room", rroom)
					room=rroom;
					myMsg.put("currentRoom", "${room}")
					messageUser(userSession,myMsg)
					
					String sendjoin=config.send.joinroom  ?: 'yes'
					if (sendjoin=='yes') {
					myMsg=[:]
					sendUsers(userSession,user)
					myMsg.put("message", "${user} has joined ${room}")
					broadcast(userSession,myMsg)
					//broadcast(userSession,["message", "${user} has joined ${room}"])
					sendRooms(userSession)
					}
				}
			}else if (message.startsWith("/listRooms")) {
				ListRooms()
			}else if (message.startsWith("/addRoom")) {
				def p1="/addRoom "
				def nroom=message.substring(p1.length(),message.length())
				addRoom(userSession,nroom)
			}else if (message.startsWith("/delRoom")) {
				def p1="/delRoom "
				def nroom=message.substring(p1.length(),message.length())
				delRoom(userSession,nroom)
			}else if (message.startsWith("/camenabled")) {
				def p1="/camenabled "
				def camuser=message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("av", "on")
				myMsg.put("message", "${camuser} has enabled webcam")
				broadcast(userSession,myMsg)
				sendUsers(userSession,camuser)
			}else if (message.startsWith("/camdisabled")) {
				def p1="/camdisabled "
				def camuser=message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("av", "off")
				myMsg.put("message", "${camuser} has disabled webcam")
				broadcast(userSession,myMsg)
				sendUsers(userSession,camuser)
				// Usual chat messages bound for all
			}else if (message.startsWith("/webrtcenabled")) {
				def p1="/webrtcenabled "
				def camuser=message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("rtc", "on")
				myMsg.put("message", "${camuser} has enabled WebrRTC")
				broadcast(userSession,myMsg)
				sendUsers(userSession,camuser)
			}else if (message.startsWith("/webrtcdisabled")) {
				def p1="/webrtcdisabled "
				def camuser=message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("rtc", "off")
				myMsg.put("message", "${camuser} has disabled WebrRTC")
				broadcast(userSession,myMsg)
				sendUsers(userSession,camuser)
				// Usual chat messages bound for all
			}else{
				myMsg.put("message", "${username}: ${message}")
				broadcast(userSession,myMsg)
				//messageUser(userSession,myMsg)
			}
		}
	}

	/*}else if (message.startsWith("/streamCam")) {
	 JSONObject json = new JSONObject(message);
	 JSONObject data = json.getJSONObject("data");
	 String to = data.getString("sendto");
	 String base64 = data.getString("url");
	 if (base64  != null) {
	 // Send the buffer to all subscibers
	 //for (Session subscriber : sessions) {
	 //	subscriber.basicRemote.sendText(base64);
	 //}
	 //sendCam(to,base64)
	 //}
	 */

	private void verifyCamAction(Session userSession,String message) {
		def myMsg=[:]
		String username=userSession.userProperties.get("camusername") as String
		String camuser=userSession.userProperties.get("camuser") as String
		def payload
		def cmessage
		def croom
		String realCamUser=realCamUser(camuser)
		Boolean isuBanned=false
		if (username)  {
			def data=JSON.parse(message)
			// authentication stuff - system calls
			if (data) {
				cmessage=data.type
				croom=data.roomId
				payload=data.payload
			}else{
				cmessage=message
			}

			if (cmessage.startsWith("DISCO:-")) {
				camsessions.remove(userSession)
			}else if (cmessage.startsWith("createRoom")) {
				def json = new JsonBuilder()
				json {
					delegate.type "roomCreated"
					delegate.payload "${username}"
				}
				jsonmessageUser(userSession,json.toString())
			} else if (cmessage.startsWith("offer")) {
				// Offer is coming from client so direct it to owner
				jsonmessageOwner(userSession,message,realCamUser)
			}else{
				// Message all others related to a msg coming from owner
				if (camuser.equals(realCamUser+":"+realCamUser)) {
					jsonmessageOther(userSession,message,realCamUser)
				}else{
					jsonmessageOwner(userSession,message,realCamUser)
				}
			}
		}
	}

	private void discoCam(Session userSession) {
		String user = userSession.userProperties.get("camusername") as String
		String camuser = userSession.userProperties.get("camuser") as String
		if (user && camuser && camuser.endsWith(':'+user)) {
			try {
				Iterator<Session> iterator=camsessions?.iterator()
				if (iterator) {
					while (iterator?.hasNext())  {
						def crec=iterator?.next()
						if (crec?.isOpen()) {
							String chuser=crec?.userProperties.get("camuser") as String
							if (chuser && chuser.startsWith(user)) {
								def myMsg1=[:]
								myMsg1.put("system","disconnect")
								messageUser(crec,myMsg1)
								camsessions.remove(crec)
							}
						}
					}
				}
			} catch (Throwable e) {
				log.error ("onMessage failed", e)
			}
		}
		try {
			camsessions.remove(userSession)
		} catch (Throwable e) {	}
	}

	private Boolean loggedIn(String user) {
		Boolean loggedin=false
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							loggedin=true
						}
					}
				}
			}
		} catch (IOException e) {
			log.info ("onMessage failed", e)
		}
		return loggedin
	}

	private Boolean camLoggedIn(String user) {
		Boolean loggedin=false
		try {
			Iterator<Session> iterator=camsessions?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("camusername").toString()
						if (cuser.equals(user)) {
							loggedin=true
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return loggedin
	}

	private void sendUserList(String iuser,Map msg) {
		def myMsgj=msg as JSON
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(iuser)) {
							crec.basicRemote.sendText(myMsgj as String)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	/*
	 private void removeCamUser(Session userSession,String username) {
	 String ruser = userSession.userProperties.get("username") as String
	 if (camusers.contains(username)&&(ruser.equals(username))){
	 camusers.remove(username)
	 }
	 }
	 private void addCamUser(Session userSession,String username) {
	 String ruser = userSession.userProperties.get("username") as String
	 if (!camusers.contains(username)&&(ruser.equals(username))){
	 camusers.add(username)
	 }
	 }
	 */

	private void removeUser(String username) {
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							iterator.remove()
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void sendUsersLoggedOut(String room,String username) {

		try {
			Iterator<Session> iterator2=chatroomUsers?.iterator()
			if (iterator2) {
				def myMsg=[:]
				String sendleave=config.send.leftroom  ?: 'yes'
				myMsg.put("message", "${username} has left ${room}")
				while (iterator2?.hasNext())  {
					def crec2=iterator2?.next()
					if (crec2.isOpen()) {
						def uiterator=crec2.userProperties.get("username").toString()
						if (uiterator!=username) {
							//broadcast(crec2,myMsg)
							//isuBanned=isBanned(username)
							//if (!isuBanned && (sendleave=='yes')){
							if (sendleave=='yes') {
								broadcast(crec2,myMsg)
							}
							
							
							def uList=[]
							def finalList=[:]
							def blocklist
							def friendslist
							if (dbSupport()) {
								ChatBlockList.withTransaction {
									blocklist=ChatBlockList.findAllByChatuser(currentUser(uiterator))
								}
								ChatFriendList.withTransaction {
									friendslist=ChatFriendList.findAllByChatuser(currentUser(uiterator))
								}
							}
							Iterator<Session> iterator=chatroomUsers?.iterator()
							if (iterator) {
								while (iterator?.hasNext())  {
									def myUser=[:]
									def crec=iterator?.next()
									if (crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
										def cuser=crec.userProperties.get("username").toString()
										def av=crec.userProperties.get("av").toString()
										def rtc=crec.userProperties.get("rtc").toString()
										def addav=""
										if (av.equals("on")) {
											addav="_av"
										}
										if (rtc.equals("on")) {
											addav="_rtc"
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
									}
								}
							}
							finalList.put("users", uList)
							sendUserList(uiterator,finalList)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}

	}

	private void sendUsers(Session userSession,String username) {
		String room = userSession.userProperties.get("room") as String
		try {
			Iterator<Session> iterator2=chatroomUsers?.iterator()
			if (iterator2) {
				while (iterator2?.hasNext())  {
					def crec2=iterator2?.next()
					if (crec2.isOpen()) {
						def uiterator=crec2.userProperties.get("username").toString()
						def uList=[]
						def finalList=[:]
						def blocklist
						def friendslist
						if (dbSupport()) {
							ChatBlockList.withTransaction {
								blocklist=ChatBlockList.findAllByChatuser(currentUser(uiterator))
							}
							ChatFriendList.withTransaction {
								friendslist=ChatFriendList.findAllByChatuser(currentUser(uiterator))
							}
						}
						Iterator<Session> iterator=chatroomUsers?.iterator()
						if (iterator) {
							while (iterator?.hasNext())  {
								def myUser=[:]
								def crec=iterator?.next()
								if (crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
									def cuser=crec.userProperties.get("username").toString()
									def av=crec.userProperties.get("av").toString()
									def rtc=crec.userProperties.get("rtc").toString()
									def addav=""
									if (av.equals("on")) {
										addav="_av"
									}
									if (rtc.equals("on")) {
										addav="_rtc"
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
								}
							}
						}
						finalList.put("users", uList)
						sendUserList(uiterator,finalList)
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}

	}

	private void broadcast2all(Map msg) {
		def myMsgj=msg as JSON
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext()) {
					def crec=iterator?.next()
					if (crec.isOpen())  {
						crec.basicRemote.sendText(myMsgj as String);
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void broadcast(Session userSession,Map msg) {
		def myMsgj=msg as JSON
		String room = userSession.userProperties.get("room") as String
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext()) {
					def crec=iterator?.next()
					if (crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
						crec.basicRemote.sendText(myMsgj as String);
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void jsonmessageUser(Session userSession,String msg) {
		userSession.basicRemote.sendText(msg as String)
	}

	private void jsonmessageOther(Session userSession,String msg,String realCamUser) {
		try {
			Iterator<Session> iterator=camsessions?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
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

	private void jsonmessageOwner(Session userSession,String msg,String realCamUser) {
		try {
			Iterator<Session> iterator=camsessions?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
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

	private String realCamUser(String camuser) {
		String realCamUser
		if (camuser) {
			if (camuser.indexOf(':')>-1) {
				realCamUser=camuser.substring(0,camuser.indexOf(':'))
			}
		}
		return realCamUser
	}
	private void messageUser(Session userSession,Map msg) {
		def myMsgj=msg as JSON
		userSession.basicRemote.sendText(myMsgj as String)
	}

	private void privateMessage(String user,Map msg,Session userSession) {
		def myMsg=[:]
		def myMsgj=msg as JSON
		String urecord=userSession.userProperties.get("username") as String
		Boolean found=false
		try {
			Iterator<Session> iterator=chatroomUsers?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
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

	private Boolean dbSupport() {

		Boolean dbsupport=false
		String dbsup=config.dbsupport  ?: 'yes'
		if ((dbsup.toLowerCase().equals('yes'))||(dbsup.toLowerCase().equals('true'))) {
			dbsupport=true
		}
		return dbsupport
	}

	private Boolean checkPM(String username, String urecord) {
		Boolean result=true
		if (dbSupport()) {
			ChatBlockList.withTransaction {
				def found=ChatBlockList.findByChatuserAndUsername(currentUser(username),urecord)
				if (found) {
					result=false
				}
			}
		}
		return result
	}

	private Boolean isAdmin(Session userSession) {
		Boolean useris=false
		String userLevel=userSession.userProperties.get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris=true
		}
		return useris
	}

	private void kickUser(Session userSession,String username) {
		Boolean useris=isAdmin(userSession)
		if (useris) {
			logoutUser(userSession,username)
		}
	}

	private void banUser(Session userSession,String username,String duration,String period) {
		Boolean useris=isAdmin(userSession)
		if (useris) {
			banthisUser(username,duration,period)
			logoutUser(userSession,username)
		}
	}

	private void logoutUser(Session userSession,String username) {
		def myMsg=[:]
		myMsg.put("message", "${username} about to be kicked off")
		broadcast(userSession,myMsg)
		try {
			Iterator<Session> iterator=chatroomUsers.iterator()
			if (iterator) {
				while (iterator.hasNext())  {
					def crec=iterator?.next()
					if (crec) {
						def uList=[]
						def finalList=[:]
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(username)) {
							def myMsg1=[:]
							myMsg1.put("system","disconnect")
							messageUser(crec,myMsg1)
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
	}

	private void unblockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser=currentUser(username)
			ChatBlockList.withTransaction {
				def found=ChatBlockList.findByChatuserAndUsername(cuser,urecord)
				found.delete(flush: true)
			}
		}
	}


	private Boolean isBanned(String username) {
		Boolean yesis=false
		if (dbSupport()) {
			def now=new Date()
			def current = new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss').format(now)
			ChatBanList.withTransaction {
				def found=ChatBanList.findAllByUsernameAndPeriodGreaterThan(username,current)
				def dd=ChatBanList.findAllByUsername(username)
				if (found) {
					yesis=true
				}
			}
		}
		return yesis
	}

	private void banthisUser(String username,String duration, String period) {
		def cc
		use(TimeCategory) {
			cc=new Date() +(duration as int)."${period}"
		}
		def current = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cc)
		ChatBanList.withTransaction {
			def found=ChatBanList.findByUsername(username)
			if (!found) {
				def newEntry=new ChatBanList()
				newEntry.username=username
				newEntry.period=current
				newEntry.save(flush:true)
			}else{
				found.period=current
				found.save(flush:true)
			}
		}
	}

	private void blockUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser=currentUser(username)
			ChatBlockList.withTransaction {
				def found=ChatBlockList.findByChatuserAndUsername(cuser,urecord)
				if (!found) {
					def newEntry=new ChatBlockList()
					newEntry.chatuser=cuser
					newEntry.username=urecord
					newEntry.save(flush:true)
				}
			}
		}
	}

	private void addUser(String username,String urecord) {
		if (dbSupport()) {
			def cuser=currentUser(username)
			ChatFriendList.withTransaction {
				def found=ChatFriendList.findByChatuserAndUsername(cuser,urecord)
				if (!found) {
					def newEntry=new ChatFriendList()
					newEntry.chatuser=cuser
					newEntry.username=urecord
					newEntry.save(flush:true)
				}
			}
		}
	}

	private void removeUser(String username,String urecord) {
		if (dbSupport()) {
			ChatFriendList.withTransaction {
				def cuser=currentUser(username)
				def found=ChatFriendList.findByChatuserAndUsername(cuser,urecord)
				found.delete(flush: true)
			}
		}
	}

	def currentUser(String username) {
		if (dbSupport()) {
			ChatUser.withTransaction {
				return ChatUser.findByUsername(username)
			}
		}
	}

	private String getCurrentUserName(Session userSession) {
		def myMsg=[:]
		String username=userSession.userProperties.get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			messageUser(userSession,myMsg)
			//chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}

	private void sendRooms(Session userSession) {
		messageUser(userSession,roomList())
	}

	private void ListRooms() {
		broadcast2all(roomList())
	}

	private Map roomList() {
		def uList=[]
		def room=config.rooms
		if (room) {
			uList=[]
			room.each {
				def myMsg=[:]
				myMsg.put("room",it)
				uList.add(myMsg)
			}
		}
		def dbrooms
		def finalList=[:]
		if (dbSupport()) {
			ChatRoomList.withTransaction {
				dbrooms=ChatRoomList?.findAll()*.room.unique()
			}
			if (dbrooms) {
				//uList=[]
				dbrooms.each {
					def myMsg=[:]
					myMsg.put("room",it)
					uList.add(myMsg)
				}
			}
		}
		if (!room && !dbrooms) {
			room='wschat'
			def myMsg=[:]
			myMsg.put("room",room)
			uList.add(myMsg)
		}
		finalList.put("rooms", uList)
		return finalList as Map
	}


	private Map<String, String> parseInput(String mtype,String message){
		def p1=mtype
		def mu=message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset=[]
		if (mu.indexOf(",")>-1) {
			user=mu.substring(0,mu.indexOf(","))
			msg=mu.substring(user.length()+1,mu.length())
		}else{
			user=mu.substring(0,mu.indexOf(" "))
			msg=mu.substring(user.length()+1,mu.length())
		}
		Map<String, String> values = new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}


	private Map<String, String> parseBan(String mtype,String message){
		def p1=mtype
		def mu=message.substring(p1.length(),message.length())
		def msg2
		def resultset=[]
		def	user=mu.substring(0,mu.indexOf(","))
		def	msg=mu.substring(user.length()+1,mu.length())
		if (msg.indexOf(':')>-1) {
			msg2=msg.substring(msg.indexOf(':')+1,msg.length())
			msg=msg.substring(0,msg.indexOf(':'))
		}
		Map<String, String> values = new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		if (msg2){
			values.put("msg2", msg2);
		}
		return values
	}
}
