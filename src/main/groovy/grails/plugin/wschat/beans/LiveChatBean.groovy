package grails.plugin.wschat.beans

import grails.validation.Validateable


class LiveChatBean extends ConnectTagBean implements Validateable {

	def livechat = true
	
	Boolean getLivechat() {
		return validateBool(livechat)
	}

	
	static constraints = {
		livechat(nullable:true)
	}
}
