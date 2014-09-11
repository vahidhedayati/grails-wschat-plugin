package grails.plugin.wschat

class WsChatTagLib {
   static namespace = "chat"
   def grailsApplication
   
   def connect =  { attrs, body ->
	   def chatuser=attrs.remove('chatuser')?.toString()
	   def room=attrs.remove('room')?.toString()
	   def template=attrs.remove('template')?.toString()
	   def chatTitle=grailsApplication?.config?.wschat.title ?: 'Grails Websocket Chat'
	   def chatHeader=grailsApplication?.config?.wschat.heading ?: 'Grails websocket chat'
	   def hostname=grailsApplication?.config?.wschat.hostname ?: 'localhost:8080'
	   def showtitle=grailsApplication.config.wschat.showtitle ?: 'yes' 
	   def dbSupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
	   chatuser=chatuser.trim().replace(' ', '_').replace('.', '_')
	   session.wschatuser=chatuser
	   if (!room) {
		   def dbrooms
		   room=grailsApplication.config.wschat.rooms[0]
		   if (dbSupport.toString().toLowerCase().equals('yes')) {
			   dbrooms=ChatRoomList?.get(0)?.room
		   }
		   if (dbrooms) {
			   room=dbrooms
		   } else if (!room && !dbrooms) {
		   	room='wschat'
		   }
	   } 
	   session.wschatroom=room
	   if (template) {
		   out << g.render(template:template, model: [dbsupport:dbSupport.toLowerCase() , showtitle:showtitle.toLowerCase(), room:room, chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(),hostname:hostname])
	   }else{   
	   		out << g.render(contextPath: pluginContextPath, template : '/wsChat/chat', model: [dbsupport:dbSupport.toLowerCase() , showtitle:showtitle.toLowerCase(), room:room, chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(),hostname:hostname])
	   }
   }
}
