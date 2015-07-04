package grails.plugin.wschat.beans

import grails.util.Holders
import grails.validation.Validateable

@Validateable
class ConfigBean {

	//Declare end points
	static final String chatEndPoint = 'WsChatEndpoint'
	static final String camEndPoint = 'WsCamEndpoint'
	static final String fileEndPoint = 'WsChatFileEndpoint'
	final String hostname = getConfig('hostname') ?: 'localhost:8080'
	final def addAppName = getConfig('add.appName') ?: true
	def dbSupport = getConfig('dbsupport') ?: true
	final def showtitle = getConfig('showtitle') ?: true
	final def debug = getConfig('debug ')?: false
	final process = getConfig('disable.login')?:false
	final String chatTitle = getConfig('title') ?: 'Grails Websocket Chat'
	final String chatHeader = getConfig('heading') ?: 'Grails websocket chat'
	String frontUser = getConfig('frontend') ?: '_frontend'
	static final Date now = new Date()
	
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
	Boolean getProcess() { 
		return validateBool(process)
	}
	Boolean getAddLayouts() {
		return validateBool(addLayouts)
	}
	Boolean getDbSupport() {
		return validateBool(dbSupport)
	}

	Boolean getDebug() {
		return validateBool(debug)
	}

	Boolean getShowtitle() {
		return validateBool(showtitle)
	}
	
	String getAppName() {
		String appName= grails.util.Metadata.current.applicationName ?: Holders.grailsApplication.metadata['app.name']
		return appName
	}
	
	Boolean getAddAppName() {
		return validateBool(addAppName)
	}

	def getUri() {
		String uri="ws://${hostname}/${appName}/${chatEndPoint}/"
		if (!addAppName) {
			uri="ws://${hostname}/${chatEndPoint}/"
		}
		return uri
	}

	def getCamEndpoint() {
		String camEndpoint="ws://${hostname}/${appName}/${camEndPoint}/"
		if (!addAppName) {
			camEndpoint="ws://${hostname}/${camEndPoint}/"
		}
		return camEndpoint
	}

	def getFileEndpoint() {
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
		if (input instanceof Boolean) {
			return input
		}else{
			return input.toBoolean()
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
