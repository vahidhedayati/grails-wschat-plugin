package grails.plugin.wschat

import grails.util.Environment
import grails.util.Holders
import org.slf4j.Logger
import org.slf4j.LoggerFactory


import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
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
	private final Logger log = LoggerFactory.getLogger(getClass().name)

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {
			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(WsChatEndpoint)
			}
			int defaultMaxSessionIdleTimeout = 0 //config.timeout ?: 0
			serverContainer.defaultMaxSessionIdleTimeout = defaultMaxSessionIdleTimeout
		}
		catch (IOException e) {
			log.error e.message, e
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) {
		userSession.userProperties.put("startTime", new Date())
		userSession.userProperties.put("room", room)
		//def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def ctx = Holders.applicationContext
		wsChatAuthService = ctx.wsChatAuthService
		wsChatUserService = ctx.wsChatUserService
		wsChatMessagingService = ctx.wsChatMessagingService
		wsChatRoomService = ctx.wsChatRoomService
		messageSource = ctx.messageSource
		localeResolver = ctx.localeResolver

	}

	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		verifyAction(userSession,message)
	}

	@OnClose
	public void handeClose(Session userSession) throws SocketException {
		if (userSession) {
			String username = userSession?.userProperties?.get("username")
			// null users issue in badly formatted _process.gsp
			// when client/server tests where being done as part of 1.20 release
			// left for similar issues - usually should not occur
			if (username && username!='null') {
				wsChatAuthService.validateLogOut(username as String)
				destroyChatUser(username)
			}
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}

}
