package grails.plugin.wschat.beans

import grails.validation.Validateable

class SignupBean implements Validateable {

	String username
	String password
	String password2
	String email

	//String room
	static constraints = {
		username(blank: false, minSize:4,  maxSize:15, validator: validateInput)
		password(blank: false,  minSize:3,  maxSize:15, validator: validatePassword)
		email(email:true, blank:false)
	}

	static def validateInput={value,object,errors->
		if (!value || value.indexOf(' ')>-1) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}

	static def validatePassword={value,object,errors->
		if (!value|| object.password2!=value) {
			return errors.rejectValue(propertyName, "invalid.$propertyName", [''] as Object[], '')
			return errors.rejectValue('password2', "invalid.password2", [''] as Object[], '')
		}
	}

}
