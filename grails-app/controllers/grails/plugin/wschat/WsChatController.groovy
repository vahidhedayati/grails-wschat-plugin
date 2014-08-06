package grails.plugin.wschat


class WsChatController {
	def grailsApplication
	
	def index() { 
		def process=grailsApplication.config.wschat.disable.login ?: 'no'
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication.config.wschat.heading ?: 'Grails websocket chat'
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		[chatTitle:chatTitle,chatHeader:chatHeader]
	}
	
	def login(String username) {
		def process=grailsApplication.config.wschat.disable.login ?: 'no'
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		session.user=username
		redirect(action: "chat")
		//redirect (uri : "/wsChat/chat/${room}")
	}
	
    def chat() { 
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication.config.wschat.heading ?: 'Grails websocket chat'
		def hostname=grailsApplication.config.wschat.hostname ?: 'localhost:8080'
		def chatuser=session.user
		[chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(),hostname:hostname]
	}
}
