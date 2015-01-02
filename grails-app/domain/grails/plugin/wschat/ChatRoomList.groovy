package grails.plugin.wschat

class ChatRoomList {
	Date dateCreated
	Date lastUpdated
	String room
	String roomType = 'chat'
	
	//static mapping = { 	
	//	roomType defaultValue: 'chat'
	//}	
    static constraints = {
		room blank: false, unique: true
		roomType nullable: true
		//, inList: ["chat", "booking"]
    }

}
