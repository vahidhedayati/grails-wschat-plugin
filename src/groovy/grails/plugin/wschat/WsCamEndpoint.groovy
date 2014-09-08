package grails.plugin.wschat


import grails.util.Holders

import java.io.IOException;
import java.nio.ByteBuffer

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.DeploymentException
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint


@WebListener

@ServerEndpoint("/WsCamEndpoint/{user}/{viewer}")

class WsCamEndpoint implements ServletContextListener {
	
	private static final Set<Session> camsessions = Collections.synchronizedSet(new HashSet<Session>())

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServerContainer serverContainer =	org.codehaus.groovy.grails.web.context.ServletContextHolder.getServletContext().getAttribute("javax.websocket.server.ServerContainer")
		try {
			serverContainer?.addEndpoint(WsCamEndpoint.class)
			// Keep chat sessions open for ever
			def config=Holders.config
			int DefaultMaxSessionIdleTimeout=config.wschat.camtimeout  ?: 0
			serverContainer.setDefaultMaxSessionIdleTimeout(DefaultMaxSessionIdleTimeout as int)
		} catch (DeploymentException e) {
			e.printStackTrace()
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}


	@OnOpen
	public void whenOpening(Session userSession,EndpointConfig c,@PathParam("user") String user,@PathParam("viewer") String viewer) {
		userSession.setMaxBinaryMessageBufferSize(1024*512)
		camsessions.add(userSession)
		
		if (viewer.equals(user)) {
			userSession.getUserProperties().put("camuser", user);
			
			if (notLoggedIn(user)) {
				userSession.getUserProperties().put("username", user);
			}
		}else{
			userSession.getUserProperties().put("camuser", user);
			
			if (notLoggedIn(viewer)) {
				userSession.getUserProperties().put("username", viewer);
			}
		}
	}

	@OnMessage
	public void processVideo(byte[] imageData, Session userSession) {
		String user = userSession.getUserProperties().get("camuser") as String
		try {
			ByteBuffer buf = ByteBuffer.wrap(imageData)
			Iterator<Session> iterator=camsessions?.iterator()
			while (iterator?.hasNext())  {
				def crec=iterator?.next()
				if (crec.isOpen() && user.equals(crec.getUserProperties().get("camuser"))) {
					crec.getBasicRemote().sendBinary(buf)
				}
			}
		} catch (Throwable ioe) {
			log.info "Error sending message " + ioe.getMessage()
		}
	}
	
	@OnMessage
	public String handleMessage(String message,Session userSession) throws IOException {
		verifyAction(userSession,message)
	}
	
	@OnClose
	public void whenClosing(Session userSession) {
		camsessions.remove(userSession)
	}
	
	private void verifyAction(Session userSession,String message) {
		def myMsg=[:]
		String username=userSession.getUserProperties().get("username") as String
		Boolean isuBanned=false
		if (!username)  {
			if (message.startsWith("DISCO:-")) {
				camsessions.remove(userSession)
					//myMsg.put("message", "${username} has left ${room}")
					//broadcast(userSession,myMsg)
				println "--- Camsession ${username} disconnected"
			}	
		}
	}	
			
	private Boolean notLoggedIn(String user) {
		Boolean notloggedin=true
		Iterator<Session> iterator=camsessions?.iterator()
		while (iterator?.hasNext())  {
			def crec=iterator?.next()
			if (crec.isOpen()) {
				def cuser=crec.getUserProperties().get("username").toString()
				if (cuser.equals(user)) {
					notloggedin=false
				}
			}
		}
		return notloggedin
	}
}
