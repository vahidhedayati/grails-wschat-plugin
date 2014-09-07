package grails.plugin.wschat

class ChatRoomList {
	Date dateCreated
	Date lastUpdated
	String room
    static constraints = {
		room blank: false, unique: true
    }

}
