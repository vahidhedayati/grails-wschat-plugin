package grails.plugin.wschat

import grails.converters.JSON
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.codehaus.groovy.grails.web.json.JSONObject


class WsChatController {
	def grailsApplication
	def wsChatRoomService
	
	def index() {
		
		String dbSupport = config.dbsupport ?: 'yes'
		//def dbrooms
		//if (dbSupport.toString().toLowerCase().equals('yes')) {
		//	dbrooms = ChatRoomList?.findAll()*.room.unique()
		//}
			
		String process = config.disable.login ?: 'no'
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'
		def room = config.rooms
		if (!room && (dbSupport=='yes')) {
			//room = dbrooms
			room = wsChatRoomService.returnRoom(dbSupport as String)
		} else if (!room && (dbSupport=='no')) {
			room = ['wschat']
		}	    
		
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		[chatTitle:chatTitle,chatHeader:chatHeader,room:room]
	}

	def login(String username,String room) {
		String errors
		String process = config.disable.login ?: 'no'
		if (process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}
		username = username.trim().replace(' ', '_').replace('.', '_')
		if (errors) {
			flash.message = errors
			redirect(controller: "wsChat",action: "index")
		}else{
			session.wschatuser = username
			session.wschatroom = room
			redirect(controller: "wsChat", action: "chat")
		}
		//redirect (uri : "/wsChat/chat/${room}")
	}
	

	def camsend(String user) {
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String addAppName = config.add.appName ?: 'yes'
		[user:user, chatTitle:chatTitle, hostname:hostname, addAppName:addAppName]
	}

	def webrtcsend(String user) {
		JSONObject iceservers = grailsApplication.config.stunServers as JSON
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String addAppName = config.add.appName ?: 'yes'
		[user:user, chatTitle:chatTitle, hostname:hostname, iceservers:iceservers, addAppName:addAppName]
	}
	
	def webrtcrec(String user) {
		JSONObject iceservers = grailsApplication.config.stunServers as JSON
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String addAppName = config.add.appName ?: 'yes'
		def chatuser = session.wschatuser
		[user:user, hostname:hostname, chatuser:chatuser, chatTitle:chatTitle,
			iceservers:iceservers, addAppName:addAppName]
	}
	
	
	def camrec(String user) {
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String hostname = config.hostname ?: 'localhost:8080'
		def chatuser = session.wschatuser
		String addAppName = config.add.appName ?: 'yes'
		[user:user, hostname:hostname, chatuser:chatuser, chatTitle:chatTitle, addAppName:addAppName]
	}

	def chat() {
		String chatTitle = config.title ?: 'Grails Websocket Chat'
		String chatHeader = config.heading ?: 'Grails websocket chat'
		String hostname = config.hostname ?: 'localhost:8080'
		String dbsupport = config.dbsupport ?: 'yes'
		String showtitle = config.showtitle ?: 'yes'
		def chatuser = session.wschatuser
		def room = session.wschatroom
		String dbSupport = config.dbsupport ?: 'yes'
		if (!room) {
			room = wsChatRoomService.returnRoom(dbSupport as String)
		}
		String addAppName = config.add.appName ?: 'yes'
		
		[showtitle:showtitle.toLowerCase(), dbsupport:dbsupport.toLowerCase() , room:room,
		chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(), hostname:hostname]
	}

	def verifyprofile(String username) {
		Boolean actualuser = false
		ChatUser chatuser = ChatUser.findByUsername(username)
		ChatUserProfile profile = ChatUserProfile.findByChatuser(chatuser)
		ChatUserPics photos = ChatUserPics.findAllByChatuser(chatuser,[max: 5, sort: 'id', order:'desc'])
		def model = [photos:photos,actualuser:actualuser,username:username,profile:profile]
		if (verifyUser(username)) {
			actualuser = true
		}
		render template: '/profile/verifyprofile', model:model
	}

	def editprofile(String username) {
		ChatUser chatuser = ChatUser.findByUsername(username)
		ChatUserProfile profile = ChatUserProfile.findByChatuser(chatuser)
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy")
		if (verifyUser(username)) {
			def bdate = profile?.birthDate
			def cdate
			if (bdate) {
				cdate = formatter.format(bdate)
			}else{
				Date cc = new Date()
				cc.clearTime()
				use(TimeCategory) {
					cc = cc -5.years
				}
				cdate = formatter.format(cc)
			}
			def model = [cdate:cdate,profile:profile,chatuser:chatuser,username:username]
			render template: '/profile/editprofile', model:model
		}else{
			render "Not authorised!"
		}
	}

	def uploadPhoto(String username) {
		ChatUser chatuser = ChatUser.findByUsername(username)
		ChatUserProfile profile = ChatUserProfile.findByChatuser(chatuser)
		ApplicationTagLib g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		def photoFile= g.createLink(controller: 'wsChat', action: 'photo', params: [username:username],  absolute: 'true' )
		def model = [photoFile:photoFile,profile:profile,chatuser:chatuser,username:username]
		if (verifyUser(username)) {
			render template: '/profile/addphoto', model:model
		}else{
			render "Not authorised!"
		}
	}

	def addaRoom() {
		
		render template : '/room/addaRoom' 
	}

	def delaRoom() {
		def roomList = ChatRoomList?.findAll()*.room.unique()
		render template : '/room/delaRoom' , model:[ roomList:roomList ]
	}

	def addRoom(String room) { 
		def record = ChatRoomList.findByRoom(room)
		if (!record) {
			record = new ChatRoomList()
			record.room = room
			if (!record.save(flush:true)) {
				render "Issue saving new room"
			}
			render "New room has been added"
		}
		render "Room ${room} already added"
	}
	
	def delRoom(String room) {
		def record = ChatRoomList.findByRoom(room)
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
		params.chatuser = ChatUser.findByUsername(params.username)
		if ((params.chatuser)&&(params.photo)) {
			def newRecord = new ChatUserPics(params)
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
		params.chatuser = ChatUser.findByUsername(params.username)
		if (params.chatuser) {
			def exists = ChatUserProfile.findByChatuser(params.chatuser)
			if (!exists) {
				def newRecord = new ChatUserProfile(params)
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
		Boolean userChecksOut = false
		def chatuser = ChatUser.findByUsername(username)
		if ((chatuser) && (username.equals(session.wschatuser))) {
			userChecksOut = true
		}
		return userChecksOut
	}
	
	private getConfig() {
		grailsApplication?.config?.wschat
	}
}
