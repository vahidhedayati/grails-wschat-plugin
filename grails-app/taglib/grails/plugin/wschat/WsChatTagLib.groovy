package grails.plugin.wschat

import grails.converters.JSON
import grails.plugin.wschat.client.WsChatClientEndpoint

import javax.websocket.Session

class WsChatTagLib extends WsChatConfService {

	static namespace  =  "chat"

	def wsChatClientService
	def chatClientListenerService
	def wsChatRoomService
	def wsChatProfileService
	def pluginbuddyService
	
	
	def includeAllStyle = { 
		out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/includes")
	}
	
	def includeStyle = {
		 if (pluginbuddyService.returnAppVersion().equals('assets')) { 
			 out << g.render(contextPath: pluginContextPath, template : "/assets")
		 }else{
		 	out << g.render(contextPath: pluginContextPath, template : "/resources")
		 }
	}
	
	def connect  =   { attrs ->

		String chatuser = attrs.remove('chatuser')?.toString()
		String room = attrs.remove('room')?.toString()
		String template = attrs.remove('template')?.toString()
		String wschatjs = attrs.remove('wschatjs')?.toString()
		String usermenujs = attrs.remove('usermenujs')?.toString()
		
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String showtitle = config.showtitle ?: 'yes'
		String dbSupport = config.dbsupport ?: 'yes'
		String debug = config.debug ?: 'off'
		
		String addAppName = config.add.appName ?: 'yes'
		
		chatuser = chatuser.replace(' ', '_').replace('.', '_')
		
		def profile= attrs.remove('profile')
		boolean updateProfile = attrs.remove('updateProfile')?.toBoolean() ?: false
		if (profile) {
			profile=profile as Map
			wsChatProfileService.addProfile(chatuser, profile, updateProfile)
		}

		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}
		session.wschatroom = room
		session.wschatuser = chatuser
		
		def model = [ dbsupport: dbSupport.toLowerCase() , showtitle: showtitle.toLowerCase(),
			room: room, chatuser: chatuser, chatTitle: chatTitle, chatHeader: chatHeader,
			now: new Date(), hostname: hostname, addAppName: addAppName, debug:debug, 
			wschatjs:wschatjs,usermenujs:usermenujs ]

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
			appName = applicationName
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
			chatApp: CHATAPP, addAppName: addAppName ]

		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(hostname, appName, room, user)
		if (receivers) {
			//if (strictMode==false) {
			//	wsChatClientService.sendMessage(clientEndPoint, ">>"+message)
			//}
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
		String addAppName = config.add.appName ?: 'yes'
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
			appName = applicationName
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
		if (addAppName=="no") {
			uri="ws://${hostname}/${CHATAPP}/"
		}
		
		
		Session oSession = chatClientListenerService.p_connect(uri, user, room)
		Map model = [  message : message, room: room, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user, receivers: receivers, divId: divId,
			chatApp: CHATAPP, addAppName: addAppName ]
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

	def complete = {attrs ->
		def clazz,name,cid,styles = ""
		if (attrs.id == null) {
			throwTagError("Tag [autoComplete] is missing required attribute [id]")
		}
		
		if ( (!attrs.controller) && (!attrs.action) ) {
			if (attrs.domain == null) {
				throwTagError("Tag [autoComplete] is missing required attribute [domain]")
			}
			if (attrs.searchField == null) {
				throwTagError("Tag [autoComplete] is missing required attribute [searchField]")
			}
		}
		
		if (!attrs.controller) {
			attrs.controller= "wsChat"
		}
		if (!attrs.action) {
			attrs.action= "autocomplete"
		}
		
		if (!attrs.max) {
			attrs.max = 10
		}
		if (!attrs.value) {
			attrs.value =""
		}
		if (!attrs.order) {
			attrs.order = "asc"
		}
		if (!attrs.collectField) {
			attrs.collectField = attrs.searchField
		}
		if (attrs.class) {
			clazz = " class='${attrs.class}'"
		}
		if (attrs.style) {
			styles = " styles='${attrs.style}'"
		}
		if (attrs.name) {
			name = " name ='${attrs.name}'"
		} else {
			name = " name ='${attrs.id}'"
		}
	
		def  required=""
		Boolean requireField=true
		if (attrs.require) {
			requireField=attrs.remove('require')?.toBoolean()
		}
		
		if (attrs.required) {
			requireField=attrs.remove('required')?.toBoolean()
		}
		if (requireField) {
			 required=" required='required' "
		}

		def template='/autoComplete/AutoCompleteBasic'
		
		def userTemplate=attrs.remove('userTemplate') ?: config.autocomplete
		
		def model = [attrs:attrs, clazz:clazz, styles:styles,name:name,required:required ]
		 
		if (userTemplate) {
			out << g.render(template:userTemplate, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template: template, model:model)
		}
		
	}
	
}
