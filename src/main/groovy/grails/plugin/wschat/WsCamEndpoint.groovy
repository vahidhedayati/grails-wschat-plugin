package grails.plugin.wschat

import grails.util.Environment
import grails.util.Holders

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer

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
@ServerEndpoint("/WsCamEndpoint/{user}/{viewer}")
class WsCamEndpoint extends ChatUtils implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass().name)



	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {

			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(WsCamEndpoint)
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
		//userSession.setmaxMessageSize(-1L)
		//def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def ctx = Holders.applicationContext
		wsChatAuthService = ctx.wsChatAuthService
		wsChatUserService = ctx.wsChatUserService
		wsChatMessagingService = ctx.wsChatMessagingService
		wsChatRoomService = ctx.wsChatRoomService
		wsCamService = ctx.wsCamService
		if (loggedIn(viewer)) {
			userSession.userProperties.put("camusername", viewer)
			userSession.userProperties.put("camuser", user+":"+viewer)
			//camUsers.putIfAbsent(viewer, userSession)
			wsCamService.addUser(viewer, userSession)
		}else{
			log.error "could not find chat user ! ${viewer}"
		}
	}

	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		wsCamService.verifyCamAction(userSession,message)
	}

	@OnMessage
	public void processVideo(byte[] imageData, Session userSession) {
		String camuser = userSession.userProperties.get("camuser") as String
		String realCamUser = wsCamService.realCamUser(camuser)
		ByteBuffer buf = ByteBuffer.wrap(imageData)
		camNames.each { String chuser, Session crec ->
			if (crec && crec.isOpen()) {
				if (chuser && chuser.startsWith(realCamUser)) {
					crec.basicRemote.sendBinary(buf)
				}
			}
		}

	}

	@OnClose
	public void whenClosing(Session userSession) throws SocketException {
		try {
			wsCamService.discoCam(userSession)
		} catch(SocketException e) {
			log.error "Error $e"
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}


}
