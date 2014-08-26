package grails.plugin.wschat

class ChatLogs {
	
	Date dateCreated
	Date lastUpdated
	String username
	Boolean loggedIn = false
	Boolean loggedOut = false
	
    static constraints = {
		username blank: false
    }
	

}
