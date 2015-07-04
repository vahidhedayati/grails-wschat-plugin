package grails.plugin.wschat

import grails.plugin.wschat.beans.AutoCompleteBean
import grails.plugin.wschat.beans.ClientTagBean
import grails.plugin.wschat.beans.ConnectTagBean
import grails.plugin.wschat.beans.InitiationBean
import grails.plugin.wschat.beans.WsConnectTagBean
import grails.plugin.wschat.client.WsChatClientEndpoint

import javax.websocket.Session

class WsChatTagLib extends WsChatConfService {

	static namespace  =  "chat"

	def wsChatClientService
	def chatClientListenerService
	def wsChatRoomService
	def wsChatProfileService
	def pluginbuddyService
	
	
	def includeAllStyle = { attrs->
		def bean = new InitiationBean(attrs)
		Map model = [bean:bean]
		out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/includes", model: model)
	}
	
	def includeStyle = { attrs->
		def bean = new InitiationBean(attrs)
		Map model = [bean:bean]
		String template='/resourcesTop'
		if (pluginbuddyService.returnAppVersion().equals('assets')) {
			template='/assetsTop'
		}
		out << g.render(contextPath: pluginContextPath, template : template, model: model)
	}
	
	def connect  =   { attrs ->
		def bean = new ConnectTagBean(attrs)
		if (bean.profile) {
			wsChatProfileService.addProfile(bean.chatuser, bean.profile, bean.updateProfile)
		}
		if (!bean.room) {
			bean.room = wsChatRoomService.returnRoom(bean.dbSupport, true)
		}
		session.wschatroom = bean.room
		session.wschatuser = bean.chatuser
		Map model = [bean:bean]
		if (bean.template) {
			out << g.render(template:bean.template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/chat", model: model)
		}
	}


	def clientConnect  =  { attrs ->
		/*
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
			room = wsChatRoomService.returnRoom(dbSupport, true)
		}

		if (!message) {
			message = "testing"
		}

		Map model = [  message : message, room: room, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user, receivers: receivers, divId: divId,
			chatApp: CHATAPP, addAppName: addAppName ]
       */

		def bean = new ClientTagBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [clientConnect] is missing required attribute [${err.field}]")
			}
		}
		if (!bean.room) {
			bean.room = wsChatRoomService.returnRoom(bean.dbSupport, true)
		}
		
		Map model = [bean:bean]
		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(bean.hostname, bean.appName, bean.room, bean.user)
		if (bean.receivers) {
			//if (strictMode==false) {
			//	wsChatClientService.sendMessage(clientEndPoint, ">>"+message)
			//}
			wsChatClientService.sendArrayPM(clientEndPoint, bean.receivers, bean.message)
		} else {
			wsChatClientService.sendMessage(clientEndPoint, bean.message)
		}

		if (bean.autodisco) {
			wsChatClientService.disco(clientEndPoint, bean.user)
		}else{
			//Session userSess = wsChatClientService.returnSession()
			//Session userSession = clientEndPoint.returnSession()
			wsChatClientService.handMessage(clientEndPoint, bean.user, bean.receivers, bean.actionMap, bean.strictMode, bean.divId, bean.masterNode)
			if (bean.frontenduser) {
				if (bean.template) {
					out << g.render(template:bean.template, model:model)
				}else{
					out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
				}
			}
		}
	}

	def clientWsConnect  =  { attrs ->
		/*
		def room = attrs.remove('room')?.toString()
		def actionMap = attrs.remove('actionMap')
		
		def jsonData = attrs.remove('jsonData')
		String sendType = attrs.remove('sendType')?.toString() ?: 'message'
		String event =  attrs.remove('event')?.toString()
		String context = attrs.remove('context')?.toString()
		
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
			room = wsChatRoomService.returnRoom(dbSupport,true)
		}

		if (!message) {
			message = "testing"
		}
		
		String uri="ws://${hostname}/${appName}/${CHATAPP}/"
		if (addAppName=="no") {
			uri="ws://${hostname}/${CHATAPP}/"
		}
		
		*/
		WsConnectTagBean bean = new WsConnectTagBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [clientWsConnect] is missing required attribute [${err.field}]")
			}
		}
		if (!bean.room) {
			bean.room = wsChatRoomService.returnRoom(bean.dbSupport, true)
		}
		Map model = [bean:bean]
		Session oSession = chatClientListenerService.p_connect(bean.uri, bean.user, bean.room)
		/*
		Map model = [  message : message, room: room, hostname: hostname, actionMap: actionMap,
			appName: appName, frontuser:frontuser,  user: user, receivers: receivers, divId: divId,
			chatApp: CHATAPP, addAppName: addAppName ]
		*/
		try{
			//closure(session)
			if (bean.sendType == 'message') {
				if (bean.receivers) {
					chatClientListenerService.sendArrayPM(oSession, bean.receivers, bean.message)
				}else{
					chatClientListenerService.sendMessage( oSession,  bean.message)
				}
			}else if (bean.sendType == 'event') {
				chatClientListenerService.alertEvent(oSession, bean.event, bean.context, bean.jsonData, bean.receivers, bean.masterNode, bean.strictMode, bean.autodisco, bean.frontenduser)
			}

			if (bean.autodisco) {
				chatClientListenerService.disconnect(oSession)
			}else{

			}
		}catch(e){
			//log.error e
		}
		if (bean.frontenduser) {
			if (bean.template) {
				out << g.render(template:bean.template, model:model)
			}else{
				out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
			}
		}
	}

	def complete = {attrs ->
		/*
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
		
			clazz = attrs.class
		
		if (attrs.style) {
			styles = attrs.style
		}
		if (!attrs.name) {
			name = ${attrs.id}
		}else{
		name=attrs.name
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
		*/
		AutoCompleteBean bean = new AutoCompleteBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [complete] is missing required attribute [${err.field}]")
			}
		}
		Map model = [bean:bean]
		//def model = [attrs:attrs, clazz:clazz, styles:styles,name:name,required:required ]
		 
		if (bean.userTemplate) {
			out << g.render(template:bean.userTemplate, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template: bean.template, model:model)
		}
		
	}
	
}
