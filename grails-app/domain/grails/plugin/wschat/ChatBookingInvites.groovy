package grails.plugin.wschat

class ChatBookingInvites {
	
	Date dateCreated
	Date lastUpdated
	
	String name
	String emailAddress
	Boolean accepted = false
	
	static belongsTo = [ booking: ChatBooking]
	
	static constraints = {
		emailAddress nullable: true,email: true, notEqual: "bill@microsoft.com"
		name nullable:true
    }

	
}
