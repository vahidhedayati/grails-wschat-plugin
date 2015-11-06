package grails.plugin.wschat.beans

import grails.validation.Validateable

class ClientTagBean extends InitiationBean implements Validateable {
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

	// TODO --- a mess getter setting something
	// causing issue left out for now
	/*
	String getFrontuser() {
		frontuser=user+frontUser 
		receivers.add(frontuser)
	}
	*/
	void setReceivers(String input) {
		if (frontuser && user) {
			receivers << user+frontUser
		}
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
