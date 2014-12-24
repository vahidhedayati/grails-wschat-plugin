package grails.plugin.wschat

import grails.converters.JSON
import grails.plugin.wschat.client.WsChatClientEndpoint
import grails.plugin.wschat.interfaces.ClientSessions

import javax.websocket.Session

class WsChatTagLib implements ClientSessions {

	static namespace  =  "chat"

	def grailsApplication

	def wsChatClientService
	def chatClientListenerService
	def wsChatRoomService
	
	def connect  =   { attrs ->

		String chatuser = attrs.remove('chatuser')?.toString()
		String room = attrs.remove('room')?.toString()
		String template = attrs.remove('template')?.toString()

		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String showtitle = config.showtitle ?: 'yes'
		String dbSupport = config.dbsupport ?: 'yes'

		chatuser = chatuser.trim().replace(' ', '_').replace('.', '_')
		session.wschatuser = chatuser

		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}

		session.wschatroom = room

		def model = [dbsupport:dbSupport.toLowerCase() ,
			showtitle:showtitle.toLowerCase(),
			room:room, chatuser:chatuser, chatTitle:chatTitle,
			chatHeader:chatHeader, now:new Date(),
			hostname:hostname]

		if (template) {

			out << g.render(template:template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/chat", model: model)
		}
	}


	def clientConnect  =  { attrs ->
		def room = attrs.remove('room')?.toString()
		def actionMap = attrs.remove('actionMap')
		boolean strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		boolean autodisco = attrs.remove('autodisco')?.toBoolean() ?: false
		boolean masterNode = attrs.remove('masterNode')?.toBoolean() ?: false
		String hostname = attrs.remove('hostname')?.toString()
		String appName = attrs.remove('appName')?.toString()
		String user = attrs.remove('user')?.toString()
		String message = attrs.remove('message')?.toString()
		String receiver = attrs.remove('receiver')?.toString() ?: ''
		String divId = attrs.remove('divId')?.toString() ?: ''
		Map aMap = [:]
		if (actionMap) {
			aMap = actionMap as Map
		}
		String dbSupport = config.dbsupport ?: 'yes'

		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}

		if (!hostname) {
			hostname = config.hostname ?: 'localhost:8080'
		}

		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}

		if (!message) {
			message = "testing"
		}
		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(hostname, appName, room, user)
		if (receiver) {
			if (strictMode==false) {
				wsChatClientService.sendMessage(clientEndPoint, ">>"+message)
			}
			wsChatClientService.sendPM(clientEndPoint, receiver, message)
		} else {
			wsChatClientService.sendMessage(clientEndPoint, message)
		}

		if (autodisco) {
			wsChatClientService.disco(clientEndPoint, user)
		}else{
			//Session userSess = wsChatClientService.returnSession()
			Session userSession = clientEndPoint.returnSession()
			wsChatClientService.handMessage(userSession, clientEndPoint, user, receiver, aMap, strictMode, divId, masterNode)
		}

	}

	def clientWsConnect  =  { attrs ->
		def room = attrs.remove('room')?.toString()
		def actionMap = attrs.remove('actionMap')
		boolean strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		boolean autodisco = attrs.remove('autodisco')?.toBoolean() ?: false
		boolean masterNode = attrs.remove('masterNode')?.toBoolean() ?: false
		String hostname = attrs.remove('hostname')?.toString()
		String appName = attrs.remove('appName')?.toString()
		String user = attrs.remove('user')?.toString()
		String message = attrs.remove('message')?.toString()
		String receiver = attrs.remove('receiver')?.toString() ?: ''
		String divId = attrs.remove('divId')?.toString() ?: ''
		String template = attrs.remove('template')?.toString()
		String sendType = attrs.remove('sendType')?.toString() ?: 'message'
		String event =  attrs.remove('event')?.toString() 
		String context = attrs.remove('context')?.toString() 
		def jsonData = attrs.remove('jsonData')
		def aUsers = attrs.remove('aUsers')
		
		if (aUsers) {
			aUsers = aUsers as ArrayList
		}
		
		/*if (jsonData) {
			jsonData = jsonData as JSON
		}*/
		
		if (actionMap) {
			actionMap = actionMap as Map
		}
		String dbSupport = config.dbsupport ?: 'yes'

		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}

		if (!hostname) {
			hostname = config.hostname ?: 'localhost:8080'
		}

		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}

		if (!message) {
			message = "testing"
		}

		String uri="ws://${hostname}/${appName}/${CHATAPP}/"
		
		Session oSession = chatClientListenerService.p_connect(uri, user, room)
		
		try{
			//oSession.basicRemote.sendText(message)
			//closure(session)
			if (sendType == 'message') {
				if (receiver) {
					chatClientListenerService.sendPM(oSession, receiver, message)
				}else{
					chatClientListenerService.sendMessage( oSession,  message)
				}	
			}else if (sendType == 'event') {
				chatClientListenerService.alertEvent(oSession, event, context, jsonData, aUsers)
			}
			
			if (autodisco) {
				chatClientListenerService.disconnect(oSession)
			}
			//if (template) {
			//	out << g.render(template:template, model:model)
			//}else{
			//	out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
			//}
		}catch(e){
			log.error e
		}
	
		}
	
	

	private getConfig() {
		grailsApplication?.config?.wschat
	}

}
