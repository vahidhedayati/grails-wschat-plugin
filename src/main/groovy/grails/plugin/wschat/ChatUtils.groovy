package grails.plugin.wschat

import grails.plugin.wschat.auth.WsChatAuthService
import grails.plugin.wschat.cam.WsCamService
import grails.plugin.wschat.file.WsFileService
import grails.plugin.wschat.messaging.WsChatMessagingService
import grails.plugin.wschat.rooms.WsChatRoomService
import grails.plugin.wschat.users.WsChatUserService
import grails.util.Holders
import org.springframework.context.MessageSource
import org.springframework.web.servlet.i18n.SessionLocaleResolver

import javax.websocket.Session

class ChatUtils extends WsChatConfService {
	
	WsChatAuthService wsChatAuthService
	WsChatUserService wsChatUserService
	WsChatRoomService wsChatRoomService
	WsChatMessagingService wsChatMessagingService
	WsFileService wsFileService
	WsCamService wsCamService
	SessionLocaleResolver localeResolver
	MessageSource messageSource

	Boolean loggedIn(String user) {
		return chatUserExists(user)
	}

	private void privateMessage(Session userSession , String username, String user,String msg) {
		wsChatMessagingService.privateMessage(user,[msgFrom:username, msgTo:user,privateMessage:msg],userSession)
	}

	private void verifyUser(Session userSession, String userType, String room, String username) {
		userSession.userProperties.put("startTime", new Date())
		userSession.userProperties.put("userType", userType)
		if (userType==ChatUser.CHAT_LIVE_USER) {
			userSession.userProperties.put("livechat", "on")
			userSession.userProperties.put("nameRequired", true)
			userSession.userProperties.put("emailedRequired", true)
			boolean hasLiveAdmin = wsChatUserService.roomHasLiveAdmin(room)
			if (hasLiveAdmin) {
				wsChatUserService.sendUsers(userSession,username,room)
				wsChatMessagingService.adminEnableEndScreens(username,[fromUser:username, msgTo:username, fromRoom:room, enabeLiveChat:'yes'],userSession)
				String msg = getConfig('enableUsersMessage')  ?: 'A member of staff has joined'
				wsChatMessagingService.messageUser(userSession, [liveMessageInitiate:msg])
			}
			wsChatMessagingService.updateLiveList(username,[fromUser:username, fromRoom:room, hasAdmin:hasLiveAdmin],userSession)
		}
		if (userType== ChatUser.CHAT_LIVE_USER_ADMIN) {
			wsChatMessagingService.updateLiveList(username,[:],userSession)
		}
	}

	private void verifyAction(Session userSession,String message) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		String room  =  userSession.userProperties.get("room") as String

		Boolean isuBanned = false
		if (!username)  {
			if (message.startsWith(CONNECTOR)) {
				def values = parseInput(CONNECTOR,message)
				String user = values.user
				String userType = values.msg
				//backward compatible
				def m = message.indexOf(',')>-1 ? message.split(",")[0] : message
				wsChatAuthService.connectUser(m,userSession,room)
				verifyUser(userSession, userType, room,username)
			}
			if (message.startsWith(LIVE_CONNECTOR)) {
				userSession.userProperties.put("joinedRoom", new Date())
				def values = parseInput(LIVE_CONNECTOR,message)
				String user = values.user
				String userType = values.msg
				def m = message.indexOf(',')>-1 ? message.split(",")[0] : message
				wsChatAuthService.connectUser(m,userSession,room,false)
				verifyUser(userSession, userType, room,username)

			}
			if ((myMsg)&&(!isuBanned)) {
				wsChatMessagingService.broadcast(userSession,myMsg)
			}
		} else {
			if (message.startsWith(DISCONNECTOR)) {
				def msg=messageSource.getMessage('wschat.user.left',[username,room].toArray(), "$username has left ${room}",localeResolver.defaultLocale)
				wsChatMessagingService.broadcast(userSession,[message: msg])
				wsChatUserService.removeUser(username)
				wsChatUserService.sendUsers(userSession,username, room)
				userSession.close()
			} else if (message.startsWith(LIVE_DISCONNECTOR)) {
				wsChatUserService.removeUser(username)
				wsChatUserService.sendUsers(userSession,username, room)
				userSession.close()
			} else if (message.startsWith("/pm")) {
				def values = parseInput("/pm ",message)
				String user = values.user
				String msg = values.msg
				if (user!=username) {
					privateMessage(userSession, username,user,msg)
				} else {
					def msga=messageSource.getMessage('wschat.pm.yourself',null, "Private message self?",localeResolver.defaultLocale)
					wsChatMessagingService.messageUser(userSession,[message:msga])
				}

				// livechat message
			} else if (message.startsWith("/lc")) {
				def v= multiParse("/lc ",message)
				String user = v.user
				String userRoom = v.msg
				String msg = v.msg2
				//lets sent a PM to Admin
				wsChatMessagingService.clientLiveMessage(user,[msgFrom:username, fromUser:user, fromRoom:userRoom, liveMessage:msg],userSession)

				// reverse admin message back to specific chat room user
			} else if (message.startsWith("/cl")) {
				def v= multiParse("/cl ",message)
				String user = v.user
				String userRoom = v.msg
				String msg = v.msg2
				//Convert it back to chat window message in adminLiveMessage comparison is done to find right user matching sending original pm
				wsChatMessagingService.adminLiveMessage(user,[msgFrom:username, fromUser:username, msgTo:user, fromRoom:userRoom, liveMessageResponse:msg],userSession)

				//reuse above to enable the chatbox and show send button on end users awaiting
				// a real human to show up
			} else if (message.startsWith("/enableUsers ")) {
				def v= parseInput("/enableUsers ",message)
				String user = v.user
				String userRoom = v.msg
				String msg = getConfig('enableUsersMessage')  ?: 'A member of staff has joined'
				wsChatMessagingService.adminEnableEndScreens(user,[fromUser:username, msgTo:user, fromRoom:userRoom, enabeLiveChat:'yes',  liveMessageInitiate:msg],userSession)

			} else if (message.startsWith("/verifyAdmin")) {
				Boolean useris = isAdmin(userSession)
				def myMsg1 = [:]
				myMsg1.put("isAdmin", useris.toString())
				wsChatMessagingService.messageUser(userSession,myMsg1)
			} else if (message.startsWith("/block")) {
				def values = parseInput("/block ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.blockUser(user,person)
				wsChatUserService.sendUsers(userSession,user, room)
			} else if (message.startsWith("/kickuser")) {
				def p1 = "/kickuser "
				def user = message.substring(p1.length(),message.length())
				wsChatUserService.kickUser(userSession,user)
			} else if (message.startsWith("/banuser")) {
				def values = multiParse("/banuser ",message)
				String user = values.user as String
				String duration = values.msg as String
				String period = values.msg2 as String
				wsChatUserService.banUser(userSession,user,duration,period)
			} else if (message.startsWith("/unblock")) {
				def values = parseInput("/unblock ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.unblockUser(user,person)
				wsChatUserService.sendUsers(userSession,user, room)
			} else if (message.startsWith("/add")) {
				def values = parseInput("/add ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.addUser(user,person)
				wsChatUserService.sendUsers(userSession,user, room)
				def msg=messageSource.getMessage('wschat.friend.added.you',[username].toArray(), "$username has added you to their Friends Listing",localeResolver.defaultLocale)
				privateMessage(userSession, user,person,msg)
			} else if (message.startsWith("/removefriend")) {
				def values = parseInput("/removefriend ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.removeUser(user,person)
				wsChatUserService.sendUsers(userSession,user, room)
				def msg=messageSource.getMessage('wschat.friend.removed.you',[username].toArray(), "$username has removed you from their Friends Listing",localeResolver.defaultLocale)
				privateMessage(userSession, user,person,msg)
			} else if (message.startsWith("/joinRoom")) {
				userSession.userProperties.put("joinedRoom", new Date())
				def values = parseInput("/joinRoom ",message)
				String user = values.user as String
				String rroom = values.msg as String
				if (wsChatRoomService.roomList().toMapString().contains(rroom)) {
					Map<String,Session> records = chatroomUsers.get(username)
					def currentRoom = records.find{it.key==room}
					Session crec2 = records.find{it.key==rroom}?.value
					if (!crec2) {
						if (currentRoom) {
							def msg=messageSource.getMessage('wschat.user.left',[username,room].toArray(), "$username has left ${room}",localeResolver.defaultLocale)
							wsChatMessagingService.broadcast(userSession,["message": msg])
							records.remove("${room}")
							wsChatUserService.sendUsers(userSession,user, room)
							records << ["${rroom}":userSession]
						}
						userSession.userProperties.put("room", rroom)
						room = rroom
						myMsg.put("currentRoom", "${room}")
						wsChatMessagingService.messageUser(userSession,myMsg)
						myMsg = [:]
						wsChatUserService.sendUsers(userSession,user, rroom)
						def msg=messageSource.getMessage('wschat.user.joined',[username,room].toArray(), "${user} has joined ${room}",localeResolver.defaultLocale)
						wsChatMessagingService.broadcast(userSession,[message:msg])
						wsChatRoomService.sendRooms(userSession)
						//Send back list of Javascripts to enable through socket respoonse
						if (isAdmin(userSession)) {
							def adminActions=[[actions:'viewUsers'],[actions:'liveChatsRooms'],[actions:'createConference'],[actions:'viewLiveChats',]]
							wsChatMessagingService.broadcast(userSession,[adminOptions:adminActions])
						}
						wsChatAuthService.addBotToChatRoom(rroom,'chat')
					} else {
						def msg=messageSource.getMessage('wschat.user.already.joined',[username,rroom].toArray(), "${user} has already logged into room ${rroom}, action denied",localeResolver.defaultLocale)
						wsChatMessagingService.messageUser(userSession, [message: msg ])
					}
				}
			} else if (message.startsWith("/joinLiveChatRoom")) {
				userSession.userProperties.put("joinedRoom", new Date())
				def values = parseInput("/joinLiveChatRoom ",message)
				String user = values.user
				String rroom = values.msg
				if (wsChatRoomService.roomList().toMapString().contains(rroom)) {
					Map<String,Session> records = chatroomUsers.get(username)
					def currentRoom = records.find{it.key==room}
					Session crec2 = records.find{it.key==rroom}?.value
					if (!crec2) {
						if (currentRoom) {
							String sendleave = getConfig('send.leaveroom')  ?: 'yes'
							if (sendleave == 'yes') {
								def msg=messageSource.getMessage('wschat.user.left',[username,room].toArray(), "$username has left ${room}",localeResolver.defaultLocale)
								wsChatMessagingService.broadcast(userSession,[message: msg])
							}
							records.remove("${room}")
							wsChatUserService.sendUsers(userSession,user, room)
							records << ["${rroom}":userSession]
						}
						userSession.userProperties.put("room", rroom)
						room = rroom
						wsChatMessagingService.messageUser(userSession,[currentRoom: room])
					} else {
						def msg=messageSource.getMessage('wschat.user.already.joined',[username,rroom].toArray(), "${user} has already logged into room ${rroom}, action denied",localeResolver.defaultLocale)
						wsChatMessagingService.messageUser(userSession, [message:msg])
					}
				}
				wsChatMessagingService.messageUser(userSession,[liveChatMode: rroom])
			} else if (message.startsWith("/listRooms")) {
				wsChatRoomService.listRooms()
			} else if (message.startsWith("/addRoom")) {
				def p1 = "/addRoom "
				def nroom = message.substring(p1.length(),message.length())
				wsChatRoomService.addRoom(userSession,nroom,'chat')
			} else if (message.startsWith("/delRoom")) {
				def p1 = "/delRoom "
				def nroom = message.substring(p1.length(),message.length())
				wsChatRoomService.delRoom(userSession,nroom)
			} else if (message.startsWith("/camenabled")) {
				def p1 = "/camenabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("av", "on")
				myMsg.put("message", "${camuser} has enabled webcam")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/camdisabled")) {
				def p1 = "/camdisabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("av", "off")
				myMsg.put("message", "${camuser} has disabled webcam")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/restartOpponent")) {
				def p1 = "/restartOpponent "
				def gameuser = message.substring(p1.length(),message.length())
				Session currentSession = wsChatUserService.usersSession(gameuser, room)
				wsChatMessagingService.messageUser(currentSession, ["game":"restartOpponent"])
			} else if (message.startsWith("/restartGame")) {
				def p1 = "/restartGame "
				def gameuser = message.substring(p1.length(),message.length())
				Session currentSession = wsChatUserService.usersSession(gameuser, room)
				wsChatMessagingService.messageUser(currentSession, ["game":"restartGame"])
			} else if (message.startsWith("/gameenabled")) {
				def p1 = "/gameenabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("game", "on")
				myMsg.put("message", "${camuser} has enabled TicTacToe")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/gamedisabled")) {
				def p1 = "/gamedisabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("game", "off")
				myMsg.put("message", "${camuser} has disabled TicTacToe")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/webrtcenabled")) {
				def p1 = "/webrtcenabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("rtc", "on")
				myMsg.put("message", "${camuser} has enabled WebrRTC")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/webrtcdisabled")) {
				def p1 = "/webrtcdisabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("rtc", "off")
				myMsg.put("message", "${camuser} has disabled WebrRTC")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/fileenabled")) {
				def p1 = "/fileenabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("file", "on")
				myMsg.put("message", "${camuser} has enabled fileSharing")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/filedisabled")) {
				def p1 = "/filedisabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("file", "off")
				myMsg.put("message", "${camuser} has disabled fileSharing")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/mediaenabled")) {
				def p1 = "/mediaenabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("media", "on")
				myMsg.put("message", "${camuser} has enabled fileSharing")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/mediadisabled")) {
				def p1 = "/mediadisabled "
				def camuser = message.substring(p1.length(),message.length())
				userSession.userProperties.put("media", "off")
				myMsg.put("message", "${camuser} has disabled fileSharing")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser,room)
			} else if (message.startsWith("/flatusers")) {
				wsChatUserService.sendFlatUsers(userSession,username)
			} else if (message.startsWith("deactive_chat_bot") || (message.startsWith("deactive_me"))) {
				String userType = userSession.userProperties.get("userType") as String
				wsChatAuthService.delBotFromChatRoom(username, room, userType, message)
			} else {
				// Usual chat messages bound for all
				myMsg.put("message", "<span class='roomPerson'>${username}: </span><span class='roomMessage'>${message.replaceAll("\\<.*?>","")}</span>")
				wsChatMessagingService.broadcast(userSession,myMsg)
			}
		}
	}
	
	String seperator(String input) {
		if (input && !input.startsWith('/')) {
			input = '/' + input
		}
		return input
	}

	/**
	 * parseInput i.e.
	 * /pm otherUser,message
	 * @param mtype
	 * @param message
	 * @return map of user and msg
	 */
	private Map parseInput(String mtype,String message){
		def mu = message.substring(mtype.length(),message.length())
		def m = mu.indexOf(",")>-1 ? mu.split(',') : mu.split(' ')
		return [user:m[0].trim(), msg:m[1].trim()]
	}

	/**
	 * multiParse will parse an input such as
	 * /banuser someuser,limit:command
	 * /livechat someruser,someRoom,message
	 * @param mtype
	 * @param message
	 * @return map of user msg/room msg2/msg
	 *
	 */
	private Map multiParse(String mtype,String message){
		def mu = message.substring(mtype.length(),message.length())
		def um = mu.split(',')
		def m = um[1].indexOf(':')>-1 ?  um[1].split(':') :  null
		return [user:um[0].trim(), msg:m[0]?m[0].trim():msg, msg2:m[1].trim()]
	}
	
        def getConfig(String configProperty) {
		Holders.config.wschat[configProperty] ?: ''
	}
}
