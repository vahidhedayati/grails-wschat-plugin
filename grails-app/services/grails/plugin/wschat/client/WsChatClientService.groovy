package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService

import javax.websocket.Session

import org.grails.web.json.JSONObject

public class WsChatClientService extends WsChatConfService {
	
	static transactional  =  false
	

	def wsChatUserService

	private Session userSession = null
	
	public WsChatClientEndpoint conn(String hostname, String appName, String room, String user ) {
		String addAppName = config.add.appName ?: 'yes'
		
		String uri="ws://${hostname}/${appName}/${CHATAPP}/${room}"
		if (addAppName=="no") {
			uri="ws://${hostname}/${CHATAPP}/${room}"
		}
		
		WsChatClientEndpoint clientEndPoint =
				new WsChatClientEndpoint(new URI(uri))
		clientEndPoint.connectClient(user)
		
		return clientEndPoint
	}

	def sendMessage(WsChatClientEndpoint clientEndPoint, String message) {
		clientEndPoint.sendMessage("${message}")
	}

	def sendArrayPM(WsChatClientEndpoint clientEndPoint, ArrayList pmuser, String message) {
		pmuser.each { cuser ->
			boolean found
			if (!cuser.toString().endsWith(frontend)) {
				found=wsChatUserService.findUser(cuser+frontend)
				if (found) {
					clientEndPoint.sendMessage("/pm ${cuser+frontend},${message}")
				}
			}
			found=wsChatUserService.findUser(cuser)
			if (found) {
				clientEndPoint.sendMessage("/pm ${cuser},${message}")
			}

		}
	}
	def sendPM(WsChatClientEndpoint clientEndPoint, String pmuser, String message) {
		boolean found
		if (!pmuser.endsWith(frontend)) {
			found=wsChatUserService.findUser(pmuser+frontend)
			if (found) {
				clientEndPoint.sendMessage("/pm ${pmuser+frontend},${message}")
			}
		}
		found=wsChatUserService.findUser(pmuser)
		if (found) {
			clientEndPoint.sendMessage("/pm ${pmuser},${message}")
		}
	}

	def disco(WsChatClientEndpoint clientEndPoint, String user) {
		clientEndPoint.disconnectClient(user)
	}


	def handMessage(WsChatClientEndpoint clientEndPoint, String user,
			ArrayList pmuser, Map aMap, boolean strictMode,String divId, boolean masterNode) {
			
		clientEndPoint.addMessageHandler(
				new WsChatClientEndpoint.MessageHandler() {
					public void handleMessage(String message) {
						if (message.startsWith('{"')) {
						JSONObject rmesg=JSON?.parse(message)
						String actionthis=''
						String msgFrom = rmesg.msgFrom
						String disconnect = rmesg.system
						if (disconnect && disconnect == "disconnect") {
							clientEndPoint.sendMessage("DISCO:-"+user)
						}
						boolean pm = false
						if (strictMode) {
							pmuser?.each { cuser ->
								if (msgFrom && msgFrom == cuser) {
									actionthis = rmesg.privateMessage
									pm = true
								}
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
									pmuser?.each { cuser ->
										if (msgFrom && msgFrom == cuser) {
											actionthis = matcher[0][2]
										}
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
									clientEndPoint.processAction( user, pm, actionthis, sendThis, divId ?: '',msgFrom,strictMode,masterNode)

								}
							}
						}
						
					}else{
						clientEndPoint.sendMessage("${message}")
					}
				  }
				})
	}


}
