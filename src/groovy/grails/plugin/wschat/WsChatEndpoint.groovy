package grails.plugin.wschat


import java.io.IOException;

import grails.util.Holders

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.DeploymentException
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint


@WebListener
@ServerEndpoint("/WsChatEndpoint/{room}")
class WsChatEndpoint extends ChatUtils implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServerContainer serverContainer =	org.codehaus.groovy.grails.web.context.ServletContextHolder.getServletContext().getAttribute("javax.websocket.server.ServerContainer")
		try {
			serverContainer?.addEndpoint(WsChatEndpoint.class)
			// Keep chat sessions open for ever
			def config=Holders.config
			int DefaultMaxSessionIdleTimeout=config.wschat.timeout  ?: 0
			serverContainer.setDefaultMaxSessionIdleTimeout(DefaultMaxSessionIdleTimeout as int)
		} catch (DeploymentException e) {
			e.printStackTrace()
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) {
		chatroomUsers.add(userSession)
		userSession.getUserProperties().put("room", room)
	}

	@OnMessage
	public String handleMessage(String message,Session userSession) throws IOException {
		try {
			verifyAction(userSession,message)
		} catch(IOException e) {
			log.debug "Error $e"
		}
	}

	@OnClose
	public void handeClose(Session userSession) throws SocketException {
		try {
			String username=userSession?.getUserProperties()?.get("username")
			if (dbSupport()&&username) {
				validateLogOut(username as String)
			}
		} catch(SocketException e) {
			log.debug "Error $e"
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}






}
