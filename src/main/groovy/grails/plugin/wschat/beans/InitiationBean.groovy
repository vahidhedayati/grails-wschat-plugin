package grails.plugin.wschat.beans

import grails.validation.Validateable

class InitiationBean extends ConfigBean implements Validateable {

	// set up as def just in case user inputs as string
	def jquery=true
	def jqueryui=true
	def bootstrap=true

	// now ensure it is returned as a boolean whatever the case
	Boolean getJquery() {
		return validateBool(jquery)
	}
	Boolean getJqueryui() {
		return validateBool(jqueryui)
	}
	Boolean getBootstrap() {
		return validateBool(bootstrap)
	}

}
