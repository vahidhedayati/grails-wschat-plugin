package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONArray
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
 * https://github.com/vahidhedayati/testwschat/blob/master/grails-app/services/anythingbut/grails/plugin/wschat/MyOverrideService.groovy
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
 * 
 * 
 import anythingbut.grails.plugin.wschat.MyOverrideService

// Place your Spring DSL code here
beans = {
	wsClientProcessService(MyOverrideService){
		chatClientListenerService = ref('chatClientListenerService')
		wsChatUserService = ref('wsChatUserService')
	}
}

 * run ctrl shift o (in eclipse based ide's ggts etc and that will import MyChatClientService)
 * 
 */
public class WsClientProcessService extends WsChatConfService {
	
	static transactional  =  false
	
	//def grailsApplication
	def chatClientListenerService
	def wsChatUserService

	// CLIENT SERVER CHAT VIA ChatClientListenerService method aka
	// This is server processing of taglib call:
	// <chat:clientWsConnect gsp
	// A demo and for you to change to what you want your backend to do
	// I have commented out a disco = false
	public void processResponse(Session userSession, String message) {
		String username = userSession.userProperties.get("username") as String

		log.debug "DEBUG ${username}: $message"

		// Disconnect automatically
		// set to false (commented out) in this example when a command is receieved
		boolean disco = true

		JSONObject rmesg=JSON.parse(message)

		String actionthis=''
		String msgFrom = rmesg.msgFrom
		boolean pm = false

		String disconnect = rmesg.system
		if (rmesg.privateMessage) {

			JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
			String command = rmesg2.command
			if (command) {
				String event,context=''
				boolean strictMode, masterNode, autodisco, frontenduser = false
				JSONArray data
				JSONArray arguments = rmesg2.arguments as JSONArray
				arguments.each { args ->
					event = args.event
					context = args.context
					data = args.data
					
				}

				def jsonData = (data as JSON).toString()


				log.debug "${event} ${context} ${jsonData}"
				log.debug "${strictMode} ${masterNode} ${autodisco} ${frontenduser}"

				if ( (event == "open_session")  || (autodisco == false)){
					disco = false
				}



				// There is a sleep time put in
				// This is because front end takes while to load up on initial connection
				// This is to match above modified event sMessage which has _received added to event
				// Put in place to stop forever loop
				if (!event.endsWith("_received")) {
					sleep(1000)
					
					
					// You can now do something with the above event received on backend of your application
					// I will give example of event back to sender where again would hit this and you could
					// do same in this block for client app
	
	
					String sMessage = """{
                        "command":"event",
                        "arguments":[
                                        {
                                        "event":"${event}_received",
                                        "context":"$context",
										"data":${jsonData as String}
                                        }
                                    ]
                        }
                    """
					
					chatClientListenerService.sendPM(userSession, msgFrom ,sMessage.replaceAll("\t","").replaceAll("\n",""))
				}
			}
		}
		if (disconnect && disconnect == "disconnect") {
			chatClientListenerService.disconnect(userSession)
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
				chatClientListenerService.disconnect(userSession)
			}else if (actionthis == 'close_my_connection') {
				if (pm) {
					chatClientListenerService.sendPM(userSession, msgFrom,"close_connection")
				}	
			}else{
				// THIS IS AN EXAMPLE
				if (actionthis == "do_something") {
					if (pm) {
						chatClientListenerService.sendPM(userSession, msgFrom,"[PROCESSED]${actionthis}")
					}else{
						chatClientListenerService.sendMessage(userSession, ">>HAVE DONE \n"+actionthis)
					}
				}else{
					// DISCONNECTING HERE OTHERWISE WE WILL GET A LOOP OF REPEATED MESSAGES
					if (disco) {
						chatClientListenerService.disconnect(userSession)

					}
				}
			}
		}
	}

	//OVERRIDE AND SET CUSTOM ACTIONS
	// CLIENT SERVER CHAT VIA WsChatClientService method aka
	// <chat:clientConnect gsp call
	public void processAct(String user, boolean pm,String actionthis, String sendThis, String divId, String msgFrom, 
		boolean strictMode, boolean masterNode) {
		
		Session userSession=wsChatUserService.usersSession(user)
		String username = userSession.userProperties.get("username") as String
		String addon="[PROCESS]"
		def myMap=[pm:pm, actionThis: actionthis, sendThis: sendThis, divId:divId,msgFrom:msgFrom, strictMode:strictMode, masterNode:masterNode]
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
		
		
		/* SET CUSTOM ACTIONS
		if (masterNode) {
			if (actionthis== 'do_task_1') {
				// TODO something on master node that has mappings to do_task_1
				log.info "something on master node that has mappings to do_task_1 TASK1"
			}else if (actionthis== 'do_task_2') {
				// TODO something on master node that has mappings to do_task_2
				log.info "something on master node that has mappings to do_task_2 TASK2"
			}else if (actionthis== 'do_task_3') {
				// TODO something on master node that has mappings to do_task_3
				log.info "something on master node that has mappings to do_task_3 TASK3"
			}
		}
		*/	
		
		
		/*
		 * Fancy block to not start client transmission 
		 * until it finds its own name _frontend logged in
		 *  This way server/client transaction can happen with no issues
		 */
		
		if ( masterNode == false ) {
			boolean found = wsChatUserService.findUser(user+frontend)
			int counter=0
			if (found==false) {
				while (found==false && (counter < 3) ) {
					sleep(600)
					found = wsChatUserService.findUser(user+frontend)
					counter++
				}
			}
		}
		
		if (pm) {
			//if (strictMode==false) {
			//chatClientListenerService.sendMessage(userSession, "${addon}${sendThis}")
			//}
			chatClientListenerService.sendPM(userSession,msgFrom,sendThis)
		}else{
			//chatClientListenerService.sendMessage(userSession, "${addon}${sendThis}")
			if (strictMode==false) {
				chatClientListenerService.sendMessage(userSession, "${addon}${sendThis}")
			}	
		}
	}

	public void truncateSlaves() {
		clientSlave.clear()
	}

	public void truncateMasters() {
		clientMaster.clear()
	}
}
