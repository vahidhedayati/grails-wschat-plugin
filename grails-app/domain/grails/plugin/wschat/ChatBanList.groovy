package grails.plugin.wschat

class ChatBanList {
	
	Date dateCreated
	Date lastUpdated
	
	String username
	
	String period
	
    static constraints = {
		username blank: false, unique: true
    }

}
