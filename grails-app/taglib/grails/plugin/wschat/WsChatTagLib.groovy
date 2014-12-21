package grails.plugin.wschat

class WsChatTagLib  {

	static namespace  =  "chat"

	def grailsApplication

	def wsChatClientService

	def connect  =   { attrs ->

		def chatuser = attrs.remove('chatuser')?.toString()
		def room = attrs.remove('room')?.toString()
		def template = attrs.remove('template')?.toString()

		def chatTitle = config.title ?: 'Grails Websocket Chat'
		def chatHeader = config.heading ?: 'Grails websocket chat'
		def hostname = config.hostname ?: 'localhost:8080'
		def showtitle = config.showtitle ?: 'yes'
		def dbSupport = config.dbsupport ?: 'yes'

		chatuser = chatuser.trim().replace(' ', '_').replace('.', '_')
		session.wschatuser = chatuser

		if (!room) {
			room = returnRoom(dbSupport as String)
		}

		session.wschatroom = room

		def model=[dbsupport:dbSupport.toLowerCase() ,
			showtitle:showtitle.toLowerCase(),
			room:room, chatuser:chatuser, chatTitle:chatTitle,
			chatHeader:chatHeader, now:new Date(),
			hostname:hostname]

		if (template) {

			out << g.render(template:template, model:model)
		}else{
			out << g.render(contextPath: pluginContextPath, template : '/wsChat/chat', model: model)
		}
	}


	def clientConnect  =  { attrs ->
		def room = attrs.remove('room')?.toString()
		def hostname = attrs.remove('hostname')?.toString()
		def appName = attrs.remove('appName')?.toString()
		def user = attrs.remove('user')?.toString()
		def message = attrs.remove('message')?.toString()
		def receiver = attrs.remove('receiver')?.toString() ?: ''
		def actionMap = attrs.remove('actionMap')
		def strictMode = attrs.remove('strictMode')?.toBoolean() ?: false
		def autodisco = attrs.remove('autodisco')?.toBoolean() ?: false

		def aMap=[:]
		if (actionMap) {
			aMap=actionMap as Map
		}

		def dbSupport = config.dbsupport ?: 'yes'

		if (!appName) {
			appName = grailsApplication.metadata['app.name']
		}

		if (!hostname) {
			hostname = config.hostname ?: 'localhost:8080'
		}

		if (!room) {
			room = returnRoom(dbSupport as String)
		}

		if (!message) {
			message = "testing"
		}

		wsChatClientService.conn(hostname, appName, room, user)
		if (receiver) {
			if (strictMode==false) {
				wsChatClientService.sendMessage(message)
			}
			wsChatClientService.sendPM(receiver, message)
		} else {
			wsChatClientService.sendMessage(message)
		}

		if (autodisco) {
			wsChatClientService.disco(user)
		}else{
			wsChatClientService.handMessage(user, receiver, aMap, strictMode)
		}

	}


	private String returnRoom(String dbSupport) {
		def dbrooms
		def room = config.rooms[0]
		if (dbSupport.toLowerCase().equals('yes')) {
			dbrooms = ChatRoomList?.get(0)?.room
		}
		if (dbrooms) {
			room = dbrooms
		} else if (!room && !dbrooms) {
			room = 'wschat'
		}
		return room as String
	}

	private getConfig() {
		grailsApplication?.config?.wschat
	}

}
