package grails.plugin.wschat


class WsChatController {
	def grailsApplication
	
	def index() { 
		def process=grailsApplication?.config?.wschat.disable.login ?: 'no'
		def chatTitle=grailsApplication?.config?.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication?.config?.wschat.heading ?: 'Grails websocket chat'
		if (process.equals('yes')) {
			render "Default sign in page disabled"
		}
		[chatTitle:chatTitle,chatHeader:chatHeader]
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
		def chatTitle=grailsApplication?.config?.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication?.config?.wschat.heading ?: 'Grails websocket chat'
		def chatuser=session.username
		[chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date()]
	}
}
