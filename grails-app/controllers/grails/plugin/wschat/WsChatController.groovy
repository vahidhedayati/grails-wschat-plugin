package grails.plugin.wschat

import groovy.time.TimeCategory

import java.text.SimpleDateFormat
import java.util.Date;


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
		def dbsupport=grailsApplication.config.wschat.dbsupport ?: 'yes'
		def chatuser=session.user
		[dbsupport:dbsupport.toLowerCase() , chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader, now:new Date(),hostname:hostname]
	}
	
	def verifyprofile(String username) {
		Boolean actualuser=false
		def chatuser=ChatUser.findByUsername(username)
		def profile=ChatUserProfile.findByChatuser(chatuser)
		def photos=ChatUserPics.findAllByChatuser(chatuser,[max: 5, sort: 'id', order:'desc'])
		if (username.equals(session.user)) {
			actualuser=true
		}
		render template: '/profile/verifyprofile', model:[photos:photos,actualuser:actualuser,username:username,profile:profile]		
	}

	def editprofile(String username) {
		def cc
		use(TimeCategory) {
			cc=new Date() -(5).years
	   }
	   def current = new SimpleDateFormat("dd/MM/yyyy").format(cc)
	   def chatuser=ChatUser.findByUsername(username)
	   def profile=ChatUserProfile.findByChatuser(chatuser)
		if (username.equals(session.user)) {
			render template: '/profile/editprofile', model:[profile:profile,chatuser:chatuser,current:current,username:username]
		}else{
			render "Not authorised!"
		}
	}

	def uploadPhoto(String username) {
		def chatuser=ChatUser.findByUsername(username)
		def profile=ChatUserProfile.findByChatuser(chatuser)
		def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		def photoFile= g.createLink(controller: 'wsChat', action: 'photo', params: [username:username],  absolute: 'true' )
		
		 if (username.equals(session.user)) {
			 render template: '/profile/addphoto', model:[photoFile:photoFile,profile:profile,chatuser:chatuser,username:username]
		 }else{
			 render "Not authorised!"
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
		if (params.chatuser) {
				def newRecord=new ChatUserPics(params)
				if (!newRecord.save(flush:true)) {
					flash.message="Something has gone wrong, could not upload photo"
				}
				flash.message="Record  ${newRecord.id} created. Create anoter?"
				photo(params.username)
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
				/*exists.firstName=params.firstName
				exists.middleName=params.middleName
				exists.lastName=params.lastName
				exists.age=params.age
				exists.birthDate=params.birthDate
				exists.gender=params.gender
				exists.wage=params.wage
				exists.email=params.email
				exists.homePage=params.homePage
				exists.children=params.children*/
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
	
}
