package grails.plugin.wschat

import grails.converters.JSON

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

import javax.websocket.Session


class WsChatConfService {

	static transactional  =  false

	def grailsApplication

	// Concurrent hashMap method -
	// to port over Session sets over to concurrentHashMap
	protected final ConcurrentMap<String, Session> chatroomUsers2 = new ConcurrentHashMap<String, Session>()

	public Collection<String> getKeyNames() {
		return Collections.unmodifiableSet(chatroomUsers2.keySet())
	}

	public Session getUser(String username) {
		Session userSession = chatroomUsers2.get(name)
		return userSession
	}

	public boolean userExists(String name) {
		return getKeyNames().contains(name)
	}

	public boolean destroyHash(String name) {
		return chatroomUsers2.remove(name) != null
	}	
	
	static final Set<HashMap<String[],String[]>> chatroomUsers1 = ([:] as Set).asSynchronized()
	static final Set<Session> chatroomUsers = ([] as Set).asSynchronized()
	final Set<Session> camsessions = ([] as Set).asSynchronized()

	public String CONNECTOR = "CONN:-"
	public String DISCONNECTOR = "DISCO:-"
	public String CHATAPP = "WsChatEndpoint"
	public String CHATVIEW = "wsChat"

	static final Set<HashMap<String[],String[]>> clientMaster = ([:] as Set).asSynchronized()
	static final Set<HashMap<String[],String[]>> clientSlave = ([:] as Set).asSynchronized()



	Map getWsconf() {
		String dbSupport = config.dbsupport ?: 'yes'
		String process = config.disable.login ?: 'no'
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'

		String hostname = config.hostname ?: 'localhost:8080'
		String addAppName = config.add.appName ?: 'yes'
		JSON iceservers = grailsApplication.config.stunServers as JSON
		String showtitle = config.showtitle ?: 'yes'
		return [dbSupport:dbSupport, process:process, chatTitle:chatTitle,
			chatHeader:chatHeader,  hostname:hostname, addAppName:addAppName,
			iceservers:iceservers, showtitle:showtitle]
	}



	boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}

	boolean getSaveClients() {
		return isConfigEnabled(config.storeForFrontEnd ?: 'false')
	}

	Boolean dbSupport() {
		Boolean dbsupport = false
		String dbsup = config.dbsupport  ?: 'yes'
		if ((dbsup.toLowerCase().equals('yes'))||(dbsup.toLowerCase().equals('true'))) {
			dbsupport = true
		}
		return dbsupport
	}

	ChatUser currentUser(String username) {
		ChatUser cu
		if (dbSupport()) {
			ChatUser.withTransaction {
				cu =  ChatUser.findByUsername(username)
			}
		}
		return cu
	}

	Boolean isAdmin(Session userSession) {
		Boolean useris = false
		String userLevel = userSession.userProperties.get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris = true
		}
		return useris
	}

	Boolean isBanned(String username) {
		Boolean yesis = false
		if (dbSupport()) {
			def now = new Date()
			def current  =  new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss').format(now)
			ChatBanList.withTransaction {
				def found = ChatBanList.findAllByUsernameAndPeriodGreaterThan(username,current)
				def dd = ChatBanList.findAllByUsername(username)
				if (found) {
					yesis = true
				}
			}
		}
		return yesis
	}

	String getApplicationName() {
		return grailsApplication.metadata['app.name']
	}

	String getFrontend() {
		return config.frontenduser ?: '_frontend'
		//return cuser
	}

	def getConfig() {
		grailsApplication?.config?.wschat ?: ''
	}

}
