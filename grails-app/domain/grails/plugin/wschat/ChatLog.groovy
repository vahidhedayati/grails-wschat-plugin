package grails.plugin.wschat


class ChatLog {
	
	Date dateCreated
	Date lastUpdated
  
	static hasMany = [messages: ChatMessage]
  
	static constraints = {
	}
  
	static mapping = {
	  messages cascade: 'all-delete-orphan'
	}
  
	String getFormattedDateCreated() {
	  dateCreated.format("MM/dd/yyyy 'at' h:mma")
	}
  }