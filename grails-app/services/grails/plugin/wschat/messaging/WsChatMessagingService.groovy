package grails.plugin.wschat.messaging

import grails.converters.JSON
import grails.plugin.wschat.ChatBlockList
import grails.plugin.wschat.ChatMessage
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.OffLineMessage
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.ChatCustomerBooking
import grails.transaction.Transactional

import javax.websocket.Session


class WsChatMessagingService extends WsChatConfService {

	def chatUserUtilService
	def i18nService


	void sendMsg(Session userSession,String msg) throws Exception {
		try {
			if (userSession && userSession.isOpen()) {
				String urecord = userSession.userProperties.get("username") as String
				log.debug "sendMsg ${urecord}: ${msg}"
				boolean isEnabled = boldef(config.dbstore_user_messages)
				if (isEnabled) {
					persistMessage(msg ,urecord)
				}
				userSession.basicRemote.sendText(msg)
			}
		} catch (Exception e) {
			//e.printStackTrace()
		}
	}

	void sendDelayedMessage(Session userSession,final String message, int delay) {
		def asyncProcess = new Thread({
			sleep(delay)
			userSession.basicRemote.sendText(message)
		} as Runnable)
		asyncProcess.start()
	}

	void messageUser(Session userSession,Map msg) {
		def myMsgj = (msg as JSON).toString()
		sendMsg(userSession, myMsgj)
	}

	void privateMessage(String user,Map msg,Session userSession) {
		def myMsg = [:]
		def myMsgj = msg as JSON
		String urecord = userSession.userProperties.get("username")
		Boolean found = false
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,user,urecord)
			persistMessage(myMsgj as String,urecord,urecord)
		}
		chatNames?.each { String cuser, Map<String,Session> records ->
			if (cuser.equals(user)) {
				records?.eachWithIndex { String room, Session crec, i ->
					if (crec && crec.isOpen() && i==0) {
						boolean sendIt = checkPM(urecord,user)
						boolean sendIt2 = checkPM(user,urecord)
						found = true
						if (sendIt&&sendIt2) {
							crec.basicRemote.sendText(myMsgj as String)
							def msga=i18nService.msg("wschat.pm.sent","pm sent to ${user}",[user])
							messageUser(userSession,[message:msga])
						}else{
							def msga=i18nService.msg("wschat.pm.not.sent.blocked","Private Message NOT sent to ${user}, you have been blocked !",[user])
							messageUser(userSession,[message:msga])
						}
					}
				}
			}
		}
		if (found == false) {
			verifyOfflinePM(user, myMsgj as String, userSession, urecord)
		}
	}

	/**
	 * clientLiveMessage
	 * converts a modified chat window message into a PM
	 * liveChat client messaging admin of chatRoom
	 * @param user
	 * @param msg
	 * @param userSession
	 */
	void clientLiveMessage(String user,Map msg,Session userSession) {
		def myMsg = (msg as JSON).toString()
		String urecord = userSession.userProperties.get("username")
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			if (user) {
				persistMessage(myMsg, user, urecord)
			}
			persistMessage(myMsg,urecord,urecord)
		}
		chatNames?.each { String cuser, Map<String,Session> records ->
			records?.each { String room, Session crec ->
				if (crec && crec.isOpen() && room==msg.fromRoom) {
					if (chatUserUtilService.isLiveAdmin(cuser)) {
						crec.basicRemote.sendText(myMsg)
						log.debug "User->Admin: ${cuser} ${myMsg}"
					}
				}
			}
		}
	}
/**
 * This is the message convertor from admin back to end user
 * @param user
 * @param msg
 * @param userSession
 */
	void adminLiveMessage(String user,Map msg,Session userSession) {
		def myMsg = (msg as JSON).toString()
		String urecord = userSession.userProperties.get("username")
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			if (user) {
				persistMessage(myMsg,user,urecord)
			}
			persistMessage(myMsg,urecord,urecord)
		}
		chatNames?.each { String cuser, Map<String,Session> records ->
			records?.each { String room, Session crec ->
				if (crec && crec.isOpen() && room==msg.fromRoom && cuser==msg.msgTo) {
					crec.basicRemote.sendText(myMsg)
					log.debug "Admin->User: ${cuser} ${myMsg}"
				}
			}
		}
	}

	/**
	 * Does two things sends message with
	 * enabeLiveChat:true,  liveMessageInitiate: custom or hardcoded message
	 * end user receives both json objects and enables their chat space + notifies
	 * them a member of staff has joined
	 * @param user
	 * @param msg
	 * @param userSession
	 */
	void adminEnableEndScreens(String user,Map msg,Session userSession) {
		def myMsg = (msg as JSON).toString()
		String urecord = userSession.userProperties.get("username")
		boolean isEnabled = boldef(config.dbstore_pm_messages)
		if (isEnabled) {
			if (user) {
				persistMessage(myMsg,user,urecord)
			}
			persistMessage(myMsg,urecord,urecord)
		}
		chatNames?.each { String cuser, Map<String,Session> records ->
			records?.each { String room, Session crec ->
				if (crec && crec.isOpen() && room==msg.fromRoom) {
					if (!chatUserUtilService.isLiveAdmin(cuser)) {
						crec.basicRemote.sendText(myMsg)
					}
				}
			}
		}
	}

	/**
	 * Updates/collects liveChat user details and sends back to valid userTypes
	 * @param user
	 * @param msg
	 * @param userSession
	 */
	void updateLiveList(String user,Map msg,Session userSession) {
		List admins=[]
		String urecord = userSession.userProperties.get("username")
		chatNames?.each { String cuser, Map<String,Session> records ->
			records?.each { String room, Session crec ->
				if (crec && crec.isOpen() && crec.userProperties.get("userType") == ChatUser.CHAT_LIVE_USER_ADMIN) {
					admins<<crec
				}
			}
		}
		//Any admins ? yes gen list and send one list to all
		if (admins) {
			String message=populateList(msg) as String
			admins?.each {Session crec->
				if (crec && crec.isOpen()) {
					crec.basicRemote.sendText(message)
				}
			}
		}
	}

	/*
	 * populates a list of admin & livechat users particpating/
	 * awaiting livechat
	 */
	JSON populateList(Map msg) {
		List result=[]
		chatNames.each { String cuser, Map<String,Session> records ->
			records?.each {String croom, Session crec ->
				if (crec && crec.isOpen() &&  (crec.userProperties.get("userType") == ChatUser.CHAT_LIVE_USER) && cuser!=msg.fromUser) {
					String userPerm = crec.userProperties.get("userLevel")
					String startTime = (crec.userProperties.get("startTime") as Date)?.format("yyyy-MM-dd HH:mm:ss")
					String joinedRoom = (crec.userProperties.get("joinedRoom") as Date)?.format("yyyy-MM-dd HH:mm:ss")
					boolean isAdmin =  chatUserUtilService.isLiveAdmin(cuser,false)
					result << [room: croom, user:cuser,isAdmin:isAdmin, joinedRoom:joinedRoom, startTime:startTime, userPerm:userPerm]
				}
			}
		}

		/*
		 * GroupBy room collection saving a lot of agro
		 */
		def finalResult=[liveChatrooms:result.groupBy{it.room}]
		return finalResult as JSON
	}

	@Transactional
	Boolean checkPM(String username, String urecord) {
		return ChatBlockList.findByChatuserAndUsername(currentUser(username),urecord)?false:true
	}

	void broadcast2all(Map msg) {
		def myMsgj = msg as JSON
		chatNames?.each { String cuser,Map<String,Session> records ->
			records?.each { String room, Session crec ->
				if (crec && crec.isOpen()) {
					crec.basicRemote.sendText(myMsgj as String);
				}
			}
		}
	}

	void broadcast(Session userSession,Map msg) {
		def myMsgj = msg as JSON
		String room = userSession.userProperties.get("room") as String
		String urecord = userSession.userProperties.get("username") as String
		boolean isEnabled = boldef(config.dbstore_room_messages)
		if (isEnabled) {
			persistMessage(myMsgj as String,urecord)
		}
		chatNames?.each { String cuser, Map<String,Session> records ->
			Session crec = records.find{it.key==room}?.value
			if (crec && crec.isOpen() && room.equals(crec.userProperties.get("room"))) {
				crec.basicRemote.sendText(myMsgj as String);
			}
		}
	}

	@Transactional
	ChatUser currentUser(String username) {
		ChatUser cu =  ChatUser.findByUsername(username)
		return cu
	}
	void jsonmessageUser(Session userSession,String msg) {
		userSession.basicRemote.sendText(msg as String)
	}

	void jsonmessageOther(Session userSession,String msg,String realCamUser, boolean fileUser=null) {
		def uList = camNames
		if (fileUser) {
			uList = fileroomUsers
		}
		uList?.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String camuser = crec.userProperties.get("camuser") as String
				if ((camuser.startsWith(realCamUser+":"))&&(!camuser.toString().endsWith(realCamUser))) {
					crec.basicRemote.sendText(msg as String)
				}
			}
		}
	}

	void jsonmessageOwner(Session userSession,String msg,String realCamUser, boolean fileUser=null) {
		def uList = camNames
		if (fileUser) {
			uList = fileroomUsers
		}
		uList?.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				def cmuser = crec.userProperties.get("camusername").toString()
				String camuser = crec.userProperties.get("camuser") as String
				if ((camuser.startsWith(realCamUser+":"))&&(camuser.toString().endsWith(realCamUser))) {
					crec.basicRemote.sendText(msg as String)
				}
			}
		}
	}

	@Transactional
	private void verifyOfflinePM(String user,String message,Session userSession,String username) {
		boolean isEnabled = boldef(config.offline_pm)
		if (isEnabled) {
			def chat = ChatUser.findByUsername(user)
			if (chat) {
				def cm = new OffLineMessage(user: username, contents: message, offlog: chat?.offlog, readMsg: false)
				if (!cm.save()) {
					log.error "verifyOfflinePM issue:  ${cm.errors}"
				}
				def msga=i18nService.msg("wschat.offline.pm.sent","Offline message sent to ${user}",[user])
				messageUser(userSession,[message: msga])
			} else{
				def msga=i18nService.msg("wschat.unable.pm.nouser","Error: ${user} not found - unable to send PM",[user])
				messageUser(userSession,[message: msga])
			}
		}else{
			def msga=i18nService.msg("wschat.offline.pm.disabled","offline messaging not enabled. Message not sent")
			messageUser(userSession,["message": msga])
		}
	}

	@Transactional
	private void persistMessage(String message, String user, String username=null) {
		boolean isEnabled = boldef(config.dbstore)
		if (isEnabled) {
			def chat = ChatUser.findByUsername(user)
			def cm = new ChatMessage(user: username, contents: message, log: chat?.log)
			if (!cm.save()) {
				log.error "Persist Message issue: ${cm.errors}"
			}
		}
	}

	private Boolean boldef(def input) {
		boolean isEnabled = true
		if (input) {
			if (input instanceof String) {
				isEnabled = isConfigEnabled(input)
			}else{
				isEnabled = input
			}
		}
		return isEnabled
	}
}
