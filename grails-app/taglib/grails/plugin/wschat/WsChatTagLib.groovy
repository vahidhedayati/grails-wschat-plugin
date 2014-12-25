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

		String addAppName = config.add.appName ?: 'yes'
		def appName=''
		if ((!appName)&& (addAppName=='yes')){
			appName = grailsApplication.metadata['app.name']+"/"
		}
		
		chatuser = chatuser.trim().replace(' ', '_').replace('.', '_')
		session.wschatuser = chatuser

		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}

		session.wschatroom = room

		def model = [ dbsupport: dbSupport.toLowerCase() , showtitle: showtitle.toLowerCase(),
			room: room, chatuser: chatuser, chatTitle: chatTitle, chatHeader: chatHeader,appName:appName,
			now: new Date(), hostname: hostname ]

		if (template) {
			out << g.render(template:template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/chat", model: model)
		}
	}


	def clientConnect  =  { attrs ->
		def room = attrs.remove('room')?.toString()
		def actionMap = attrs.remove('actionMap')
		def receivers = attrs.remove('receivers')
		boolean strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		boolean autodisco = attrs.remove('autodisco')?.toBoolean() ?: false
		boolean masterNode = attrs.remove('masterNode')?.toBoolean() ?: false
		boolean frontenduser = attrs.remove('frontenduser')?.toBoolean() ?: false
		String hostname = attrs.remove('hostname')?.toString()
		String appName = attrs.remove('appName')?.toString()
		String user = attrs.remove('user')?.toString()
		String message = attrs.remove('message')?.toString()
		String divId = attrs.remove('divId')?.toString() ?: ''
		String template = attrs.remove('template')?.toString()

		if (receivers) {
			receivers = receivers as ArrayList
		}

		String frontuser=''
		if (frontenduser) {
			frontuser=user+frontend
			receivers.add(frontuser)
		}

		if (actionMap) {
			actionMap = actionMap as Map
		}

		String dbSupport = config.dbsupport ?: 'yes'
		String addAppName = config.add.appName ?: 'yes'
		
		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}
		
		if (addAppName=='no') {
			appName=''
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

		Map model = [  message : message, room: room, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user, receivers: receivers, divId: divId,
			chatApp: CHATAPP ]

		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(hostname, appName, room, user)
		if (receivers) {
			if (strictMode==false) {
				wsChatClientService.sendMessage(clientEndPoint, ">>"+message)
			}
			wsChatClientService.sendArrayPM(clientEndPoint, receivers, message)
		} else {
			wsChatClientService.sendMessage(clientEndPoint, message)
		}

		if (autodisco) {
			wsChatClientService.disco(clientEndPoint, user)
		}else{
			//Session userSess = wsChatClientService.returnSession()
			//Session userSession = clientEndPoint.returnSession()
			wsChatClientService.handMessage(clientEndPoint, user, receivers, actionMap, strictMode, divId, masterNode)
			if (frontenduser) {
				if (template) {
					out << g.render(template:template, model:model)
				}else{
					out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
				}
			}
		}
	}

	def clientWsConnect  =  { attrs ->
		def room = attrs.remove('room')?.toString()
		def actionMap = attrs.remove('actionMap')
		def jsonData = attrs.remove('jsonData')
		def receivers = attrs.remove('receivers')
		boolean strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		boolean autodisco = attrs.remove('autodisco')?.toBoolean() ?: false
		boolean masterNode = attrs.remove('masterNode')?.toBoolean() ?: false
		boolean frontenduser = attrs.remove('frontenduser')?.toBoolean() ?: false
		String hostname = attrs.remove('hostname')?.toString()
		String appName = attrs.remove('appName')?.toString()
		String user = attrs.remove('user')?.toString()
		String message = attrs.remove('message')?.toString()
		String divId = attrs.remove('divId')?.toString() ?: ''
		String template = attrs.remove('template')?.toString()
		String sendType = attrs.remove('sendType')?.toString() ?: 'message'
		String event =  attrs.remove('event')?.toString()
		String context = attrs.remove('context')?.toString()

		if (receivers) {
			receivers = receivers as ArrayList
		}
		if (jsonData) {
			if(jsonData instanceof String) {
				jsonData =JSON.parse(jsonData)
			}
			jsonData = jsonData as JSON
		}

		String frontuser=''
		if (frontenduser) {
			frontuser=user+frontend
			receivers.add(frontuser)
		}

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
		Map model = [  message : message, room: room, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user, receivers: receivers, divId: divId,
			chatApp: CHATAPP ]
		try{
			//closure(session)
			if (sendType == 'message') {
				if (receivers) {
					chatClientListenerService.sendArrayPM(oSession, receivers, message)
				}else{
					chatClientListenerService.sendMessage( oSession,  message)
				}
			}else if (sendType == 'event') {
				chatClientListenerService.alertEvent(oSession, event, context, jsonData, receivers, masterNode, strictMode, autodisco, frontenduser)
			}

			if (autodisco) {
				chatClientListenerService.disconnect(oSession)
			}else{

			}
		}catch(e){
			//log.error e
		}
		if (frontenduser) {
			if (template) {
				out << g.render(template:template, model:model)
			}else{
				out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
			}
		}
	}

	private String getFrontend() {
		def cuser=config.frontenduser ?: '_frontend'
		return cuser
	}

	private getConfig() {
		grailsApplication?.config?.wschat
	}

}
