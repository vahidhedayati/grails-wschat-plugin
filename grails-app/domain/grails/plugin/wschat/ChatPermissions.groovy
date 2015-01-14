package grails.plugin.wschat

class ChatPermissions {

	Date dateCreated
	Date lastUpdated
	String name

	static hasMany=[ChatUser]

	static constraints = {
		name blank: false, unique: true
	}

}
