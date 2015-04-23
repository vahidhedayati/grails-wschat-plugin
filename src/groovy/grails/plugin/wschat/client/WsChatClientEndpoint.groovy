package grails.plugin.wschat.client


import grails.converters.JSON
import grails.plugin.wschat.users.WsChatUserService

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

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ClientEndpoint
public class WsChatClientEndpoint {

	private final Logger log = LoggerFactory.getLogger(getClass().name)

	private Session userSession = null

	private MessageHandler messageHandler
	private WsClientProcessService  wsClientProcessService
	private WsChatUserService  wsChatUserService
	private ConfigObject config


	public WsChatClientEndpoint(final URI endpointURI) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer()
			container.connectToServer(this, endpointURI)
		} catch (Exception e) {
			log.error e
			//throw new RuntimeException(e)
		}
	}

	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) {
		this.userSession = userSession
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def grailsApplication = ctx.grailsApplication
		wsClientProcessService = ctx.wsClientProcessService
		wsChatUserService = ctx.wsChatUserService
		
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
		userSession.basicRemote.sendText(message)
	}

	public void disconnectClient(String user) {
		String message="DISCO:-"+user
		userSession.basicRemote.sendText(message)
	}

	boolean verifyUser(String userId) {
		return wsChatUserService.findUser(userId)
	}

	public void processAction( String user, boolean pm,String actionthis, String sendThis,
			String divId, String msgFrom, boolean strictMode, boolean masterNode) {
		wsClientProcessService.processAct(user ,pm ?: false, 
			actionthis ?: '', sendThis ?: '', divId ?: '_', msgFrom ?: '',
			strictMode ?: false, masterNode ?: false)
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
		///log.error t.printStackTrace()
	}

}
