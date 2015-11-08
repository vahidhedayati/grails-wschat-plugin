package grails.plugin.wschat

class ChatRoomList {
	static final String DEFAULT_ROOM_TYPE = 'chat'
	static final List DEFAULT_ROOM = ['wschat']
	
	Date dateCreated
	Date lastUpdated
	String room
	String roomType =  DEFAULT_ROOM_TYPE
	

    static constraints = {
		room blank: false, unique: true
		roomType nullable: true
		//, inList: ["chat", "booking"]
    }

}
