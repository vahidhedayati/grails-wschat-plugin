package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.beans.ConfigBean

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ChatClientListenerService extends WsChatConfService {
	
	static transactional  =  false
	
	def grailsApplication
	def wsChatRoomService
	def wsChatUserService
	def chatClientOverrideService

	def sendArrayPM(Session userSession, ArrayList user,String message) {
		ConfigBean bean = new ConfigBean()
		user.each { cuser ->
			boolean found
			found=wsChatUserService.findUser(cuser)
			if (found) {
				userSession.basicRemote.sendText("/pm ${cuser},${message}")
			}
			if (!cuser.toString().endsWith(bean.frontUser)) {
				found=wsChatUserService.findUser(cuser+bean.frontUser)
				if (found) {
					userSession.basicRemote.sendText("/pm ${cuser+bean.frontUser},${message}")
				}
			}
		}
	}
	
	
	def sendPM(Session userSession, String user,String message) {
		String username = userSession.userProperties.get("username") as String
		boolean found
		ConfigBean bean = new ConfigBean()
		found=wsChatUserService.findUser(user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
		if (user && !user.endsWith(bean.frontUser)) {
			found=wsChatUserService.findUser(user+bean.frontUser)
			if (found) {
				userSession.basicRemote.sendText("/pm ${user+bean.frontUser},${message}")
			}
		}
	}

	public void sendMessage(Session userSession,final String message) {
		userSession.basicRemote.sendText(message)
	}

	public connectUserRoom  = {  String user, String room,  Closure closure ->

		//String wshostname = config.hostname ?: 'localhost:8080'
		//String uri="ws://${wshostname}/${applicationName}${CHATAPP}/"
		ConfigBean bean = new ConfigBean()

		Session oSession = p_connect( bean.uri, user, room)

		try{
			closure(oSession)
		}catch(e){
			throw e
		}
		finally {
			disconnect(oSession)
		}
	}

	public connectRoom  = { String room,  Closure closure ->

		//String wshostname = config.hostname ?: 'localhost:8080'
		//String uri="ws://${wshostname}/${applicationName}${CHATAPP}/"
		ConfigBean bean = new ConfigBean()
		
		String oUsername = config.app.id ?: "[${(Math.random()*1000).intValue()}]-$room";
		Session oSession = p_connect( bean.uri, oUsername, room)

		try{
			closure(oSession)
		}catch(e){
			throw e
		}
		finally {
			disconnect(oSession)
		}
	}

	Session connect() {
		ConfigBean bean = new ConfigBean()
		def room = wsChatRoomService.returnRoom(bean.dbSupport, true)
		String oUsername = config.app.id ?: "[${(Math.random()*1000).intValue()}]-$room";
		Session csession = p_connect( bean.uri, oUsername, room)
		return csession
	}

	Session p_connect(String _uri, String _username, String _room){
		//WsChatClientEndpoint wsChatClientEndpoint=new WsChatClientEndpoint()
		String oRoom = _room ?: config.room
		URI oUri
		if(_uri){
			oUri = URI.create(_uri+oRoom);
		}
		def container = ContainerProvider.getWebSocketContainer()
		Session oSession
		try{
			oSession = container.connectToServer(ChatClientEndpoint.class, oUri)
			oSession.basicRemote.sendText(CONNECTOR+_username)
		}catch(Exception e){
			e.printStackTrace()
			if(oSession && oSession.isOpen()){
				oSession.close()
			}
			return null
		}
		oSession.userProperties.put("username", _username)
		return  oSession
	}


	public Session disconnect(Session _oSession){
		try{
			if(_oSession && _oSession.isOpen()){
				String user = _oSession.userProperties.get("username") as String
				if (user) {
					ConfigBean bean = new ConfigBean()
					sendMessage(_oSession, DISCONNECTOR)
					Session nsess
					if (user.endsWith(bean.frontUser)) {
						nsess =wsChatUserService.usersSession(user.substring(0,user.indexOf(bean.frontUser)))
					}else{
						nsess =wsChatUserService.usersSession(user+bean.frontUser)
					}
					if (nsess) {
						sendMessage(nsess, DISCONNECTOR)
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return _oSession
	}

	public void alertEvent(def _oSession,  String _event, String _context, JSON _data,
			ArrayList cusers, boolean masterNode, boolean strictMode, boolean autodisco,
			boolean frontenduser){

		def oSession = _oSession ?: connect()
		String sMessage = """{
                        "command":"event",
                        "arguments":[
                                        {
                                        "event":"$_event",
                                        "context":"$_context",
										"data":[${_data as String}]
                                        }
                                    ]
                        }
                    """
		cusers.each { userId ->
			sendPM(oSession,
					chatClientOverrideService.getGlobalReceiverNameFromUserId(userId as String),
					sMessage.replaceAll("\t","").replaceAll("\n",""))
		}
		if (_oSession == null) {
			disconnect(oSession)
		}
	}

}
