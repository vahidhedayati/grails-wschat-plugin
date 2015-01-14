package grails.plugin.wschat

class ChatBlockList {
	
	Date dateCreated
	Date lastUpdated
	
	String username
	static belongsTo=[chatuser:ChatUser]
	
    static constraints = {
		username blank: false
    }

}
