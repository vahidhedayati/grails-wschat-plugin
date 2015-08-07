package grails.plugin.wschat.beans

import grails.validation.Validateable

@Validateable
class LiveChatBean extends ConnectTagBean {

	def livechat = true
	
	Boolean getLivechat() {
		return validateBool(livechat)
	}

	
	static constraints = {
		livechat(nullable:true)
	}
}
