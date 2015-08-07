package grails.plugin.wschat


/*
 * @author: Vahid Hedayati
 * used for chat to a person through website solution
 */
class ChatCustomerBooking {

	String name
	String username
	String emailAddress
	String roomName
	String controller
	String action
	//String params

	Date startTime
	Boolean active=true
	Boolean guestUser=true
	Date lastUpdated
	Date dateCreated

	def getActive1() {
		//Date now = new Date( 1280512800L * 1000 )
		Date now = new Date()
		if ((now.toTimeStamp() - lastUpdated.toTimestamp()) > 10000  ) {
			active=false
		}
	}

	static constraints = {
		name(nullable:true)
		emailAddress(nullable:true, email:true)
		active(nullable:true)
		roomName(nullable:true)
		controller(nullable:true)
		action(nullable:true)
		//params(nullable:true)
	}
}
