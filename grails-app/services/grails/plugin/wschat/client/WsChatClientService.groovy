package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.interfaces.ClientSessions

import javax.websocket.ContainerProvider
import javax.websocket.SendHandler
import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class WsChatClientService implements ClientSessions {

	def grailsApplication
	private Session userSession = null
	public WsChatClientEndpoint conn(String hostname, String appName, String room, String user ) {
		WsChatClientEndpoint clientEndPoint = 
		new WsChatClientEndpoint(new URI("ws://${hostname}/${appName}/${CHATAPP}/${room}"))
		clientEndPoint.connectClient(user)
		return clientEndPoint
	}

	def sendMessage(WsChatClientEndpoint clientEndPoint, String message) {
		clientEndPoint.sendMessage("${message}")
	}

	def sendPM(WsChatClientEndpoint clientEndPoint, String pmuser, String message) {
		clientEndPoint.sendMessage("/pm ${pmuser},${message}")
	}

	def disco(WsChatClientEndpoint clientEndPoint, String user) {
		clientEndPoint.disconnectClient(user)
	}

	def handMessage(Session userSess, WsChatClientEndpoint clientEndPoint, String user,
			String pmuser, Map aMap, boolean strictMode,String divId, boolean masterNode) {

		clientEndPoint.addMessageHandler(
				new WsChatClientEndpoint.MessageHandler() {
					public void handleMessage(String message) {
						JSONObject rmesg=JSON.parse(message)
						def actionthis=''
						def msgFrom = rmesg.msgFrom
						String disconnect = rmesg.system
						if (disconnect && disconnect == "disconnect") {
							clientEndPoint.sendMessage("DISCO:-"+user)
						}
						boolean pm = false
						if (strictMode) {
							if (msgFrom && msgFrom == pmuser) {
								actionthis = rmesg.privateMessage
								pm = true
							}
						}else{
							if (msgFrom ) {
								actionthis = rmesg.privateMessage
								pm = true
							}
						}
						def rmessage = rmesg.message
						if (rmessage) {
							def matcher = (rmessage =~ /(.*): (.*)/)
							if (matcher.matches()){
								msgFrom = matcher[0][1]
								if (strictMode) {
									if (msgFrom && msgFrom == pmuser) {
										actionthis = matcher[0][2]
									}
								}else{
									if (msgFrom) {
										actionthis = matcher[0][2]
									}
								}
							}
						}

						if (actionthis) {
							if ( (actionthis == 'close_connection') 
								|| (actionthis.startsWith('DISCO:-')) ) {
								clientEndPoint.sendMessage("DISCO:-"+user)
							}else{
								if (aMap.containsKey(actionthis)) {
									String sendThis=aMap[actionthis]
									clientEndPoint.processAction(userSess, pm, actionthis, sendThis, divId ?: '',msgFrom,strictMode,masterNode)

								}
							}
						}
					}

				})
	}

	private getConfig() {
		grailsApplication?.config?.wschat
	}
}
