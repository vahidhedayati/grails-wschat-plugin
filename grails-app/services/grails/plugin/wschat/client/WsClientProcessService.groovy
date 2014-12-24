package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.interfaces.ClientSessions

import javax.websocket.ContainerProvider
import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

/*
 * Vahid Hedayati
 * December 2014
 * WsClientProcessService is the response processing for chat client response
 * Override this service in your main app and set these to do what you wish
 * 
 * Primary processResponse is related to chat:clientWsConnect taglib call 
 * has no maps and is all up to you what you want to respond with
 * do_something is an example that returns a response otherwise it disconnects
 * 
 * Secondary processAct is called via chat:clientConnect tag lib
 * It sets up responses / and interface update if config is enabled:
 * wschat.storeForFrontEnd="true"
 * 
 * The interface hashmaps can be used for non websocket longpolling front end updates
 * 
 * Both methods must have autodisco="false"  (default action if tag not defined)
 * for these to work otherwise chat client will disconnect
 * after sending initial response
 * 
 * Override Service Howto:
 * 
 * 1. Create a service in your app:
 * 
 * package grails.plugin.wschat.myclient
 * import grails.plugin.wschat.client.WsClientProcessService
 * import javax.websocket.Session
 * public  class MyChatClientService extends WsClientProcessService {
 * 
 * @Override
 * public void processAct(Session userSession, boolean pm,String actionthis, String sendThis,String divId, String msgFrom, boolean strictMode, boolean masterNode) {
 * 		...
 * 		....
 * 		.. copy paste each service item you wish to override and add the annotation above it
 * 		.. then change what you wish to alter from default plugin methods
 * 		..
 * 		..
 * 2. Setting up bean :
 * 
 * Open conf/spring/resources.groovy
 * beans = {
 * 		wsClientProcessService(MyChatClientService)
 * 	}
 * run ctrl shift o (in eclipse based ide's ggts etc and that will import MyChatClientService)
 * 
 */
public class WsClientProcessService  implements ClientSessions {

	def grailsApplication
	def chatClientListenerService


	// CLIENT SERVER CHAT VIA ChatClientListenerService method aka
	// <chat:clientWsConnect gsp call
	public void processResponse(Session userSession, String message) {
		JSONObject rmesg=JSON.parse(message)
		def actionthis=''
		def msgFrom = rmesg.msgFrom
		boolean pm = false
		String disconnect = rmesg.system
		if (disconnect && disconnect == "disconnect") {
			chatClientListenerService.sendMessage(userSession, DISCONNECTOR)
		}
		if (msgFrom ) {
			actionthis = rmesg.privateMessage
			pm = true
		}

		def rmessage = rmesg.message
		if (rmessage) {
			def matcher = (rmessage =~ /(.*): (.*)/)
			if (matcher.matches()){
				msgFrom = matcher[0][1]
				if (msgFrom) {
					actionthis = matcher[0][2]
				}
			}
		}

		if (actionthis) {

			if (actionthis == 'close_connection') {
				chatClientListenerService.sendMessage(userSession, DISCONNECTOR)
			}else{
				// THIS IS AN EXAMPLE
				if (actionthis == "do_something") {
					chatClientListenerService.sendMessage(userSession, ">>HAVE DONE \n"+actionthis)
				}else{
					// DISCONNECTING HERE OTHERWISE WE WILL GET A LOOP OF REPEATED MESSAGES
					chatClientListenerService.sendMessage(userSession, DISCONNECTOR)
				}

			}
		}
	}
	

	// CLIENT SERVER CHAT VIA WsChatClientService method aka
	// <chat:clientConnect gsp call
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



	private boolean getSaveClients() {
		return isConfigEnabled(config.storeForFrontEnd ?: 'false')
	}

	private boolean isConfigEnabled(String config) {
		return Boolean.valueOf(config)
	}

	public void truncateSlaves() {
		clientSlave.clear()
	}

	public void truncateMasters() {
		clientMaster.clear()
	}


	private getConfig() {
		grailsApplication?.config?.wschat
	}
}
