package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService

import javax.websocket.ContainerProvider
import javax.websocket.Session


public class ChatClientListenerService extends WsChatConfService {
	
	static transactional  =  false

	def wsChatRoomService
	def wsChatUserService
	def chatClientOverrideService

	def sendArrayPM(Session userSession, ArrayList user,String message) {
		user.each { cuser ->
			boolean found
			found=wsChatUserService.findUser(cuser)
			if (found) {
				userSession.basicRemote.sendText("/pm ${cuser},${message}")
			}
			if (!cuser.toString().endsWith(frontend)) {
				found=wsChatUserService.findUser(cuser+frontend)
				if (found) {
					userSession.basicRemote.sendText("/pm ${cuser+frontend},${message}")
				}
			}
		}
	}
	
	
	def sendPM(Session userSession, String user,String message) {
		String username = userSession.userProperties.get("username") as String
		boolean found

		found=wsChatUserService.findUser(user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
		if (!user.endsWith(frontend)) {
			found=wsChatUserService.findUser(user+frontend)
			if (found) {
				userSession.basicRemote.sendText("/pm ${user+frontend},${message}")
			}
		}
	}

	public void sendMessage(Session userSession,final String message) {
		userSession.basicRemote.sendText(message)
	}

	public connectUserRoom  = {  String user, String room,  Closure closure ->

		String wshostname = config.hostname ?: 'localhost:8080'
		String uri="ws://${wshostname}/${applicationName}${CHATAPP}/"


		Session oSession = p_connect( uri, user, room)

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

		String wshostname = config.hostname ?: 'localhost:8080'
		String uri="ws://${wshostname}/${applicationName}${CHATAPP}/"

		String oUsername = config.app.id ?: "[${(Math.random()*1000).intValue()}]-$room";
		Session oSession = p_connect( uri, oUsername, room)

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
		String dbSupport = config.dbsupport ?: 'yes'
		String wshostname = config.hostname ?: 'localhost:8080'
		String uri = "ws://${wshostname}/${applicationName}${CHATAPP}/"
		def room = wsChatRoomService.returnRoom(dbSupport as String)
		String oUsername = config.app.id ?: "[${(Math.random()*1000).intValue()}]-$room";
		Session csession = p_connect( uri, oUsername, room)
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
				sendMessage(_oSession, DISCONNECTOR)
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
