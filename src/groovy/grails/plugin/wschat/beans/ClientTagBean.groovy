package grails.plugin.wschat.beans

import grails.validation.Validateable

@Validateable
class ClientTagBean extends InitiationBean {
	String room
	Map actionMap
	ArrayList receivers 
	Boolean strictMode = false
	Boolean autodisco = false
	Boolean masterNode = false
	Boolean frontenduser = false
	
	
	
	String message = "testing"
	String divId 
	String template

	String user
	String frontuser
	String getFrontuser() {
		frontuser=user+frontUser 
		receivers.add(frontuser)
	}

	Boolean getStrictMode() {
		return validateBool(strictMode)
	}
	Boolean getAutodisco() {
		return validateBool(autodisco)
	}
	Boolean getMasterNode() {
		return validateBool(masterNode)
	}
	Boolean getFrontenduser() {
		return validateBool(frontenduser)
	}

	static constraints = {
		user(nullable: false)
		receivers(nullable:true)
		divId(nullable:true)
		room(nullable:true)
		template(nullable:true)
		actionMap(nullable:true)
	}
}
