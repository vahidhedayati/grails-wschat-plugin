package grails.plugin.wschat


import grails.plugin.wschat.client.WsChatClientService

import javax.servlet.ServletContext
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.websocket.ClientEndpoint
import javax.websocket.CloseReason
import javax.websocket.ContainerProvider
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.WebSocketContainer
import javax.websocket.server.PathParam
import javax.websocket.server.ServerContainer

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ClientEndpoint
public class WsChatClientEndpoint extends ChatUtils  implements ServletContextListener {
	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private Session userSession = null

	private MessageHandler messageHandler
	private WsChatClientService  wsChatClientService
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.servletContext
		final ServerContainer serverContainer = servletContext.getAttribute("javax.websocket.server.ServerContainer")
		try {

			def ctx = servletContext.getAttribute(GA.APPLICATION_CONTEXT)

			def grailsApplication = ctx.grailsApplication

			wsChatAuthService = ctx.wsChatAuthService
			wsChatClientService = ctx.wsChatClientService
			
			config = grailsApplication.config.wschat
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



	public WsChatClientEndpoint(final URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) {
		this.userSession = userSession
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		wsChatAuthService = ctx.wsChatAuthService
		wsChatClientService = ctx.wsChatClientService
		
		config = grailsApplication.config.wschat
				
	}


	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		this.userSession = null
	}

	@OnMessage
	public void onMessage(final String message) {
		if (messageHandler != null) {
			messageHandler.handleMessage(message)
		}
	}

	public void addMessageHandler(final MessageHandler msgHandler) {
		messageHandler = msgHandler
	}

	public void connectClient(String user) {
		String message="CONN:-"+user
		//wsChatAuthService.connectUser(message,userSession,room)
		userSession.basicRemote.sendText(message)
	}

	public void disconnectClient(String user) {
		String message="DISCO:-"+user
		userSession.basicRemote.sendText(message)
	}

	public void listDomain(String sendThis,String divId) {
		
	}
	
	public void processAction( Session userSess, boolean pm,String actionthis, String sendThis, 
		String divId, String msgFrom, boolean strictMode) {
		/*
		if (pm) {
			if (strictMode==false) {
				sendMessage("||>>"+sendThis)
			}
			sendMessage("/pm ${msgFrom},${sendThis}")
		}else{
			sendMessage("${sendThis}")
		}
		*/
		wsChatClientService.processAct(userSess ?: userSession ,pm ?: false, actionthis ?: '', sendThis ?: '', divId ?: '_', msgFrom ?: '',strictMode ?: false)
	}
	
	public void sendMessage(final String message) {
		userSession.basicRemote.sendText(message)
	}
	
	public Session returnSession() {
		return userSession
	}

	public static interface MessageHandler {
		public void handleMessage(String message)
	}

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
	}

}
