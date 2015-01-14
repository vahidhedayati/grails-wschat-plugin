package grails.plugin.wschat

class ChatAuthLogs {

	Date dateCreated
	Date lastUpdated
	String username
	Boolean loggedIn = false
	Boolean loggedOut = false

	static constraints = {
		username blank: false
	}

}
