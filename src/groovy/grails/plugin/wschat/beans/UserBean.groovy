package grails.plugin.wschat.beans


import grails.converters.JSON
import grails.validation.Validateable

@Validateable
class UserBean extends ConfigBean {

	//static final String defaultPerm = 'user'


	String user
	String rtc
	String chatuser

	String template
	String wschatjs
	String usermenujs

	def iceservers =getConf('stunServers')
	//JSON iceservers = grailsApplication.config.stunServers as JSON
	//= getConfig('iceservers') as ArrayList
	JSON getIceservers() { 
		if (iceservers) {
			return iceservers as JSON
		}
	}
	static constraints = {
		user(nullable: false, validator: validateInput)
		rtc(nullable:true)
		chatuser(nullable:true)
		iceservers(nullable:true)
		template(nullable:true)
		wschatjs(nullable:true)
		usermenujs(nullable:true)
	}
	
	String getUser() {
		if (user) {
			return user.trim().replace(' ', '_').replace('.', '_')
		}
	}

	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}
}
