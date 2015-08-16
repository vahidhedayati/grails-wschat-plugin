package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.beans.ConfigBean

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class WsChatClientService extends WsChatConfService {

	static transactional  =  false

	def grailsApplication
	def wsChatUserService

	WsChatClientEndpoint conn(String uri, String user ) {
		WsChatClientEndpoint clientEndPoint = new WsChatClientEndpoint(new URI(uri))
		clientEndPoint.connectClient(user)
		return clientEndPoint
	}

	void sendMessage(WsChatClientEndpoint clientEndPoint, String message) {
		clientEndPoint.sendMessage("${message}")
	}

	void sendArrayPM(WsChatClientEndpoint clientEndPoint, ArrayList pmuser, String message) {
		ConfigBean bean = new ConfigBean()
		pmuser.each { cuser ->
			boolean found
			if (cuser && !cuser.toString().endsWith(bean.frontUser)) {
				found=wsChatUserService.findUser(cuser+bean.frontUser)
				if (found) {
					clientEndPoint.sendMessage("/pm ${cuser+bean.frontUser},${message}")
				}
			}
			found=wsChatUserService.findUser(cuser)
			if (found) {
				clientEndPoint.sendMessage("/pm ${cuser},${message}")
			}

		}
	}
	
	void sendPM(WsChatClientEndpoint clientEndPoint, String pmuser, String message) {
		boolean found
		ConfigBean bean = new ConfigBean()
		if (!pmuser.endsWith(bean.frontUser)) {
			found=wsChatUserService.findUser(pmuser+bean.frontUser)
			if (found) {
				clientEndPoint.sendMessage("/pm ${pmuser+bean.frontUser},${message}")
			}
		}
		found=wsChatUserService.findUser(pmuser)
		if (found) {
			clientEndPoint.sendMessage("/pm ${pmuser},${message}")
		}
	}

	void disco(WsChatClientEndpoint clientEndPoint, String user) {
		clientEndPoint.disconnectClient(user)
	}


	void handMessage(WsChatClientEndpoint clientEndPoint, String user,
			ArrayList pmuser, Map aMap, boolean strictMode,String divId, boolean masterNode) {

		clientEndPoint.addMessageHandler(
				new WsChatClientEndpoint.MessageHandler() {
					public void handleMessage(String message) {
						if (message.startsWith('{"')) {
							JSONObject rmesg=JSON?.parse(message)
							String actionthis=''
							String msgFrom = rmesg.msgFrom
							String disconnect = rmesg.system
							if (disconnect && disconnect == "disconnect") {
								clientEndPoint.sendMessage("DISCO:-"+user)
							}
							boolean pm = false
							if (strictMode) {
								pmuser?.each { cuser ->
									if (msgFrom && msgFrom == cuser) {
										actionthis = rmesg.privateMessage
										pm = true
									}
								}
							}else{
								if (msgFrom ) {
									actionthis = rmesg.privateMessage
									pm = true
								}
							}
							def rmessage = rmesg.message
							if (rmessage) {
								def matcher = (rmessage =~ /(.*): (.*)/)
								if (matcher.matches()){
									msgFrom = matcher[0][1]
									if (strictMode) {
										pmuser?.each { cuser ->
											if (msgFrom && msgFrom == cuser) {
												actionthis = matcher[0][2]
											}
										}
									}else{
										if (msgFrom) {
											actionthis = matcher[0][2]
										}
									}
								}
							}

							if (actionthis) {
								if ( (actionthis == 'close_connection')
								|| (actionthis.startsWith('DISCO:-')) ) {
									clientEndPoint.sendMessage("DISCO:-"+user)
								}else{
									if (aMap.containsKey(actionthis)) {
										String sendThis=aMap[actionthis]
										clientEndPoint.processAction( user, pm, actionthis, sendThis, divId ?: '',msgFrom,strictMode,masterNode)

									}
								}
							}

						}else{
							clientEndPoint.sendMessage("${message}")
						}
					}
				})
	}

}
