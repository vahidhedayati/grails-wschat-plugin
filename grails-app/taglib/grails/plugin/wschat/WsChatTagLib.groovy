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
	def wsChatAuthService

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
		wsChatAuthService.addBotToChatRoom(bean.room, 'chat', bean.enable_Chat_Bot, bean.botMessage, bean.uri)
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

	/**
	 * customerChatButton is an insecure button generated
	 * for easy access/use to the next taglib call customerChat
	 *
	 * <chat:customerChatButton user="${session.user}"/>
	 * or <chat:customerChatButton />
	 */
	def customerChatButton= { attrs->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		Map model = [bean:bean]
		out << g.render(contextPath: pluginContextPath,template: '/customerChat/chatButton', model:model)
	}

	/**
	 * Original liveChat method
	 * interacts with end-user with what is required is 1 room per live chat
	 * you should ensure each customerChat request hits a new room,
	 * leave roomName blank to ensure it is randomly generated
	 *
	 * Will provide a room bot to verify end user and grab name/email
	 * Will email/notify admin group of a new livechat request
	 * Bot will also try to help with words matching patterns in ChatAI
	 *
	 *
	 * <chat:customerChat />
	 * <chat:customerChat user="${params.user}" />
	 * <chat:customerChat user="${params.user}" roomName="${params.room }" />
	 * <chat:customerChat user="${params.user}" roomName="${params.room }" name="Users actualName"/>
	 * <chat:customerChat user="${params.user}" roomName="${params.room }" name="Users actualName"  emailAddress="users@email.com" />
	 *
	 */
	def customerChat = { attrs ->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		if (!bean.roomName) {
			bean.roomName = randomService.shortRand(controllerName+actionName)
			//bean.roomName = 'fred'
		}
		// if a username has not been provided so far -
		// set the username to be Guest{SessionID}
		// this now means if the user is using same session and is on another page
		// chat system will recognise them
		bean.guestUser = false
		if (!bean.user) {
			bean.guestUser = true
			bean.user = 'Guest' + session.id
		}
		String uri = "${bean.uri}${bean.roomName}"

		// This will save client livechat Request in CustomerBooking
		wsChatBookingService.saveCustomerBooking(bean)

		/*
		 * Renders /customerChat/_chatPage.gsp
		 */
		Map model = [bean:bean, uri:uri]
		if (bean.template) {
			out << g.render(template:bean.template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:"/customerChat/chatPage", model: model)
		}

		/*
		 * This does the client side connection, it has a twist
		 * tries to figure out if it should ask the user for their name (configurable by you)
		 * and if its an existing user that has used the system before.
		 * Variation of messages generated and sent to trigger bot conversation with end user
		 *
		 * It has a lot more turns and twists, it also triggers emails to the admin group look for
		 * wsChatBookingService.sendLiveEmail in WsClientProcessingService.groovy to understand more
		 */
		wsChatAuthService.addBotToChatRoom(bean.roomName, 'liveChat', true, bean.botLiveMessage, bean.uri, bean.user)
	}

	/**
	 * new liveChat method
	 * user input disabled until staff/admin joins their live chat
	 * user livechat rooms shared. So one admin can interact with many users in that room
	 * Users oblivious / unaware of this
	 * no chat bot in this model
	 * admin is notified of a new live chat request
	 * Where all users can be routed to one or a few rooms - easier to monitor
	 *
	 * No buttons - call this directly when you have enough information look at video 10 and example site
	 * <chat:liveChat />
	 * <chat:liveChat user="${params.user}" />
	 * <chat:liveChat user="${params.user}" roomName="${params.room }" />
	 * <chat:liveChat user="${params.user}" roomName="${params.room }" name="Users actualName"/>
	 * <chat:liveChat user="${params.user}" roomName="${params.room }" name="Users actualName"  emailAddress="users@email.com" />
	 */
	def liveChat = { attrs ->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		if (!bean.roomName) {
			bean.roomName = randomService.shortRand(controllerName+actionName)
		}
		bean.guestUser = false
		if (!bean.user) {
			bean.guestUser = true
			bean.user = 'Guest' + session.id
		}
		String uri = "${bean.uri}${bean.roomName}"

		ChatCustomerBooking ccb = wsChatBookingService.saveCustomerBooking(bean)

		/*
		 * Renders /customerChat/_liveChatPage.gsp
		 */
		Map model = [bean:bean, uri:uri]
		if (bean.template) {
			out << g.render(template:bean.template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:"/customerChat/liveChatPage", model: model)
		}

		/*
		 * Triggers an email to admin group
		 * to notify someone needs help
		 */
		wsChatBookingService.sendLiveEmail(ccb,bean.user,bean.roomName)
	}

	/**
	 *  monitoring call that allows you to
	 *  monitor live chat requests from any part of your existing site
	 *  whilst you can use the admin cog to monitor live chat requests
	 *  this is an attempt to take away repetitve clicks / extra work
	 *  and just present it to you in a nice easy way to be able to provide
	 *  best possible response for your end users.
	 */
	def monitorliveChat = { attrs ->
		attrs << [controller:controllerName, action: actionName, params: params ]
		CustomerChatTagBean bean = new CustomerChatTagBean(attrs)
		if (!bean.roomName) {
			bean.roomName = "adminRoom"
		}
		if (!bean.user) {
			bean.user = 'Guest'+session.id
		}
		String uri = "${bean.uri}${bean.roomName}"
		Map model = [bean:bean, uri:uri]
		if (bean.template) {
			out << g.render(template:bean.template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template:"/customerChat/monitorLiveChat", model: model)
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
