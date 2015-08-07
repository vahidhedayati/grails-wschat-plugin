package grails.plugin.wschat.beans

import java.util.Date;
import java.util.List;

import grails.converters.JSON
import grails.validation.Validateable


class CustomerChatTagBean extends ClientTagBean  implements Validateable {

	String name
	//String username
	String emailAddress
	String roomName
	String controller
	String action
	String params
	final String assistant = getConfig('liveChatAssistant') ?: 'assistant'
	
	Date startTime
	Boolean guestUser=true
	Boolean active=true
	
	static constraints = {
		name(nullable:true)
		//username(nullable:true)
		emailAddress(nullable:true)
		roomName(nullable:true)
		controller(nullable:true)
		action(nullable:true)
		params(nullable:true)
		active(nullable:true)
	}
}
