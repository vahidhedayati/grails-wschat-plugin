package grails.plugin.wschat.client

import grails.converters.JSON
import org.grails.web.json.JSONObject
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.beans.ConfigBean

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ChatClientListenerService extends WsChatConfService {

	static transactional = false
	def wsChatRoomService
	def wsChatUserService
	def chatClientOverrideService

	void sendArrayPM(Session userSession, ArrayList user, String message) {
		ConfigBean bean = new ConfigBean()
		user.each { cuser ->
			boolean found
			found = wsChatUserService.findUser(cuser)
			if (found) {
				userSession.basicRemote.sendText("/pm ${cuser},${message}")
			}
			if (!cuser.toString().endsWith(bean.frontUser)) {
				found = wsChatUserService.findUser(cuser + bean.frontUser)
				if (found) {
					userSession.basicRemote.sendText("/pm ${cuser + bean.frontUser},${message}")
				}
			}
		}
	}

	void sendPM(Session userSession, String user, String message) {
		String username = userSession.userProperties.get("username") as String
		boolean found
		ConfigBean bean = new ConfigBean()
		found = wsChatUserService.findUser(user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
		if (user && !user.endsWith(bean.frontUser)) {
			found = wsChatUserService.findUser(user + bean.frontUser)
			if (found) {
				userSession.basicRemote.sendText("/pm ${user + bean.frontUser},${message}")
			}
		}
	}

	void sendDelayedMessage(Session userSession, final String message, int delay) {
		def asyncProcess = new Thread({
			sleep(delay)
			userSession.basicRemote.sendText(message)
		} as Runnable)
		asyncProcess.start()
	}

	void sendMessage(Session userSession, final String message) {
		userSession.basicRemote.sendText(message)
	}

	def connectUserRoom = { String user, String room, Closure closure ->
		ConfigBean bean = new ConfigBean()
		Session oSession = p_connect(bean.uri, user, room)
		try {
			closure(oSession)
		} catch (e) {
			throw e
		}
		finally {
			disconnect(oSession)
		}
	}

	def connectRoom = { String room, Closure closure ->
		ConfigBean bean = new ConfigBean()
		String oUsername = config.app.id ?: "[${(Math.random() * 1000).intValue()}]-$room";
		Session oSession = p_connect(bean.uri, oUsername, room)
		try {
			closure(oSession)
		} catch (e) {
			throw e
		}
		finally {
			disconnect(oSession)
		}
	}

	Session connect() {
		ConfigBean bean = new ConfigBean()
		def room = wsChatRoomService.returnRoom(true)
		String oUsername = config.app.id ?: "[${(Math.random() * 1000).intValue()}]-$room";
		Session csession = p_connect(bean.uri, oUsername, room)
		return csession
	}

	Session p_connect(String uri, String username, String room) {
		String oRoom = room ?: config.room
		URI oUri
		if (uri) {
			oUri = URI.create(uri + oRoom);
		}
		def container = ContainerProvider.getWebSocketContainer()
		Session oSession
		try {
			oSession = container.connectToServer(ChatClientEndpoint.class, oUri)
			oSession.basicRemote.sendText(CONNECTOR + username)
		} catch (Exception e) {
			e.printStackTrace()
			if (oSession && oSession.isOpen()) {
				oSession.close()
			}
			return null
		}
		oSession.userProperties.put("username", username)
		return oSession
	}

	Session disconnectLive(Session userSession, String chatUser, String room, String botName) {
		Session nsess = wsChatUserService.usersSession(botName, room)
		sendMessage(nsess, DISCONNECTOR)
	}

	Session disconnectChat(Session userSession, String chatUser, String room, String botName) {
		int userCount = 0
		chatNames.each { String cuser, Map<String, Session> records ->
			def crec = records.find { it.key == room }
			if (crec && cuser != botName) {
				userCount++
			}
		}
		if (userCount <= 1) {
			sendMessage(userSession, DISCONNECTOR)
		}
	}


	Session disconnect(Session userSession) {
		try {
			if (userSession && userSession.isOpen()) {
				String user = userSession.userProperties.get("username") as String
				String room = userSession.userProperties.get("room") as String
				if (user) {
					ConfigBean bean = new ConfigBean()
					sendMessage(userSession, DISCONNECTOR)
					Session nsess
					if (user.endsWith(bean.frontUser)) {
						nsess = wsChatUserService.usersSession(user.substring(0, user.indexOf(bean.frontUser)), room)
					} else {
						nsess = wsChatUserService.usersSession(user + bean.frontUser, room)
					}
					if (nsess) {
						sendMessage(nsess, DISCONNECTOR)
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace()
		}
		return userSession
	}

	void alertEvent(def _oSession, String _event, String _context, JSON _data,
					ArrayList cusers, boolean masterNode, boolean strictMode, boolean autodisco,
					boolean frontenduser) {

		def oSession = _oSession ?: connect()
		String sMessage = """{
                        "command":"event",
                        "arguments":[
                                        {
                                        "event":"$_event",
                                        "context":"$_context",
										"data":[${_data as String}]
                                        }
                                    ]
                        }
                    """
		cusers.each { userId ->
			sendPM(oSession,
					chatClientOverrideService.getGlobalReceiverNameFromUserId(userId as String),
					sMessage.replaceAll("\t", "").replaceAll("\n", ""))
		}
		if (_oSession == null) {
			disconnect(oSession)
		}
	}
}