package grails.plugin.wschat.beans

import grails.converters.JSON
import grails.validation.Validateable

class WsConnectTagBean extends ClientTagBean implements Validateable {

	def jsonData
	String sendType = 'message'
	String event 
	String context
	
	JSON getJsonData() { 
		if (jsonData) {
			if(jsonData instanceof String) {
				jsonData =JSON.parse(jsonData)
			}else{
				jsonData = jsonData as JSON
			}
			return jsonData
		}		
	}
	
	static constraints = {
		context(nullable:true)
		event(nullable:true)
		jsonData(nullable:true)
	}
}
