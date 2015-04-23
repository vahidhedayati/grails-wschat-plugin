package grails.plugin.wschat


import grails.util.Environment

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

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)

			def grailsApplication = ctx.grailsApplication

			config = grailsApplication.config.wschat
			int defaultMaxSessionIdleTimeout = config.camtimeout ?: 0
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
		println "-- $user :: ${viewer}"
		userSession.setMaxBinaryMessageBufferSize(1024*512)
		userSession.setMaxTextMessageBufferSize(1000000)
		//userSession.setmaxMessageSize(-1L)


		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config = grailsApplication.config.wschat

		wsChatAuthService = ctx.wsChatAuthService
		wsChatUserService = ctx.wsChatUserService
		wsChatMessagingService = ctx.wsChatMessagingService
		wsChatRoomService = ctx.wsChatRoomService
		//wsCamService = ctx.wsCamService
		wsFileService = ctx.wsFileService

		if (loggedIn(viewer)) {
			userSession.userProperties.put("camusername", viewer)

			if (viewer.equals(user)) {
				userSession.userProperties.put("camuser", user+":"+user)
			}else{
				userSession.userProperties.put("camuser", user+":"+viewer)

			}

			if (!camLoggedIn(viewer)) {
				fileroomUsers.putIfAbsent(user, userSession)
			}
			
		}else{
			log.error "could not find chat user ! ${viewer}"
		}
	}

	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		println "HM1 --- ${message} "
		wsFileService.verifyFileAction(userSession,message)
	}

	@OnMessage
	public void processVideo(byte[] imageData, Session userSession) {
		println "HM2 --- ${imageData} "
		String camuser = userSession.userProperties.get("camuser") as String
		String realCamUser = wsFileService.realCamUser(camuser)
		ByteBuffer buf = ByteBuffer.wrap(imageData)
		fileNames.each { String chuser, Session crec ->
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
