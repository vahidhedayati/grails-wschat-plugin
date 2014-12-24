package grails.plugin.wschat.client


import javax.websocket.ClientEndpoint
import javax.websocket.CloseReason
import javax.websocket.EndpointConfig
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.PathParam

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ClientEndpoint
public class ChatClientEndpoint  {
	 
	private final Logger log = LoggerFactory.getLogger(getClass().name)
	private Session userSession = null
	private WsClientProcessService wsClientProcessService
	
	@OnOpen
	public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) {
		this.userSession = userSession
		
		def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		wsClientProcessService = ctx.wsClientProcessService
	}
	
	@OnClose
	public void onClose(final Session userSession, final CloseReason reason) {
		this.userSession = null
	}

	@OnMessage
    public void onMessage(String message, Session userSession){
        try {
			wsClientProcessService.processResponse(userSession,message)
        } catch (IOException ex) {
            ex.printStackTrace()
        }
    }

	@OnError
	public void handleError(Throwable t) {
		t.printStackTrace()
		//log.error t
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

}
