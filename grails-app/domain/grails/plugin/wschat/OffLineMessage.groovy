package grails.plugin.wschat

class OffLineMessage {
	
	ChatLog offlog
	String user
	String contents
	Boolean readMsg = false
	Date dateCreated
  
	static constraints = {
	  user nullable: true
	}
  
	static mapping = {
	  contents type: 'text'
	}
  
	String toString() {
	  if (user) {
		return "$user: $contents"
	  }
	  return contents
	}
  }
  