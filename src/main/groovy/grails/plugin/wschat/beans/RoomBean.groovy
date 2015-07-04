package grails.plugin.wschat.beans

import grails.validation.Validateable

class RoomBean extends ConfigBean implements Validateable {

	Boolean sender = false
	static constraints = {
		room(nullable: false, validator: validateInput)
	}
	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
}
