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
	
	private void sendUserList(String iuser,Map msg) {
		def myMsg=[:]
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers.iterator()
		while (iterator.hasNext())  {
			def crec=iterator.next()
			def cuser=crec.getUserProperties().get("username").toString()
			if (cuser.equals(iuser)) {
				crec.getBasicRemote().sendText(myMsgj as String)
			}
		}
	}
	
	private void sendUsers(String username) { 
		Iterator<Session> iterator=chatroomUsers.iterator()
		while (iterator.hasNext())  {
			def crec=iterator.next()
			def cuser=crec.getUserProperties().get("username").toString()
			StringBuffer sb=new StringBuffer()
			if (cuser) {
				def myMsg=[:]
				def cclass='dropdown-submenu'
				if ((username)&&(username.equals(cuser))) {
					cclass="dropdown-submenu active"
				}
				sb.append("<li class=\"${cclass}\"><a tabindex=\"-1\" class=\"user-title\" href=\"#\">${cuser}</a>")
				sb.append('<ul class="dropdown-menu"><li><a href="#">PM'+cuser+'</a></li></ul></li>')
				myMsg.put("users", sb.toString())
				sendUserList(cuser,myMsg)
			}	
		}
	}
	
	private void broadcast(Map msg) {
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers.iterator()
		while (iterator.hasNext()) iterator.next().getBasicRemote().sendText(myMsgj as String)
	}
	
	private void privateMessage(String user,Map msg,Session userSession) {
		def myMsg=[:]
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers.iterator()
		Boolean found=false
		while (iterator.hasNext())  {
			def crec=iterator.next()
			def cuser=crec.getUserProperties().get("username").toString()
			if (cuser.equals(user)) {
				found=true
				crec.getBasicRemote().sendText(myMsgj as String)
				myMsg.put("message","--> PM sent to ${user}")
				messageUser(userSession,myMsg)
			}
		}
		if (found==false) {
			myMsg.put("message","Error: ${user} not found - unable to send PM")
			messageUser(userSession,myMsg)
		}	
	}
	private void messageUser(Session userSession,Map msg) { 
		def myMsgj=msg as JSON
		userSession.getBasicRemote().sendText(myMsgj as String)
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
				sendUsers(username)
				myMsg.put("message", "${username} has joined")
			} else{
				myMsg.put("message", "issue with request, being disconnected! ${message}")
				chatroomUsers.remove(userSession)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				users.remove(usernamec)
				
				chatroomUsers.remove(userSession)
				sendUsers(null)
				myMsg.put("message", "${usernamec} has left")
				broadcast(myMsg)
	
			}else if (message.startsWith("/pm")) {
				def p1="/pm "
				def mu=message.substring(p1.length(),message.length())
				def user
				def msg
				if (mu.indexOf(",")>-1) {
				 	user=mu.substring(0,mu.indexOf(","))
					 msg=mu.substring(user.length()+1,mu.length())
				}else{
					user=mu.substring(0,mu.indexOf(" "))
					msg=mu.substring(user.length()+1,mu.length())
				}
				if (!user.equals(usernamec)) {
					myMsg.put("message", "PM(${usernamec}):${msg}")
					privateMessage(user,myMsg,userSession)
				}else{
					myMsg.put("message","Private messaging yourself? action not permitted!")
					messageUser(userSession,myMsg)
				}	
				
			// Usual chat messages bound for all	
			}else{
				myMsg.put("message", "${usernamec}:${message}")
				broadcast(myMsg)
			}
		}
		
	}
	
	private String getCurrentUserName(Session userSession) {
		def myMsg=[:]
		String username=userSession.getUserProperties().get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			messageUser(userSession,myMsg)
			chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}
}
