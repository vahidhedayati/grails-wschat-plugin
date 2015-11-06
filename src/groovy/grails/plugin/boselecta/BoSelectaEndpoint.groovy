package grails.plugin.boselecta


import grails.plugin.boselecta.interfaces.UserSessions
import grails.util.Environment

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
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@WebListener
@ServerEndpoint("/BoSelectaEndpoint/{job}")
class BoSelectaEndpoint  extends ConfService implements ServletContextListener {

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private ConfigObject config
	private AuthService authService
	private MessagingService messagingService

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {

			if (Environment.current == Environment.DEVELOPMENT) {
				serverContainer.addEndpoint(BoSelectaEndpoint)
			}

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)

			def grailsApplication = ctx.grailsApplication

			config = grailsApplication.config.boselecta
			int defaultMaxSessionIdleTimeout = config.timeout ?: 0
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
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("job") String job) {
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		config = grailsApplication.config.boselecta
		authService = ctx.authService
		messagingService = ctx.messagingService
		userSession.userProperties.put("job", job)
	}


	@OnMessage
	public void handleMessage(String message,Session userSession) throws IOException {
		try {
			verifyAction(userSession,message)
		} catch(IOException e) {
			log.debug "Error $e"
		}
	}

	@OnClose
	public void handeClose(Session userSession) throws SocketException {
		String username = userSession?.userProperties?.get("username")
		if (username) {
			destroyJobUser(username)
		}
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}

	private void verifyAction(Session userSession,String message) {
		def myMsg = [:]
		String username = userSession.userProperties.get("username") as String
		String job  =  userSession.userProperties.get("job") as String
		String connector = "CONN:-"
		Boolean isuBanned = false
		if (!username)  {
			if (message.startsWith(connector)) {
				authService.connectUser(message,userSession,job)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				authService.destroyJob(job)
			}else if (message.startsWith("/pm")) {
				def values = parseInput("/pm ",message)
				String user = values.user as String
				def msg = values.msg

				if (!user.equals(username)) {
					messagingService.privateMessage(userSession, user,msg)
				}else{
					myMsg.put("message","Private message self?")
					messagingService.messageUser(userSession,myMsg)
				}
			}
		}
	}

	private Session findSession(String username) {
		Session userSession
		jobNames.each { String cuser, Session crec ->
			if (crec && crec.isOpen()) {

				if (cuser.equals(username)) {
					userSession=crec
				}
			}
		}
		return userSession
	}

}
