package grails.plugin.wschat.client

import grails.util.Holders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.websocket.*
import javax.websocket.server.PathParam

//Used by SSL Client Socket connection
//TODO Still need to ensure this actually all works

public class PragmaticEndpoint extends Endpoint {
	 
	private final Logger log = LoggerFactory.getLogger(getClass().name)
	private Session userSession = null
	private WsClientProcessService wsClientProcessService

	@Override
	public void onOpen(final Session session, EndpointConfig ec) {
		this.userSession = userSession

		//def ctx= SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
		def ctx = Holders.applicationContext
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
