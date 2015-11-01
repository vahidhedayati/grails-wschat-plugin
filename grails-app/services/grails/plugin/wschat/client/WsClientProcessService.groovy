package grails.plugin.wschat.client

import com.fasterxml.jackson.databind.deser.impl.PropertyValue
import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import grails.plugin.wschat.ChatCustomerBooking
import grails.plugin.wschat.ChatUserProfile
import javax.websocket.Session
import grails.plugin.wschat.beans.ConfigBean
import grails.plugin.wschat.beans.ChatBotBean
import grails.transaction.Transactional
import grails.plugin.wschat.ChatAI
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatBadWords
import grails.plugin.wschat.ChatCustomerBooking
import grails.plugin.wschat.ChatMessage

import javax.websocket.Session

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
		grailsApplication = ref('grailsApplication')
		chatClientListenerService = ref('chatClientListenerService')
		wsChatUserService = ref('wsChatUserService')
	}
}

 * run ctrl shift o (in eclipse based ide's ggts etc and that will import MyChatClientService)
 * 
 */
public class WsClientProcessService extends WsChatConfService {

	def chatClientListenerService
	def wsChatUserService
	def wsChatBookingService
	def wsChatMessagingService
	def chatUserUtilService

	// DO NOT Disconnect automatically - required for live chat!
	static boolean disco = false


	// CLIENT SERVER CHAT VIA ChatClientListenerService method aka
	// This is server processing of taglib call:
	// <chat:clientWsConnect gsp
	// A demo and for you to change to what you want your backend to do
	// I have commented out a disco = false
	@Transactional
	void processResponse(Session userSession, String message) {
		String username = userSession.userProperties.get("username") as String
		//String room = userSession.userProperties.get("room") as String
		log.debug "DEBUG ${username}: $message"
		String assistant = config.liveChatAssistant ?: 'assistant'
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
					this.disco = false
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
		//Cut back on DB lookups store chat / admin info into chatBotBean
		ChatBotBean chatBotBean = userSession.userProperties.get('chatBotBean') as ChatBotBean
		ChatCustomerBooking ccb
		boolean isLiveAdmin = false
		boolean emailSent = false
		boolean adminVerified = false
		if (chatBotBean) {
			ccb	= chatBotBean.customer
			isLiveAdmin	= chatBotBean.isLiveAdmin
			emailSent	= chatBotBean.emailSent
			adminVerified = chatBotBean.adminVerified
		}

		if (disconnect && disconnect == "disconnect") {
			chatClientListenerService.disconnect(userSession)
		}

		if (msgFrom) {
			actionthis = rmesg.privateMessage
			pm = true
		} else {
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
		}
		Session currentSession
		if (actionthis == 'close_connection') {
			chatClientListenerService.disconnect(userSession)
		} else if (actionthis == 'deactive_me') {
			if (ccb) {
				ccb.active=false
				ccb.save()
			}
			String room = returnRoom(username)
			if (room) {
				chatClientListenerService.disconnectLive(userSession, msgFrom, room, username)
			}
		} else if (actionthis == 'deactive_chat_bot') {
			String room = returnRoom(username)
			if (room) {
				chatClientListenerService.disconnectChat(userSession, msgFrom, room, username)
			}
		} else if (actionthis == 'close_my_connection') {
			if (pm) {
				chatClientListenerService.sendPM(userSession, msgFrom,"close_connection")
			}
		} else if (actionthis == "do_something") {
			if (pm) {
				chatClientListenerService.sendPM(userSession, msgFrom,"[PROCESSED]${actionthis}")
			}else{
				chatClientListenerService.sendMessage(userSession, ">>HAVE DONE \n"+actionthis)
			}
		}  else  if (msgFrom && msgFrom != username) {
			currentSession = returnSession(msgFrom)
			boolean nameRequired = true
			boolean emailRequired = true
			boolean helpRequested = false

			String room, userType
			if (currentSession) {
				nameRequired = currentSession.userProperties.get("nameRequired") as boolean
				emailRequired = currentSession.userProperties.get("emailRequired") as boolean
				helpRequested = currentSession.userProperties.get("helpRequested") as boolean
				userType = currentSession.userProperties.get("userType") as String
				room = returnRoom(msgFrom)
			}
			boolean askName = boldef(config.liveChatAskName)
			boolean askEmail = boldef(config.liveChatAskEmail)
			if (currentSession && userType && userType=='liveChat') {
				if (!chatBotBean || !adminVerified) {
					setBotBean(userSession, msgFrom, chatBotBean)
					chatBotBean = userSession.userProperties.get('chatBotBean') as ChatBotBean
					if (chatBotBean) {
						ccb	= chatBotBean.customer
						isLiveAdmin	= chatBotBean.isLiveAdmin
					}
				}
				if (!helpRequested && !isLiveAdmin && !emailSent) {
					wsChatBookingService.sendLiveEmail(ccb,msgFrom,room)
					currentSession.userProperties.put("nameRequired", true)
				}

				if (!isLiveAdmin && ccb?.name && askName && !helpRequested &&!emailSent) {
					currentSession.userProperties.put("nameRequired", false)
					boolean doAi = boldef(config.enable_AI)
					String additional = ', please wait'
					if (doAi) {
						additional = '. Feel free to ask a question and maybe the bot can help whilst you are waiting'
					}
					chatClientListenerService.sendMessage(userSession, "Greetings ${ccb.name}! you appear to be an existing user ${additional}")
					ccb.active=true
					ccb.save()

				} else if (!isLiveAdmin && nameRequired && actionthis && askName && ccb) {
					ccb.active=true
					ccb.save()
					currentSession.userProperties.put("nameRequired", false)
					String name = actionthis
					ccb.name=name
					ccb.save()
					chatClientListenerService.sendMessage(userSession, "Thanks ${name}, just incase we get cut off what is your email?")
					currentSession.userProperties.put("emailRequired", true)
				} else 	if (!isLiveAdmin && currentSession && emailRequired && actionthis && askEmail) {
					String email = actionthis
					ccb.emailAddress=email
					if (!ccb.validate()) {
						currentSession.userProperties.put("emailRequired", true)
						chatClientListenerService.sendMessage(userSession, "Thanks ${ccb?.name?: 'Guest'}, I could not verify email ${email} can you try again?")
					} else {
						currentSession.userProperties.put("emailRequired", false)
						ccb.save()
						boolean doAi = boldef(config.enable_AI)
						String additional = ', please wait'
						if (doAi) {
							additional = '. Feel free to ask a question and maybe the bot can help whilst you are waiting'
						}
						chatClientListenerService.sendMessage(userSession, "Thanks ${ccb?.name?: 'Guest'}, I have ${email} as your email now ${additional}")
					}

				} else if (actionthis && msgFrom){
					boolean isEnabled = boldef(config.store_live_messages)
					if (isEnabled) {
						String logUser
						if (isLiveAdmin) {
							logUser = msgFrom
						}
						if (ccb) {
							persistLiveMessage(ccb, actionthis, logUser)
						}
					}
					boolean doAi = boldef(config.enable_AI)
					Map wordListing = wordListing(actionthis)
					checkAI(userSession,wordListing, doAi, actionthis)
				}
			} else if (currentSession && userType=='chat') {
				//this.disco = false
				boolean doAi = boldef(config.enable_Chat_AI)
				Map wordListing = wordListing(actionthis)
				checkAI(userSession,wordListing,doAi, actionthis)
				doAi = boldef(config.enable_Chat_BadWords)
				checkBadWords(userSession,wordListing,doAi, actionthis, msgFrom)
			} else {
				//this.disco = true
				// DISCONNECTING HERE OTHERWISE WE WILL GET A LOOP OF REPEATED MESSAGES {unsure of its accuracy now after all new changes}
				if (disco) {
					chatClientListenerService.disconnect(userSession)
				}
			}
		}
	}

	private void setBotBean(Session userSession, String msgFrom, ChatBotBean cbean=null) {
		if (!cbean) {
			cbean = new ChatBotBean()
		}
		cbean.username=msgFrom
		ChatCustomerBooking ccb = ChatCustomerBooking.findByUsername(msgFrom)
		if (ccb) {
			cbean.customer = ccb
		} else {
			ChatUser cu = ChatUser.findByUsername(msgFrom)
			cbean.chatuser = cu
			cbean.isLiveAdmin = chatUserUtilService.isLiveAdmin(msgFrom)
			cbean.isChatAdmin = chatUserUtilService.isLiveAdmin(msgFrom)
			cbean.isConfigLiveAdmin = chatUserUtilService.isConfLiveAdmin(msgFrom)
			cbean.adminVerified = true
		}
		userSession.userProperties.put('chatBotBean', cbean)
	}

	private Map wordListing(String actionthis) {
		String output
		def words = actionthis.split(" ")
		List wordList = []
		List singleWords = []
		if (words.size()>1) {
			String lastWord=''
			words.eachWithIndex { String c, int i ->
				wordList << c
				singleWords << c
				if (i>0) {
					lastWord = "${lastWord} ${c}"
					wordList << lastWord
				} else {
					lastWord = c
				}
			}
		}
		wordList<< actionthis
		return [wordList:wordList,singleWords:singleWords]
	}

	/**
	 * checks badWords db table and returns defined action for the bad words which can be kick or ban and ban defined by duration period
	 * or customise another message to be returned maybe telling off the user
	 * @param userSession
	 * @param wordListing
	 * @param doAi
	 * @param actionthis
	 * @param user
	 */
	private void checkBadWords(Session userSession, Map wordListing, boolean doAi, String actionthis, String user) {
		if (doAi) {
			List dbList = wordListing.wordList
			List singleWords = wordListing.singleWords
			if (dbList) {
				String query = """ select new map(ai.input as input, ai.output as output, ai.duration as duration, ai.period as period) FROM ChatBadWords ai
					where ai.input in (:rawList)"""
				Map inputParams = [rawList:dbList]
				def results = ChatBadWords.executeQuery(query,inputParams,[readonly:true,timeout:20,max:-1])
				if (results) {
					def finalResult = [:].withDefault { [] }
					results.each {
						def inputWords = it.input.split(" ")
						int closest = 0
						inputWords.each {
							if (singleWords.contains(it)) {
								closest++
							}
						}
						String send = "${it.output} $user"
						if (it.period && it.duration) {
							send +=",${it.duration}:${it.period}"
						}
						finalResult["${closest}"] <<  send
						//finalResult["${it.output}"] <<  closest
					}
					def mostRelated =  finalResult.max { a, b ->a.key.size() <=> b.key.size()}?.value
					mostRelated?.each {
						String send = it
						chatClientListenerService.sendMessage(userSession, send)
					}
				}
			}
		}
	}


	/**
	 * This does some wild query against ChatAI DB table and tries to match user input
	 * against what it finds and currently for anything that matches words within a sentence
	 * all those matches are returned - probably needs more work to make it more focused on input
	 * matching actual result set.
	 * @param userSession
	 * @param wordListing
	 * @param doAi
	 * @param actionthis
	 */
	private void checkAI(Session userSession,Map wordListing, boolean doAi, String actionthis) {
		if (doAi) {
			List dbList = wordListing.wordList
			List singleWords = wordListing.singleWords
			if (dbList) {
				List likeList = dbList.collect { "  or ai.input  like '%${it}%' "}
				String query = """ select new map(ai.input as input, ai.output as output) FROM ChatAI ai
					where (ai.input in (:rawList)  ${likeList.join()} )"""
				Map inputParams = [rawList:dbList]
				def results = ChatAI.executeQuery(query,inputParams,[readonly:true,timeout:20,max:-1])
				if (results) {
					def finalResult = [:].withDefault { [] }
					results.each {
						def inputWords = it.input.split(" ")
						int closest = 0
						inputWords.each {
							if (singleWords.contains(it)) {
								closest++
							}
						}
						finalResult["${closest}"] <<  it.output
						//finalResult["${it.output}"] <<  closest
					}
					def mostRelated =  finalResult.max { a, b ->a.key.size() <=> b.key.size()}?.value
					mostRelated?.each {
						chatClientListenerService.sendMessage(userSession, it)
					}
				}
			}
		}
	}

	@Transactional
	void persistLiveMessage(ChatCustomerBooking cb, String message, String user=null) {
		boolean isEnabled = boldef(config.dbstore)
		if (isEnabled) {
			def cm = new ChatMessage(user: user, contents: message, log: cb.log)
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

	private String returnRoom(String msgFrom) {
		Map<String,Session> records = chatroomUsers.get(msgFrom)
		return records.find{ it.value != null}?.key
	}

	private Session returnSession(String msgFrom) {
		Map<String,Session> records = chatroomUsers.get(msgFrom)
		String room = records.find{ it.value != null}?.key
		Session currentSession = getChatUser(msgFrom,room)
		return currentSession
	}


	/**
	 * OVERRIDE AND SET CUSTOM ACTIONS  CLIENT SERVER CHAT VIA WsChatClientService method aka <chat:clientConnect gsp call
	 * @param user
	 * @param pm
	 * @param actionthis
	 * @param sendThis
	 * @param divId
	 * @param msgFrom
	 * @param strictMode
	 * @param masterNode
	 */
	public void processAct(String user, boolean pm,String actionthis, String sendThis, String divId,
						   String msgFrom, boolean strictMode, boolean masterNode) {

		Session userSession = returnSession(user)
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
			chatClientListenerService.sendPM(userSession,msgFrom,sendThis)
		}else{
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