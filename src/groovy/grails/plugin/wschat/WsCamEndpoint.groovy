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

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@WebListener

@ServerEndpoint("/WsCamEndpoint/{user}/{viewer}")

class WsCamEndpoint extends ChatUtils implements ServletContextListener {
	
	private final Logger log = LoggerFactory.getLogger(getClass().name)
	

	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {
			
			// Adding this conflicts with listener added via plugin descriptor
			// Whilst it works as run-app - in production this causes issues
			def environment=Environment.current.name
			if (environment=='development') {
				serverContainer.addEndpoint(WsCamEndpoint)
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
		if (loggedIn(viewer)) {
			userSession.setMaxBinaryMessageBufferSize(1024*512)
			userSession.setMaxTextMessageBufferSize(1000000)
			//userSession.setmaxMessageSize(-1L)
			if (viewer.equals(user)) {
				userSession.userProperties.put("camuser", user+":"+user);
			}else{
				userSession.userProperties.put("camuser", user+":"+viewer);
			}
			if (!camLoggedIn(viewer)) {
				userSession.userProperties.put("camusername", viewer);
				camsessions.add(userSession)
			}
			
			def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
			def grailsApplication = ctx.grailsApplication
			config = grailsApplication.config.wschat
			
		}else{
			log.info "could not find chat user ! ${viewer}"
		}
	}

	@OnMessage
	public String handleMessage(String message,Session userSession) throws IOException {
		verifyCamAction(userSession,message)
	}

	@OnMessage
	public void processVideo(byte[] imageData, Session userSession) {
		String camuser = userSession.userProperties.get("camuser") as String
		String realCamUser=realCamUser(camuser)
		try {
			ByteBuffer buf = ByteBuffer.wrap(imageData)
			Iterator<Session> iterator=camsessions?.iterator()
			if (iterator) {
				while (iterator?.hasNext())  {
					def crec=iterator?.next()
					if (crec?.isOpen()) {
						String chuser=crec?.userProperties.get("camuser") as String
						if (chuser && chuser.startsWith(realCamUser)) {
							crec.basicRemote.sendBinary(buf)
						}
					}
				}
			}
		} catch (Throwable ioe) {
			log.debug "Error sending message " + ioe.getMessage()
		}
	}

	@OnClose
	public void whenClosing(Session userSession) throws SocketException {
		try {
			discoCam(userSession)
		} catch(SocketException e) {
			log.debug "Error $e"
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}


}
