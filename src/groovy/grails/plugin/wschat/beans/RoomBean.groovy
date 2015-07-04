package grails.plugin.wschat.beans

import grails.validation.Validateable

@Validateable
class RoomBean extends ConfigBean {

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
