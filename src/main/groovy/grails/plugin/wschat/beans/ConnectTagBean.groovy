package grails.plugin.wschat.beans

import grails.validation.Validateable

class ConnectTagBean extends InitiationBean implements Validateable {

	String template
	String wschatjs
	String usermenujs
	def updateProfile = false
	def profile

	public ConnectTagBean() {
		super
	}

	/*
	Boolean setAddLayouts(String addLayouts) {
		this.addLayouts = addLayouts
		return addLayouts
	}
	*/
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
