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

@ServerEndpoint("/WsChatFileEndpoint/{user}/{viewer}")

class WsChatFileEndpoint extends ChatUtils implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {

			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(WsChatFileEndpoint)
			}
			int defaultMaxSessionIdleTimeout = 0 //config.timeout ?: 0
			serverContainer.defaultMaxSessionIdleTimeout = defaultMaxSessionIdleTimeout
		}
		catch (IOException e) {
			log.error e.message, e
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}

	@OnOpen
	public void whenOpening(Session userSession,EndpointConfig c,@PathParam("user") String user,@PathParam("viewer") String viewer) {
		userSession.setMaxBinaryMessageBufferSize(1024*512)
		userSession.setMaxTextMessageBufferSize(1000000)

		def ctx = Holders.applicationContext
		wsChatAuthService = ctx.wsChatAuthService
		wsChatUserService = ctx.wsChatUserService
		wsChatMessagingService = ctx.wsChatMessagingService
		wsChatRoomService = ctx.wsChatRoomService
		wsFileService = ctx.wsFileService
		
		if (loggedIn(viewer)) {
			userSession.userProperties.put("camusername", viewer)
			userSession.userProperties.put("camuser", user+":"+viewer)
			
			wsFileService.addUser(viewer, userSession)
		}else{
			log.error "could not find chat user ! ${viewer}"
		}
	}

	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		wsFileService.verifyFileAction(userSession,message)
	}

	@OnClose
	public void whenClosing(Session userSession) throws SocketException {
		try {
			wsFileService.discoCam(userSession)
		} catch(SocketException e) {
			log.error "Error $e"
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}


}
