package grails.plugin.wschat

import grails.plugin.wschat.auth.WsChatAuthService
import grails.plugin.wschat.cam.WsCamService
import grails.plugin.wschat.interfaces.ChatSessions
import grails.plugin.wschat.messaging.WsChatMessagingService
import grails.plugin.wschat.rooms.WsChatRoomService
import grails.plugin.wschat.users.WsChatUserService

import javax.websocket.Session


class ChatUtils extends WsChatConfService  implements ChatSessions {

	ConfigObject config

	WsChatAuthService wsChatAuthService
	WsChatUserService wsChatUserService
	WsChatRoomService wsChatRoomService
	WsChatMessagingService wsChatMessagingService
	WsCamService wsCamService

	private void verifyAction(Session userSession,String message) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		String room  =  userSession.userProperties.get("room") as String
		String connector = "CONN:-"
		Boolean isuBanned = false
		if (!username)  {
			if (message.startsWith(connector)) {
				//TODO
				wsChatAuthService.connectUser(message,userSession,room)
			}
			if ((myMsg)&&(!isuBanned)) {
				wsChatMessagingService.broadcast(userSession,myMsg)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				String dbsup = config.logleavers ?: 'no'

				if (dbsup == 'yes') {
					log.error "Room: >${room}< | User: >${username}<  "
				}


				//userSession.close()
				//wsChatUserService.removeUser(username)
				//String bcasto = config.left.timeout  ?: '0'
				//sleep(bcasto as int)
				//wsChatUserService.sendUsersLoggedOut(croom,username)
				wsChatUserService.logUserOut(userSession,username,room)

			}else if (message.startsWith("/pm")) {
				def values = parseInput("/pm ",message)
				String user = values.user as String
				String msg = values.msg as String
				if (!user.equals(username)) {
					myMsg.put("msgFrom", username)
					myMsg.put("msgTo", user)
					myMsg.put("privateMessage", "${msg}")
					wsChatMessagingService.privateMessage(user,myMsg,userSession)
				}else{
					myMsg.put("message","Private message self?")
					wsChatMessagingService.messageUser(userSession,myMsg)
				}
			}else if (message.startsWith("/block")) {
				def values = parseInput("/block ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.blockUser(user,person)
				wsChatUserService.sendUsers(userSession,user)
			}else if (message.startsWith("/kickuser")) {
				def p1 = "/kickuser "
				def user = message.substring(p1.length(),message.length())
				wsChatUserService.kickUser(userSession,user)
			}else if (message.startsWith("/banuser")) {
				def values = parseBan("/banuser ",message)
				String user = values.user as String
				String duration = values.msg as String
				String period = values.msg2 as String
				wsChatUserService.banUser(userSession,user,duration,period)
			}else if (message.startsWith("/unblock")) {
				def values = parseInput("/unblock ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.unblockUser(user,person)
				wsChatUserService.sendUsers(userSession,user)
			}else if (message.startsWith("/add")) {
				def values = parseInput("/add ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.addUser(user,person)
				wsChatUserService.sendUsers(userSession,user)
			}else if (message.startsWith("/removefriend")) {
				def values = parseInput("/removefriend ",message)
				String user = values.user as String
				String person = values.msg as String
				wsChatUserService.removeUser(user,person)
				wsChatUserService.sendUsers(userSession,user)
			}else if (message.startsWith("/joinRoom")) {
				def values = parseInput("/joinRoom ",message)
				String user = values.user as String
				String rroom = values.msg as String
				if (wsChatRoomService.roomList().toMapString().contains(rroom)) {
					userSession.userProperties.put("room", rroom)
					room = rroom;
					myMsg.put("currentRoom", "${room}")
					wsChatMessagingService.messageUser(userSession,myMsg)

					String sendjoin = config.send.joinroom  ?: 'yes'
					if (sendjoin == 'yes') {
						myMsg = [:]
						wsChatUserService.sendUsers(userSession,user)
						myMsg.put("message", "${user} has joined ${room}")
						wsChatMessagingService.broadcast(userSession,myMsg)
						//broadcast(userSession,["message", "${user} has joined ${room}"])
						wsChatRoomService.sendRooms(userSession)
					}
				}
			}else if (message.startsWith("/listRooms")) {
				wsChatRoomService.ListRooms()
			}else if (message.startsWith("/addRoom")) {
				def p1 = "/addRoom "
				def nroom = message.substring(p1.length(),message.length())
				wsChatRoomService.addRoom(userSession,nroom)
			}else if (message.startsWith("/delRoom")) {
				def p1 = "/delRoom "
				def nroom = message.substring(p1.length(),message.length())
				wsChatRoomService.delRoom(userSession,nroom)
			}else if (message.startsWith("/camenabled")) {
				def p1 = "/camenabled "
				def camuser = message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("av", "on")
				myMsg.put("message", "${camuser} has enabled webcam")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser)
			}else if (message.startsWith("/camdisabled")) {
				def p1 = "/camdisabled "
				def camuser = message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("av", "off")
				myMsg.put("message", "${camuser} has disabled webcam")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser)
				// Usual chat messages bound for all
			}else if (message.startsWith("/webrtcenabled")) {
				def p1 = "/webrtcenabled "
				def camuser = message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("rtc", "on")
				myMsg.put("message", "${camuser} has enabled WebrRTC")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser)
			}else if (message.startsWith("/webrtcdisabled")) {
				def p1 = "/webrtcdisabled "
				def camuser = message.substring(p1.length(),message.length())
				//addCamUser(userSession,camuser)
				userSession.userProperties.put("rtc", "off")
				myMsg.put("message", "${camuser} has disabled WebrRTC")
				wsChatMessagingService.broadcast(userSession,myMsg)
				wsChatUserService.sendUsers(userSession,camuser)
				// Usual chat messages bound for all
			}else{
				myMsg.put("message", "${username}: ${message}")
				wsChatMessagingService.broadcast(userSession,myMsg)
				//messageUser(userSession,myMsg)
			}
		}
	}

	Boolean camLoggedIn(String user) {
		Boolean loggedin = false
		try {
			Iterator<Session> iterator = camsessions?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec = iterator?.next()
					if (crec.isOpen()) {
						def cuser = crec.userProperties.get("camusername").toString()
						if (cuser.equals(user)) {
							loggedin = true
						}
					}
				}
			}
		} catch (IOException e) {
			log.error ("onMessage failed", e)
		}
		return loggedin
	}



	private Map<String, String> parseInput(String mtype,String message){
		def p1 = mtype
		def mu = message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset = []
		if (mu.indexOf(",")>-1) {
			user = mu.substring(0,mu.indexOf(","))
			msg = mu.substring(user.length()+1,mu.length())
		}else{
			user = mu.substring(0,mu.indexOf(" "))
			msg = mu.substring(user.length()+1,mu.length())
		}
		Map<String, String> values  =  new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}


	private Map<String, String> parseBan(String mtype,String message){
		def p1 = mtype
		def mu = message.substring(p1.length(),message.length())
		def msg2
		def resultset = []
		def	user = mu.substring(0,mu.indexOf(","))
		def	msg = mu.substring(user.length()+1,mu.length())
		if (msg.indexOf(':')>-1) {
			msg2 = msg.substring(msg.indexOf(':')+1,msg.length())
			msg = msg.substring(0,msg.indexOf(':'))
		}
		Map<String, String> values  =  new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		if (msg2){
			values.put("msg2", msg2);
		}
		return values
	}
}
