package grails.plugin.wschat

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.plugin.wschat.interfaces.UserMaps

import javax.websocket.Session
import java.util.concurrent.ConcurrentMap

class WsChatConfService implements UserMaps, GrailsApplicationAware {

	static transactional  =  false
	def config
	def cfg
	GrailsApplication grailsApplication

	/*
	 * ChatUser ConcurrentHashMap
	 */
	ConcurrentMap<String, Map<String,Session>> getChatNames() {
		return chatroomUsers
	}
	Collection<String> getChatUsers() {
		return Collections.unmodifiableSet(chatroomUsers.keySet())
	}
	Session getChatUser(String username,String room) {
		Map<String,Session> records = chatroomUsers.get(username)
		return (records.find{it.key==room}?.value as Session)
	}
	boolean chatUserExists(String username) {
		return chatUsers.contains(username)
	}
	boolean destroyChatUser(String username) {
		return chatroomUsers.remove(username) != null
	}

	/*
	 * CamUser ConcurrentHashMap
	 */
	Collection<String> getAvUsers() {
		return Collections.unmodifiableSet(camUsers.keySet())
	}
	ConcurrentMap<String, Session>  getCamNames() {
		return camUsers
	}
	Session getCamUser(String username) {
		Session userSession = camUsers.get(username)
		return userSession
	}
	boolean camUserExists(String username) {
		return avUsers.contains(username)
	}
	boolean destroyCamUser(String username) {
		return camUsers.remove(username) != null
	}

	/*
	 * fileroomUser ConcurrentHashMap
	 */
	Collection<String> getFileUsers() {
		return Collections.unmodifiableSet(fileroomUsers.keySet())
	}
	ConcurrentMap<String, Session>  getFileNames() {
		return fileroomUsers
	}
	Session getFileUser(String username) {
		Session userSession = fileroomUsers.get(username)
		return userSession
	}
	boolean fileUserExists(String username) {
		return fileUsers.contains(username)
	}
	boolean destroyFileUser(String username) {
		return fileroomUsers.remove(username) != null
	}

	public static final String CONNECTOR = "CONN:-"
	public static final String LIVE_CONNECTOR = "LIVECONN:-"
	public static final String DISCONNECTOR = "DISCO:-"
	public static final String LIVE_DISCONNECTOR = "LIVEDISCO:-"
	public static final String CHATAPP = "WsChatEndpoint"
	public static final String CHATVIEW = "wsChat"

	//static final Set<HashMap<String[],String[]>> clientMaster = ([:] as Set).asSynchronized()
	//static final Set<HashMap<String[],String[]>> clientSlave = ([:] as Set).asSynchronized()

	Map getWsconf() {
		String process = config.disable.login ?: 'no'
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'

		String hostname = config.hostname ?: 'localhost:8080'
		String addAppName = config.add.appName ?: 'no'
		JSON iceservers  = cfg.stunServers as JSON
		String showtitle = config.showtitle ?: 'yes'
		return [process:process, chatTitle:chatTitle,
			chatHeader:chatHeader,  hostname:hostname, addAppName:addAppName,
			iceservers:iceservers, showtitle:showtitle]
	}

	boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}

	boolean getSaveClients() {
		return isConfigEnabled(config.storeForFrontEnd ?: 'false')
	}

	Boolean isAdmin(Session userSession) {
		Boolean useris = false
		String userLevel = userSession.userProperties.get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris = true
		}
		return useris
	}

	String getApplicationName() {
		return grailsApplication.metadata['app.name']
	}

	String getFrontend() {
		return config.frontenduser ?: '_frontend'
	}

	void setGrailsApplication(GrailsApplication ga) {
		cfg = ga.config
		config = cfg.wschat
	}
}
