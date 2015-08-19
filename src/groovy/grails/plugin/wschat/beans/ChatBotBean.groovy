package grails.plugin.wschat.beans

import grails.plugin.wschat.ChatCustomerBooking
import grails.plugin.wschat.ChatUser

class ChatBotBean {
	String username
	ChatCustomerBooking customer
	ChatUser chatuser
	Boolean adminVerified=false
	Boolean isLiveAdmin=false
	Boolean isChatAdmin=false
	Boolean isConfigLiveAdmin=false
	Boolean emailSent=true
	
	
	static constraints = {
		customer(nullable:true)
		chatuser(nullable:true)	
	}
}
