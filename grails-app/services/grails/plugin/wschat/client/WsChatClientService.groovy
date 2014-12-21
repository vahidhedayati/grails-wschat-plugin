package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatClientEndpoint
import grails.plugin.wschat.interfaces.ClientSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class WsChatClientService implements ClientSessions {

	def grailsApplication

	public WsChatClientEndpoint conn(String hostname, String appName, String room, String user ) {
		WsChatClientEndpoint clientEndPoint = new WsChatClientEndpoint(new URI("ws://${hostname}/${appName}/WsChatEndpoint/${room}"))
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

	public void processAct(Session userSession, boolean pm,String actionthis, String sendThis,
			String divId, String msgFrom, boolean strictMode, boolean masterNode) {

		String addon="[PROCESS]"

		def myMap=[pm:pm, actionThis: actionthis, sendThis: sendThis, divId:divId,
			msgFrom:msgFrom, strictMode:strictMode, masterNode:masterNode ]


		if (masterNode) {
			addon="[PROCESSED]"
			if (saveClients) {
				clientMaster.add(myMap)
			}
		}else{
			if (saveClients) {
				clientSlave.add(myMap)
			}
		}

		if (pm) {
			//if (strictMode==false) {
			userSession.basicRemote.sendText("${addon}"+sendThis)
			//}
			userSession.basicRemote.sendText("/pm ${msgFrom},${sendThis}")
		}else{
			userSession.basicRemote.sendText("${addon}${sendThis}")
			userSession.basicRemote.sendText("${sendThis}")
		}
	}

	def handMessage(Session userSess, WsChatClientEndpoint clientEndPoint, String user,
			String pmuser, Map aMap, boolean strictMode,String divId, boolean masterNode) {

		clientEndPoint.addMessageHandler(new WsChatClientEndpoint.MessageHandler() {
					public void handleMessage(String message) {
						JSONObject rmesg=JSON.parse(message)
						def actionthis=''
						def msgFrom = rmesg.msgFrom
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

							if (actionthis == 'close_connection') {
								clientEndPoint.sendMessage("DISCO:-"+user)
							}else{
								if (aMap.containsKey(actionthis)) {
									String sendThis=aMap[actionthis]
									clientEndPoint.processAction(userSess, pm, actionthis, sendThis, divId ?: '',msgFrom,strictMode,masterNode)
								}
							}
						}
					}
				});
	}


	private boolean getSaveClients() {
		return isConfigEnabled(config.storeForFrontEnd)
	}

	private getConfig() {
		grailsApplication?.config?.wschat
	}

	private boolean isConfigEnabled(String config) {
		return Boolean.valueOf(config ?: false)
	}

	public void truncateSlaves() {
		clientSlave.clear()
	}

	public void truncateMasters() {
		clientMaster.clear()
	}

}
