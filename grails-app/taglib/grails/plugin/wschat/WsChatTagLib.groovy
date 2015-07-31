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
			bean.room = wsChatRoomService.returnRoom(true)
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
		def bean = new ClientTagBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [clientConnect] is missing required attribute [${err.field}]")
			}
		}
		if (!bean.room) {
			bean.room = wsChatRoomService.returnRoom(true)
		}
		

		String uri = "${bean.uri}${bean.room}"
		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(uri, bean.user)
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
			Map model = [bean:bean, uri:uri]
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
		WsConnectTagBean bean = new WsConnectTagBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [clientWsConnect] is missing required attribute [${err.field}]")
			}
		}
		if (!bean.room) {
			bean.setRoom(wsChatRoomService.returnRoom(true))
		}
		Session oSession = chatClientListenerService.p_connect(bean.uri, bean.user, bean.room)
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
		String uri = "${bean.uri}${bean.room}"
		Map model = [bean:bean, uri:uri]
		if (bean.frontenduser) {
			if (bean.template) {
				out << g.render(template:bean.template, model:model)
			}else{
				out << g.render(contextPath: pluginContextPath, template:"/${CHATVIEW}/process", model: model)
			}
		}
	}

	def complete = {attrs ->
		AutoCompleteBean bean = new AutoCompleteBean(attrs)
		if (!bean.validate()) {
			bean.errors.allErrors.each {err ->
				throwTagError("Tag [complete] is missing required attribute [${err.field}]")
			}
		}
		Map model = [bean:bean]
		if (bean.userTemplate) {
			out << g.render(template:bean.userTemplate, model: model)
		}else{
			out << g.render(contextPath: pluginContextPath, template: bean.template, model:model)
		}
	}
}
