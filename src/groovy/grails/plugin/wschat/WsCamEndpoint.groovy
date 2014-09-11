package grails.plugin.wschat


import grails.util.Holders

import java.nio.ByteBuffer

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.DeploymentException
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


	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		final ServerContainer serverContainer =	org.codehaus.groovy.grails.web.context.ServletContextHolder.getServletContext().getAttribute("javax.websocket.server.ServerContainer")
		try {
			serverContainer?.addEndpoint(WsCamEndpoint.class)
			// Keep chat sessions open for ever
			def config=Holders.config
			int DefaultMaxSessionIdleTimeout=config.wschat.camtimeout  ?: 0
			serverContainer.setDefaultMaxSessionIdleTimeout(DefaultMaxSessionIdleTimeout as int)
		} catch (DeploymentException e) {
			e.printStackTrace()
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}


	@OnOpen
	public void whenOpening(Session userSession,EndpointConfig c,@PathParam("user") String user,@PathParam("viewer") String viewer) {
		userSession.setMaxBinaryMessageBufferSize(1024*512)
		camsessions.add(userSession)

		if (viewer.equals(user)) {
			userSession.getUserProperties().put("camuser", user+":"+user);
			if (notLoggedIn(user)) {
				userSession.getUserProperties().put("camusername", user);
			}

		}else{

			userSession.getUserProperties().put("camuser", user+":"+viewer);
			if (notLoggedIn(viewer)) {
				userSession.getUserProperties().put("camusername", viewer);
			}

			/*def combo=user+":"+viewer
			 if (!camusers.contains(combo)){
			 camusers.add(combo)
			 }
			 */
		}
	}

	@OnMessage
	public void processVideo(byte[] imageData, Session userSession) {
		String camuser = userSession.getUserProperties().get("camuser") as String
		String realCamUser
		if (camuser) {
			realCamUser=camuser.substring(0,camuser.indexOf(':'))
		}
		try {
			ByteBuffer buf = ByteBuffer.wrap(imageData)
			Iterator<Session> iterator=camsessions?.iterator()
			while (iterator?.hasNext())  {
				def crec=iterator?.next()

				if (crec?.isOpen()) {
					String chuser=crec?.getUserProperties().get("camuser") as String
					if (chuser && chuser.startsWith(realCamUser)) {
						crec.getBasicRemote().sendBinary(buf)
					}
				}
			}
		} catch (Throwable ioe) {
			log.debug "Error sending message " + ioe.getMessage()
		}
	}

	@OnMessage
	public String handleMessage(String message,Session userSession) throws IOException {

		verifyCamAction(userSession,message)
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
