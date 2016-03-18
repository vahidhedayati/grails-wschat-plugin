package grails.plugin.wschat.beans

import grails.util.Holders
import grails.validation.Validateable

class ConfigBean implements Validateable {
	//Declare end points
	public static final String chatPoint = 'wsChat'
	public static final String chatEndPoint = 'WsChatEndpoint'
	public static final String camEndPoint = 'WsCamEndpoint'
	public static final String fileEndPoint = 'WsChatFileEndpoint'
	public final String rootp=rootPath
	public final String KEYSTORE =  getConfig('KEYSTORE') ?: "/home/user/IdeaProjects/wschat/tomcat.jks"
	public final String KEYPASSWORD = getConfig('KEYPASSWORD') ?:"changeit"

	public final String wsProtocol = getConfig('wsProtocol') ?: 'ws'
	public final String siteProtocol = getConfig('siteProtocol') ?: 'http'
	public final boolean isSecure

	public final String hostname = getConfig('hostname') ?: 'localhost:8080'
	public final boolean addAppName = getConfig('addAppName')?validateBool(getConfig('addAppName')):false
	public final boolean showtitle = getConfig('showtitle')?validateBool(getConfig('showtitle')):true
	public final boolean debug = getConfig('debug')?validateBool(getConfig('debug')):false
	public final boolean process = getConfig('disable.login')?validateBool(getConfig('disable.login')):false
	public final boolean enable_Chat_AI = getConfig('enable_Chat_AI')?validateBool(getConfig('enable_Chat_AI')):true
	public final boolean enable_Chat_BadWords = getConfig('enable_Chat_BadWords')?validateBool(getConfig('enable_Chat_BadWords')):true
	public final boolean enable_Chat_Bot = getConfig('enable_Chat_Bot')?validateBool(getConfig('enable_Chat_Bot')):true
	public final boolean liveChatAskName = getConfig('liveChatAskName')?validateBool(getConfig('liveChatAskName')):true
	public final String liveChatNameMessage = getConfig('liveChatNameMessage') ?: 'Hi new user, what is your name ?'
	public final boolean enable_AI = getConfig('enable_AI')?validateBool(getConfig('enable_AI')):true
	public final String assistant = getConfig('liveChatAssistant') ?: 'assistant'
	public final String chatTitle = getConfig('title') ?: 'Grails Websocket Chat'
	public final String chatHeader = getConfig('heading') ?: 'Grails websocket chat'
	public final String frontUser = getConfig('frontend') ?: '_frontend'
	public final String botMessage = getConfig('botMessage') ?: 'Greetings I am the room bot'
	public final boolean enableSecurity = getConfig('enableSecurity') ? validateBool(getConfig('enableSecurity')):false
	public static final Date now = new Date()

	Boolean addLayouts = false
	String room
	ArrayList rooms
	String chatuser
	String uri


	boolean getIsSecure() {
		if (wsProtocol=='wss') {
			return true
		}
		return false
	}

	String getChatuser() {
		if (chatuser) {
			chatuser=chatuser.replace(' ', '_').replace('.', '_')
		}
		return chatuser
	}

	Boolean getAddLayouts() {
		return validateBool(addLayouts)
	}

	String getAppName() {
		String appName= grails.util.Metadata.current.applicationName ?: Holders.grailsApplication.metadata['app.name']  ?: ''
		return appName
	}

	String getUrl() {
		String url="${siteProtocol}://${hostname}/${appName}/${chatPoint}/"
		if (!addAppName) {
			url="${siteProtocol}://${hostname}/${chatPoint}/"
		}
		return url
	}

	String getUri() {
		String uri="${wsProtocol}://${hostname}/${appName}/${chatEndPoint}/"
		if (!addAppName) {
			uri="${wsProtocol}://${hostname}/${chatEndPoint}/"
		}
		return uri
	}

	String getCamEndpoint() {
		String camEndpoint="${wsProtocol}://${hostname}/${appName}/${camEndPoint}/"
		if (!addAppName) {
			camEndpoint="${wsProtocol}://${hostname}/${camEndPoint}/"
		}
		return camEndpoint
	}

	String getFileEndpoint() {
		String fileEndpoint="${wsProtocol}://${hostname}/${appName}/${fileEndPoint}/"
		if (!addAppName) {
			fileEndpoint="${wsProtocol}://${hostname}/${fileEndPoint}/"
		}
		return fileEndpoint
	}


	static constraints = {
		room(nullable:true)
		rooms(nullable:true)
		chatuser(nullable:true)
		uri(nullable:true)
	}

	//worker for the boolean logic
	private Boolean validateBool(def input) {
		if (input) {
			if (input instanceof Boolean) {
				return input
			} else {
				return input.toBoolean()
			}
		} else {
			return false
		}
	}

	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
	def getRootPath() {
		Holders.config.parentContext.getResource("WEB-INF/grails-app/views/layouts") ?: ''
	}

	def getConf(String configProperty) {
		Holders.config[configProperty] ?: ''
	}

	def getConfig(String configProperty) {
		Holders.config.wschat[configProperty] ?: ''
	}
}
