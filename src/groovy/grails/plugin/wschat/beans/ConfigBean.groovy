package grails.plugin.wschat.beans

import grails.util.Holders
import grails.validation.Validateable

@Validateable
class ConfigBean {

	//Declare end points
	public static final String chatPoint = 'wsChat'
	public static final String chatEndPoint = 'WsChatEndpoint'
	public static final String camEndPoint = 'WsCamEndpoint'
	public static final String fileEndPoint = 'WsChatFileEndpoint'
	public final String hostname = getConfig('hostname') ?: 'localhost:8080'
	public final boolean addAppName = getConfig('addAppName')?validateBool(getConfig('addAppName')):true
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
	public static final Date now = new Date()

	Boolean addLayouts = false
	String room
	ArrayList rooms
	String chatuser

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
		String appName= grails.util.Metadata.current.applicationName ?: Holders.grailsApplication.metadata['app.name']
		return appName
	}
	
	String getUrl() { 
		String url="http://${hostname}/${appName}/${chatPoint}/"
		if (!addAppName) {
			url="http://${hostname}/${chatPoint}/"
		}
		return url
	}
	
	String getUri() {
		String uri="ws://${hostname}/${appName}/${chatEndPoint}/"
		if (!addAppName) {
			uri="ws://${hostname}/${chatEndPoint}/"
		}
		return uri
	}

	String getCamEndpoint() {
		String camEndpoint="ws://${hostname}/${appName}/${camEndPoint}/"
		if (!addAppName) {
			camEndpoint="ws://${hostname}/${camEndPoint}/"
		}
		return camEndpoint
	}

	String getFileEndpoint() {
		String fileEndpoint="ws://${hostname}/${appName}/${fileEndPoint}/"
		if (!addAppName) {
			fileEndpoint="ws://${hostname}/${fileEndPoint}/"
		}
		return fileEndpoint
	}


	static constraints = {
		room(nullable:true)
		rooms(nullable:true)
		chatuser(nullable:true)
	}

	//worker for the boolean logic
	private Boolean validateBool(def input) {
		if (input) {
			if (input instanceof Boolean) {
				return input
			} else if (input){
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

	def getConf(String configProperty) {
		Holders.config[configProperty] ?: ''
	}

	def getConfig(String configProperty) {
		Holders.config.wschat[configProperty] ?: ''
	}
}
