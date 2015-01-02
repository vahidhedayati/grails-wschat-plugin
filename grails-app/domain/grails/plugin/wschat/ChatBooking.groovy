package grails.plugin.wschat

class ChatBooking {
	
	Date dateCreated
	Date lastUpdated
	
	String conferenceName
	
	static hasMany = [invites: ChatBookingInvites]
	
	Date dateTime
	Date endDateTime
	
	static constraints = {
		conferenceName blank:false
		dateTime blank: false
		endDateTime blank: false
		invites nullable: true
    }

	
}
