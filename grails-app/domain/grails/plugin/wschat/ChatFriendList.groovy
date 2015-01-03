package grails.plugin.wschat

class ChatFriendList {
	
	Date dateCreated
	Date lastUpdated

	String username

	
	static belongsTo=[chatuser:ChatUser]
	
    static constraints = {
		username blank: false
		//, unique: true
    }
	
}
