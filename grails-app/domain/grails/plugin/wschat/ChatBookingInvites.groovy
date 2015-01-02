package grails.plugin.wschat

class ChatBookingInvites {
	
	Date dateCreated
	Date lastUpdated
	
	String username
	String emailAddress
	String token
	
	Boolean accepted = false
	
	static belongsTo = [ booking: ChatBooking]
	
	static constraints = {
		emailAddress blank: false,email: true, notEqual: "bill@microsoft.com"
		username blank: false
    }

	
}
