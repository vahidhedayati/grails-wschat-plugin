package grails.plugin.wschat

class ChatBooking {
	
	Date dateCreated
	Date lastUpdated
	
	String conferenceName
	
	static hasMany = [invites: ChatBookingInvites]
	
	String users
	Date dateTime
	Date endDateTime
	
	static constraints = {
		conferenceName blank:false
		masterUser blank: false
		dateTime blank: false
		endDateTime blank: false
		users nullable: true
		invites nullable: true
    }

	
}
