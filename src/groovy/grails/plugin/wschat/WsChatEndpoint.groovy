package grails.plugin.wschat


import grails.converters.JSON
import grails.util.Holders
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

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
//@ServerEndpoint("/WsChatEndpoint/{room}")
@ServerEndpoint("/WsChatEndpoint")
class WsChatEndpoint implements ServletContextListener {
	
	private static List users = Collections.synchronizedList(new ArrayList())
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>())
	
	@Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
   		final ServerContainer serverContainer =	org.codehaus.groovy.grails.web.context.ServletContextHolder.getServletContext().getAttribute("javax.websocket.server.ServerContainer")
	    try {
            serverContainer?.addEndpoint(WsChatEndpoint.class)
			// Keep chat sessions open for ever
			def config=Holders.config
			int DefaultMaxSessionIdleTimeout=config.wschat.timeout  ?: 0
			serverContainer.setDefaultMaxSessionIdleTimeout(DefaultMaxSessionIdleTimeout as int)
        } catch (DeploymentException e) {
            e.printStackTrace()
        }
    }

	@Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
	
    @OnOpen
    //public void handleOpen(Session userSession,EndpointConfig c,@PathParam("room") String room) { 
	public void handleOpen(Session userSession) {
		chatroomUsers.add(userSession)
    }	
	
    @OnMessage
    public String handleMessage(String message,Session userSession) throws IOException {
		verifyAction(userSession,message)
    }
	
    @OnClose
    public void handeClose(Session userSession) {
		String username=userSession?.getUserProperties()?.get("username")
		if (dbSupport()&&username) {
			validateLogOut(username as String)
		}
        //chatroomUsers.remove(userSession)
    }
	
    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace()
    }
	
	private void sendUserList(String iuser,Map msg) {
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers?.iterator()
		while (iterator?.hasNext())  {
			def crec=iterator?.next()
			if (crec) {
				def cuser=crec.getUserProperties().get("username").toString()
				if (cuser.equals(iuser)) {
					crec.getBasicRemote().sendText(myMsgj as String)
				}
			}
		}
	}
	
	private void removeUser(String username) {
		Iterator<Session> iterator=chatroomUsers?.iterator()
	
		while (iterator?.hasNext())  {
			def crec=iterator?.next()
			if (crec) {
				def cuser=crec.getUserProperties().get("username").toString()
				if (cuser.equals(username)) {
					iterator.remove()
				}
			}
		}
	}
	
	private void sendUsers(String username) {
		Iterator<Session> iterator=chatroomUsers?.iterator()
		while (iterator?.hasNext())  {
			def uList=[]
			def finalList=[:]
			def crec=iterator?.next()
			if (crec) {
				def cuser=crec.getUserProperties().get("username").toString()
				def blocklist
				def friendslist
				if (dbSupport()) {
					blocklist=ChatBlockList.findAllByChatuser(currentUser(cuser))
					friendslist=ChatFriendList.findAllByChatuser(currentUser(cuser))
				}
				getCurrentUserNames().each {
					def myUser=[:]
					if (cuser.equals(it)) {
						myUser.put("owner", it)
						uList.add(myUser)				
					}else{
						if ((blocklist)&&(blocklist.username.contains(it))) {
							myUser.put("blocked", it)
							uList.add(myUser)
							
						}else if  ((friendslist)&&(friendslist.username.contains(it))) {
							myUser.put("friends", it)
							uList.add(myUser)
						}else{
							myUser.put("user", it)
							uList.add(myUser)
						}
					}	
				}
				finalList.put("users", uList)
				sendUserList(cuser,finalList)
			}	
		}
	}

	private void broadcast(Map msg) {
		def myMsgj=msg as JSON
		Iterator<Session> iterator=chatroomUsers?.iterator()
		while (iterator?.hasNext()) {
			def crec=iterator?.next()
			if (crec) {
				crec.getBasicRemote()?.sendText(myMsgj as String)
			}
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
		
		String username=userSession.getUserProperties().get("username") as String
		String connector="CONN:-"
		Boolean isuBanned=false		
		if (!username)  {
			if (message.startsWith(connector)) {
				username=message.substring(message.indexOf(connector)+connector.length(),message.length()).replaceAll(' ', '_').trim()
				userSession.getUserProperties().put("username", username)
				isuBanned=isBanned(username)
				if (!isuBanned){
					if (dbSupport()) {
						def userLevel=validateLogin(username)
						userSession.getUserProperties().put("userLevel", userLevel)
						Boolean useris=isAdmin(userSession)
						def myMsg1=[:]
						myMsg1.put("isAdmin", useris.toString())
						messageUser(userSession,myMsg1)
				
					}
					if (!users.contains(username)){
						users.add(username)
					}
					sendUsers(username)
					myMsg.put("message", "${username} has joined")
			
				}else{
				
					def myMsg1=[:]
					myMsg1.put("isBanned", "user ${username} is banned being disconnected")
					messageUser(userSession,myMsg1)
					//chatroomUsers.remove(userSession)
				}	
			}

			if ((myMsg)&&(!isuBanned)) {
				broadcast(myMsg)
			}
		}else{
			if (message.startsWith("DISCO:-")) {
				users.remove(username)
				removeUser(username)
				//chatroomUsers.remove(userSession)
				sendUsers(null)
				isuBanned=isBanned(username)
				if (!isuBanned){
					myMsg.put("message", "${username} has left")
					broadcast(myMsg)
				}
	
			}else if (message.startsWith("/pm")) {
				def values=parseInput("/pm ",message)
				def user=values.user
				def msg=values.msg
				if (!user.equals(username)) {
					myMsg.put("msgFrom", username)
					myMsg.put("msgTo", user)
					myMsg.put("privateMessage", "${msg}")
					privateMessage(user,myMsg,userSession)
				}else{
					myMsg.put("message","Private message self?")
					messageUser(userSession,myMsg)
				}	
			}else if (message.startsWith("/block")) {
				def values=parseInput("/block ",message)
				def user=values.user
				def person=values.msg
				blockUser(user,person)
				sendUsers(user)
			}else if (message.startsWith("/kickuser")) {
				def p1="/kickuser "
				def user=message.substring(p1.length(),message.length())
				kickUser(userSession,user)
			}else if (message.startsWith("/banuser")) {
				def values=parseBan("/banuser ",message)
				def user=values.user
				def duration=values.msg
				def period=values.msg2
				banUser(userSession,user,duration,period)
			}else if (message.startsWith("/unblock")) {
				def values=parseInput("/unblock ",message)
				def user=values.user
				def person=values.msg
				unblockUser(user,person)
				sendUsers(user)
			}else if (message.startsWith("/add")) {
				def values=parseInput("/add ",message)
				def user=values.user
				def person=values.msg
				addUser(user,person)
				sendUsers(user)
			}else if (message.startsWith("/removefriend")) {
				def values=parseInput("/removefriend ",message)
				def user=values.user
				def person=values.msg
				removeUser(user,person)
				sendUsers(user)
			// Usual chat messages bound for all	
			}else{
				myMsg.put("message", "${username}: ${message}")
				broadcast(myMsg)
			}
		}
		
	}

	
	private void privateMessage(String user,Map msg,Session userSession) {
		def myMsg=[:]
		def myMsgj=msg as JSON
		String urecord=userSession.getUserProperties().get("username") as String
		Iterator<Session> iterator=chatroomUsers?.iterator()
		Boolean found=false
		while (iterator?.hasNext())  {
			def crec=iterator?.next()
			if (crec) {
				def cuser=crec.getUserProperties().get("username").toString()
				if (cuser.equals(user)) {
					Boolean sendIt=checkPM(urecord,user)
					Boolean sendIt2=checkPM(user,urecord)
					found=true
					if (sendIt&&sendIt2) {
						crec.getBasicRemote().sendText(myMsgj as String)
						myMsg.put("message","--> PM sent to ${user}")
						messageUser(userSession,myMsg)
					}else{
						myMsg.put("message","--> PM NOT sent to ${user}, you have been blocked !")
						messageUser(userSession,myMsg)
					}
				}
			}
		}
		if (found==false) {
			myMsg.put("message","Error: ${user} not found - unable to send PM")
			messageUser(userSession,myMsg)
		}
	}
	
	
	private String validateLogin(String username) {
		def exists=ChatUser.findByUsername(username)
		if (!exists) {
			def config=Holders.config
			String defaultPermission=config.wschat.defaultperm  ?: 'user'
			def perm=ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
			def cc=new ChatUser()
			cc.username=username
			cc.permissions=perm
			cc.save(flush:true)
			cc.discard()
			def logit=new ChatLogs()
			logit.username=username
			logit.loggedIn=true
			logit.loggedOut=false
			logit.save(flush:true)
			
			return perm.name as String
		}else{
		
			return exists.permissions.name as String
		}
	}
	
	private Boolean dbSupport() {
		def config=Holders.config
		Boolean dbsupport=false
		String dbsup=config.wschat.dbsupport  ?: 'yes'
		if ((dbsup.toLowerCase().equals('yes'))||(dbsup.toLowerCase().equals('true'))) {
			dbsupport=true
		}
		return dbsupport
	}
	
	private void validateLogOut(String username) {
		def logit=new ChatLogs()
		logit.username=username
		logit.loggedIn=false
		logit.loggedOut=true
		logit.save(flush:true)
	}
	
	private Boolean checkPM(String username, String urecord) {
		def found=ChatBlockList.findByChatuserAndUsername(currentUser(username),urecord)
		if (found) {
			return false
		}
		return true			
	}
	
	private Boolean isAdmin(Session userSession) {
		Boolean useris=false
		String userLevel=userSession.getUserProperties().get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris=true
		}
		return useris
	}
	
	private void kickUser(Session userSession,String username) {
		Boolean useris=isAdmin(userSession)
		if (useris) {
			logoutUser(username)
		}
	}
	
	private void banUser(Session userSession,String username,String duration,String period) {
		Boolean useris=isAdmin(userSession)
		if (useris) {
			banthisUser(username,duration,period)
			logoutUser(username)
		}
	}
	
	private void logoutUser(String username) {
		def myMsg=[:]
		myMsg.put("message", "${username} about to be kicked off")
		broadcast(myMsg)
		Iterator<Session> iterator=chatroomUsers.iterator()
		while (iterator.hasNext())  {
			def crec=iterator?.next()
			if (crec) {
				def uList=[]
				def finalList=[:]
				def cuser=crec.getUserProperties().get("username").toString()
				if (cuser.equals(username)) {
					def myMsg1=[:]
					myMsg1.put("system","disconnect")
					messageUser(crec,myMsg1)
				}
			}
		}	
	}		
	
	private void unblockUser(String username,String urecord) {
		def cuser=currentUser(username)
		def found=ChatBlockList.findByChatuserAndUsername(cuser,urecord)
		found.delete(flush: true)
	}
	
	private Boolean isBanned(String username) {
		Boolean yesis=false
		def now=new Date()
		def current = new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss').format(now)
		def found=ChatBanList.findAllByUsernameAndPeriodGreaterThan(username,current)
		def dd=ChatBanList.findAllByUsername(username)
		if (found) {
			yesis=true
		}
		return yesis
	}
	
	private void banthisUser(String username,String duration, String period) {
		def cc
		use(TimeCategory) {
			 cc=new Date() +(duration as int)."${period}"
		}
		def current = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(cc)
		def found=ChatBanList.findByUsername(username)
		if (!found) {
				def newEntry=new ChatBanList()
				newEntry.username=username
				newEntry.period=current
				newEntry.save(flush:true)
		}else{
			found.period=current
			found.save(flush:true)
		}
		
	}
	
	private void blockUser(String username,String urecord) {
		def cuser=currentUser(username)
		def found=ChatBlockList.findByChatuserAndUsername(cuser,urecord)
		if (!found) {
			def newEntry=new ChatBlockList()
			newEntry.chatuser=cuser
			newEntry.username=urecord
			newEntry.save(flush:true)
		}
	}
	
	private void addUser(String username,String urecord) {
		def cuser=currentUser(username)
		def found=ChatFriendList.findByChatuserAndUsername(cuser,urecord)
		if (!found) {
			def newEntry=new ChatFriendList()
			newEntry.chatuser=cuser
			newEntry.username=urecord
			newEntry.save(flush:true)
		}
	}
	
	private void removeUser(String username,String urecord) {
		def cuser=currentUser(username)
		def found=ChatFriendList.findByChatuserAndUsername(cuser,urecord)
		found.delete(flush: true)
	}
	
	def currentUser(String username) {
		return ChatUser.findByUsername(username)
	}
	
	private String getCurrentUserName(Session userSession) {
		def myMsg=[:]
		String username=userSession.getUserProperties().get("username") as String
		if (!username) {
			myMsg.put("message","Access denied no username defined")
			messageUser(userSession,myMsg)
			//chatroomUsers.remove(userSession)
		}else{
			return username
		}
	}
	
	private Map<String, String> parseInput(String mtype,String message){
		def p1=mtype
		def mu=message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset=[]
		if (mu.indexOf(",")>-1) {
			user=mu.substring(0,mu.indexOf(","))
			msg=mu.substring(user.length()+1,mu.length())
		}else{
			user=mu.substring(0,mu.indexOf(" "))
			msg=mu.substring(user.length()+1,mu.length())
		}
		
		Map<String, String> values = new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}
	
	
	private Map<String, String> parseBan(String mtype,String message){
		def p1=mtype
		def mu=message.substring(p1.length(),message.length())
		def msg2
		def resultset=[]
		def	user=mu.substring(0,mu.indexOf(","))
		def	msg=mu.substring(user.length()+1,mu.length())
		if (msg.indexOf(':')>-1) {
			msg2=msg.substring(msg.indexOf(':')+1,msg.length())
			msg=msg.substring(0,msg.indexOf(':'))
		}
		Map<String, String> values = new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		if (msg2){
			values.put("msg2", msg2);
		}
		return values
	}
}
