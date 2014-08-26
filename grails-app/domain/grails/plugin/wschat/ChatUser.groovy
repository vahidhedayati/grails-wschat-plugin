package grails.plugin.wschat

class ChatUser {
	
	Date dateCreated
	Date lastUpdated
	String username
	ChatPermissions permissions
	
	static hasMany = [friends: ChatFriendList, blocked:ChatBlockList ]
	
	static mapping = {
		permissions lazy: false
	}
	
	static constraints = {
		username blank: false, unique: true
		friends nullable:true
		blocked nullable:true
    }

	
}
