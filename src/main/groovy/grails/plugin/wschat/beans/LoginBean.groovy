package grails.plugin.wschat.beans

import grails.validation.Validateable

class LoginBean extends ConfigBean implements Validateable {

	String username
	//String room
	static constraints = {
		username(nullable: false, validator: validateInput)
		room(nullable: false, validator: validateInput)
	}

	String getUsername() {
		if (username) {
			return username.trim().replace(' ', '_').replace('.', '_')
		}
	}
	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
}
