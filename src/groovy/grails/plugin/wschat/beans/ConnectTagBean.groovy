package grails.plugin.wschat.beans

import grails.validation.Validateable

@Validateable
class ConnectTagBean extends InitiationBean {

	String template
	String wschatjs
	String usermenujs
	def updateProfile = false
	def profile

	Boolean getUpdateProfile() {
		return validateBool(updateProfile)
	}

	Map getProfile() {
		if (profile) {
			return profile as Map
		}
	}
	
	static constraints = {
		chatuser(nullable: false)
		template(nullable:true)
		wschatjs(nullable:true)
		usermenujs(nullable:true)
		profile(nullable:true)
	}
}
