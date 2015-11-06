package grails.plugin.wschat.beans

import grails.converters.JSON
import grails.validation.Validateable

@Validateable
class LiveChatBean extends ConnectTagBean {

	def livechat = true

	//JSON activeLCTitle=["background-colour":"#FF0000","background":"#c00","color":"white"] as JSON
	//JSON activeLCBody=["background":"#FFF","color":"#000"] as JSON

	
	Boolean getLivechat() {
		return validateBool(livechat)
	}

	
	static constraints = {
		livechat(nullable:true)
		//activeLCTitle(nullable:true)
		//activeLCBody(nullable:true)
	}
	/*
	void setActiveLCTitle(Map input) {
		if (input){
			activeLCTitle=input as JSON
		}
	}
	void setActiveLCBody(Map input) {
		if (input){
			activeLCBody=input as JSON
		}
	}
	
	String getActiveLCBody() {
		return activeLCBody as String
	}
	String getActiveLCTitle() {
		return activeLCTitle as String
	}
	*/
	
}
