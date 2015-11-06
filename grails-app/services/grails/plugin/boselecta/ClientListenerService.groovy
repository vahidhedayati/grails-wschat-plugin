package grails.plugin.boselecta


import grails.plugin.boselecta.beans.ConnectionBean
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.ContainerProvider
import javax.websocket.Session

public class ClientListenerService extends ConfService implements ClientSessions {

	static transactional  =  false
	def randService

	def sendArrayPM(Session userSession, String job,String message) {
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				boolean found = false
				if (job==cjob) {
					found=findUser(cuser)
					if (found) {
						crec.basicRemote.sendText("/pm ${cuser},${message}")
					}
					if (!cuser.toString().endsWith(frontend)) {
						found=findUser(cuser+frontend)
						if (found) {
							crec.basicRemote.sendText("/pm ${cuser+frontend},${message}")
						}
					}
				}
			}
		}
	}

	def sendJobMessage(String job,String message) {
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				if (job==cjob) {
					crec.basicRemote.sendText("${message}")
				}
			}
		}
	}

	def sendFrontEndPM(Session userSession, String user,String message) {
		def found=findUser(user+frontend)
		// Fixed - private messaging from backend to front-end
		// messages were getting sent to all before.
		userSession.basicRemote.sendText("/pm ${user+frontend},${message}")
	}

	// Added backend PM - new connection info was being relayed to all before.
	def sendBackPM(String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				String cjob =  crec.userProperties.get("job") as String
				if (user==cuser) {
					crec.basicRemote.sendText("${message}")
				}
			}
		}
	}

	def sendBackEndPM(Session userSession, String user,String message) {
		if (user.endsWith(frontend)) {
			user=user.substring(0,user.indexOf(frontend))
		}
		userSession.basicRemote.sendText("/pm ${user},${message}")
	}

	def sendPM(Session userSession, String user,String message) {
		String username = userSession.userProperties.get("username") as String
		boolean found
		found=findUser(user)
		if (found) {
			userSession.basicRemote.sendText("/pm ${user},${message}")
		}
		if (!user.endsWith(frontend)) {
			found=findUser(user+frontend)
			if (found) {
				sendFrontEndPM(userSession,user, message )
			}
		}
	}

	boolean findUser(String username) {
		boolean found = false
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (cuser.equals(username)) {
					found = true
				}
			}
		}
		return found
	}


	public void sendMessage(Session userSession,final String message) {
		userSession.basicRemote.sendText(message)
	}

	Session p_connect(ConnectionBean cBean){
		String uri = cBean.uri
		String job = cBean.job ?: randService.shortRand('noJob')
		String username = cBean.user ?: randService.shortRand('noUser')
		URI oUri
		if(uri){
			oUri = URI.create(uri+job);
		}
		
		
		def container = ContainerProvider.getWebSocketContainer()
		Session sess
		try{
			sess = container.connectToServer(BoSelectaClientEndpoint.class, oUri)
			sess.basicRemote.sendText(CONNECTOR+username)
		}catch(Exception e){
			e.printStackTrace()
			if(sess && sess.isOpen()){
				sess.close()
			}
			return null
		}
		sess.userProperties.put("username", username)
		return  sess
	}


	public Session disconnect(Session sess){
		try{
			if(sess && sess.isOpen()){
				sendMessage(sess, DISCONNECTOR)
			}
		}catch (Exception e){
			e.printStackTrace()
		}
		return sess
	}

}
