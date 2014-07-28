package grails.plugin.wschat


import grails.converters.JSON

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener
import javax.websocket.DeploymentException
import javax.websocket.OnClose
import javax.websocket.OnError
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.ServerContainer
import javax.websocket.server.ServerEndpoint


@WebListener
@ServerEndpoint("/WsChatEndpoint")
class WsChatEndpoint implements ServletContextListener {
	
	private static List users = Collections.synchronizedList(new ArrayList());
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>())
	
   
	@Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
   		final ServerContainer serverContainer =	org.codehaus.groovy.grails.web.context.ServletContextHolder.getServletContext().getAttribute("javax.websocket.server.ServerContainer")
	    try {
            serverContainer?.addEndpoint(WsChatEndpoint.class)
        } catch (DeploymentException e) {
            e.printStackTrace()
        }
    }

	@Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
	
    @OnOpen
    public void handleOpen(Session userSession) { 
		chatroomUsers.add(userSession)
    }	
	
    @OnMessage
    public String handleMessage(String message,Session userSession) throws IOException {
		verifyAction(userSession,message)
    }
	
    @OnClose
    public void handeClose(Session userSession) {
        chatroomUsers.remove(userSession)
    }
	
    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace()
    }
	
	private void sendUsers() { 
		def myMsg=[:]
		StringBuffer sb=new StringBuffer()
		getCurrentUserNames().each {
			sb.append("${it}\n")
		}
		myMsg.put("users", sb.toString())
		broadcast(myMsg)
	}
	
	private void broadcast(Map msg) {
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers.iterator()
		while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(myMsgj as String)
	}
	
	public static List getCurrentUserNames() {
		return Collections.unmodifiableList(users);
	}
	
	private void verifyAction(Session userSession,String message) {
		def myMsg=[:]
		String usernamec=userSession.getUserProperties().get("username") as String
		String connector="CONN:-"		
		if (!usernamec)  {
			if (message.startsWith(connector)) {
				String username=message.substring(message.indexOf(connector)+connector.length(),message.length())
				userSession.getUserProperties().put("username", username)
				if (!users.contains(username)){
					users.add(username)
				}
				sendUsers()
				myMsg.put("message", "${username} has joined")
			} else{
				myMsg.put("message", "issue with request, being disconnected! ${message}")
				chatroomUsers.remove(userSession)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				users.remove(usernamec)
				sendUsers()
				chatroomUsers.remove(userSession)
				myMsg.put("message", "${usernamec} has left")
			}else{
				myMsg.put("message", "${usernamec}:${message}")
			}
		}
		broadcast(myMsg)
	}
	
	private String getCurrentUserName(Session userSession) {
		String username=userSession.getUserProperties().get("username") as String
		if (!username) {
			userSession.getBasicRemote().sendText("Access denied no username defined")
			chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}
}
