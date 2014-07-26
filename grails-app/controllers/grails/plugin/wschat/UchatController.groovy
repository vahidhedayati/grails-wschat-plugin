package grails.plugin.wschat


class UchatController {
	def grailsApplication
	
	def index() { 
		def process=grailsApplication?.config?.wschat.disable.login ?: 'no'
		if (process.equals('yes')) {
			render "Default sign in page disabled"
		}
	}
	
	def login(String username) {
		def process=grailsApplication?.config?.wschat.disable.login ?: 'no'
		if (process.equals('yes')) {
			render "Default sign in page disabled"
		}
		session.username=username
		redirect(action: "chat")
	}
	
    def chat() { 
		def chatuser=session.username
		[chatuser:chatuser]
	}
}
