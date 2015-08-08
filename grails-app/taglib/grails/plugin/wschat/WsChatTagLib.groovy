package grails.plugin.wschat

import grails.plugin.wschat.beans.AutoCompleteBean
import grails.plugin.wschat.beans.ClientTagBean
import grails.plugin.wschat.beans.ConnectTagBean
import grails.plugin.wschat.beans.InitiationBean
import grails.plugin.wschat.beans.WsConnectTagBean
import grails.plugin.wschat.client.WsChatClientEndpoint
import grails.plugin.wschat.beans.CustomerChatTagBean

import javax.websocket.Session

class WsChatTagLib extends WsChatConfService {

	static namespace  =  "chat"

	def wsChatClientService
	def chatClientListenerService
	def wsChatRoomService
	def wsChatProfileService
	def randomService
	def wsChatBookingService

	def includeAllStyle = { attrs->
		def bean = new InitiationBean(attrs)
		Map model = [bean:bean]
		out << g.render(contextPath: pluginContextPath, template : "/${CHATVIEW}/includes", model: model)
	}

	def includeStyle = { attrs->
		def bean = new InitiationBean(attrs)
		Map model = [bean:bean]
		String template = '/assetsTop'
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
			bean.setRoom(wsChatRoomService.returnRoom(true))
		}
		// This method does not appear to work under grails 3
		String uri = "${bean.uri}${bean.room}"
		WsChatClientEndpoint clientEndPoint = wsChatClientService.conn(uri, bean.user)
		if (bean.receivers) {
			wsChatClientService.sendArrayPM(clientEndPoint, bean.receivers, bean.message)
		} else {
			wsChatClientService.sendMessage(clientEndPoint, bean.message)
		}

		if (bean.autodisco) {
			wsChatClientService.disco(clientEndPoint, bean.user)
		}else{
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

	def customerChatButton= { attrs->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		Map model = [bean:bean]
		out << g.render(contextPath: pluginContextPath,template: '/customerChat/chatButton', model:model)
	}

	def customerChat = { attrs ->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		if (!bean.roomName) {
			bean.roomName = randomService.shortRand('livechat')
			//bean.roomName = 'fred'
		}
		// if a username has not been provided so far -
		// set the username to be Guest{SessionID}
		// this now means if the user is using same session and is on another page
		// chat system will recognise them
		if (!bean.user) {
			bean.guestUser = true
			bean.user = 'Guest'+session.id
		} else {
			bean.guestUser = false
		}
		String uri = "${bean.uri}${bean.roomName}"

		wsChatBookingService.saveCustomerBooking(bean)
		Map model = [bean:bean, uri:uri]
		if (bean.template) {
			out << g.render(template:bean.template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:"/customerChat/chatPage", model: model)
		}
		bean.message="Welcome, this is an automated message, attempting to retrieve a member of staff for you. Please wait"
		Session oSession = chatClientListenerService.p_connect(bean.uri, bean.user+"_"+bean.assistant, bean.roomName)
		chatClientListenerService.sendDelayedMessage(oSession, bean.message,1000)
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
