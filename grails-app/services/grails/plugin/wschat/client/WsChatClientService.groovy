package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatClientEndpoint

import org.codehaus.groovy.grails.web.json.JSONObject



class WsChatClientService {

	//private WsChatClientEndpoint clientEndPoint

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

	def handMessage(WsChatClientEndpoint clientEndPoint, String user,String pmuser, Map aMap, boolean strictMode) {
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
									if (pm) {
										clientEndPoint.sendMessage("/pm ${msgFrom},${sendThis}")
										//if (strictMode==false) {
										//	clientEndPoint.sendMessage("${sendThis}")
										//}
									}else{
										clientEndPoint.sendMessage("${sendThis}")
									}
								}
							}
						}
					}
				});
	}
}
