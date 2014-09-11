package grails.plugin.wschat

import groovy.time.TimeCategory

import java.text.SimpleDateFormat


class WsChatController {
	def grailsApplication

	def index() {
		
		def dbSupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
		def dbrooms
		if (dbSupport.toString().toLowerCase().equals('yes')) {
			dbrooms=ChatRoomList?.findAll()*.room.unique()
		}	
		def process=grailsApplication.config.wschat.disable.login ?: 'no'
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication.config.wschat.heading ?: 'Grails websocket chat'
		def room=grailsApplication.config.wschat.rooms
		if (!room && dbrooms) {
			room=dbrooms
		} else if (!room && !dbrooms) {
			room=['wschat']
		}	    
		
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		[chatTitle:chatTitle,chatHeader:chatHeader,room:room]
	}

	def login(String username,String room) {
		def errors
		def process=grailsApplication.config.wschat.disable.login ?: 'no'
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		username=username.trim().replace(' ', '_').replace('.', '_')
		if (errors) {
			flash.message=errors
			redirect(controller: "wsChat",action: "index")
		}else{
			session.wschatuser=username
			session.wschatroom=room
			redirect(controller: "wsChat", action: "chat")
		}
		//redirect (uri : "/wsChat/chat/${room}")
	}
	

	def camsend(String user) {
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def hostname=grailsApplication.config.wschat.hostname ?: 'localhost:8080'
		//def dbsupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
		[user:user,chatTitle:chatTitle,hostname:hostname]
	}

	
	def camrec(String user) {
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def hostname=grailsApplication.config.wschat.hostname ?: 'localhost:8080'
		def chatuser=session.wschatuser
		//def dbsupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
		[user:user,hostname:hostname,chatuser:chatuser,chatTitle:chatTitle]
	}

	def chat() {
		def chatTitle=grailsApplication.config.wschat.title ?: 'Grails Websocket Chat'
		def chatHeader=grailsApplication.config.wschat.heading ?: 'Grails websocket chat'
		def hostname=grailsApplication.config.wschat.hostname ?: 'localhost:8080'
		def dbsupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
		def showtitle=grailsApplication.config.wschat.showtitle ?: 'yes'
		def chatuser=session.wschatuser
		def room=session.wschatroom
		[showtitle:showtitle.toLowerCase(), dbsupport:dbsupport.toLowerCase() , room:room, chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(),hostname:hostname]
	}

	def verifyprofile(String username) {
		Boolean actualuser=false
		def chatuser=ChatUser.findByUsername(username)
		def profile=ChatUserProfile.findByChatuser(chatuser)
		def photos=ChatUserPics.findAllByChatuser(chatuser,[max: 5, sort: 'id', order:'desc'])
		if (verifyUser(username)) {
			actualuser=true
		}
		render template: '/profile/verifyprofile', model:[photos:photos,actualuser:actualuser,username:username,profile:profile]
	}

	def editprofile(String username) {
		def chatuser=ChatUser.findByUsername(username)
		def profile=ChatUserProfile.findByChatuser(chatuser)
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy")
		if (verifyUser(username)) {
			def bdate=profile?.birthDate
			def cdate
			if (bdate) {
				cdate = formatter.format(bdate)
			}else{
				Date cc=new Date()
				cc.clearTime()
				use(TimeCategory) {
					cc=cc -5.years
				}
				cdate=formatter.format(cc)
			}
			render template: '/profile/editprofile', model:[cdate:cdate,profile:profile,chatuser:chatuser,username:username]
		}else{
			render "Not authorised!"
		}
	}

	def uploadPhoto(String username) {
		def chatuser=ChatUser.findByUsername(username)
		def profile=ChatUserProfile.findByChatuser(chatuser)
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		def photoFile= g.createLink(controller: 'wsChat', action: 'photo', params: [username:username],  absolute: 'true' )

		if (verifyUser(username)) {
			render template: '/profile/addphoto', model:[photoFile:photoFile,profile:profile,chatuser:chatuser,username:username]
		}else{
			render "Not authorised!"
		}
	}

	def addaRoom() {
		
		render template : '/room/addaRoom' 
	}

	def delaRoom() {
		def roomList=ChatRoomList?.findAll()*.room.unique()
		render template : '/room/delaRoom' , model:[ roomList:roomList ]
	}

	def addRoom(String room) { 
		def record=ChatRoomList.findByRoom(room)
		if (!record) {
			record=new ChatRoomList()
			record.room=room
			if (!record.save(flush:true)) {
				render "Issue saving new room"
			}
			render "New room has been added"
		}
		render "Room ${room} already added"
	}
	
	def delRoom(String room) {
		def record=ChatRoomList.findByRoom(room)
		if (!record) {
			render "Room ${room} not found"
		}else{
			record.delete(flush:true)
			render "Room has been deleted"
		}
	}
	
	def photo(String username) {
		render template : '/profile/photo' , model:[ username:username ]
	}

	def realPhoto(String photoId) {
		render template : '/profile/realPhoto' , model:[ photoId:photoId ]
	}

	def addPhoto() {
		params.chatuser=ChatUser.findByUsername(params.username)
		if ((params.chatuser)&&(params.photo)) {
			def newRecord=new ChatUserPics(params)
			if (!newRecord.save(flush:true)) {
				flash.message="Something has gone wrong, could not upload photo"
			}
			flash.message="Record  ${newRecord.id} created. Create another?"
			photo(params.username)
		}else{
			flash.message="Could not upload photo was a photo selected?"
		}
	}

	def viewPic(Long id) {
		def photo = ChatUserPics.get( params.id )
		byte[] image = photo.photo
		response.outputStream << image
	}

	def updateProfile() {
		String output
		if (params.birthDate) {
			params.birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(params.birthDate)
		}
		params.chatuser=ChatUser.findByUsername(params.username)
		if (params.chatuser) {
			def exists=ChatUserProfile.findByChatuser(params.chatuser)
			if (!exists) {
				def newRecord=new ChatUserProfile(params)
				if (!newRecord.save(flush:true)) {
					output="Something has gone wrong"
				}
			}else{
				exists.properties = params
				if (!exists.save(flush:true)) {
					output="Something has gone wrong"
				}
			}
		}
		if (!output) {
			output="Infromation has been updated"
		}
		render output
	}

	def confirmBan(String username,String duration,String period) {
		[username:username,duration:duration,period:period]
	}

	private Boolean verifyUser(String username) {
		Boolean userChecksOut=false
		def chatuser=ChatUser.findByUsername(username)
		if ((chatuser) && (username.equals(session.wschatuser))) {
			userChecksOut=true
		}
		return userChecksOut
	}
}
