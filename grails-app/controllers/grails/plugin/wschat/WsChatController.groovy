package grails.plugin.wschat

import grails.converters.JSON
import groovy.time.TimeCategory

import java.text.SimpleDateFormat
import java.util.Map;

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib


class WsChatController extends WsChatConfService {

	def wsChatRoomService
	def autoCompleteService
	def wsChatUserService
	def wsChatProfileService
	def wsChatBookingService
	def wsChatContService

	
	def sendfile(String room) {
		def chatuser = session.wschatuser
		boolean sender = false
		if (room == chatuser) {
			sender = true
		}
		[sender:sender, room:room, hostname:wsconf.hostname, chatuser:chatuser, chatTitle:wsconf.chatTitle,
			addAppName:wsconf.addAppName]
	}

	def index() {
		def room = config.rooms
		if (!room && (wsconf.dbSupport=='yes')) {
			room = wsChatRoomService.returnRoom(wsconf.dbSupport as String)
		} else if (!room && (wsconf.dbSupport=='no')) {
			room = ['wschat']
		}
		if (wsconf.process.toLowerCase().equals('yes')) {
			render "Default sign in page disabled"
		}

		[chatTitle:wsconf.chatTitle,chatHeader:wsconf.chatHeader,room:room]
	}

	def login(String username,String room) {
		String errors
		if (wsconf.process.toLowerCase().equals('yes')) {
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

	def chat() {
		def chatuser = session.wschatuser
		def room = session.wschatroom
		String debug = config.debug ?: 'off'
		if (!room) {
			room = wsChatRoomService.returnRoom(wsconf.dbSupport as String)
		}
		[showtitle:wsconf.showtitle.toLowerCase(), dbsupport:wsconf.dbSupport.toLowerCase() , room:room,
			chatuser:chatuser, chatTitle:wsconf.chatTitle,chatHeader:wsconf.chatHeader,
			now:new Date(), hostname:wsconf.hostname, addAppName: wsconf.addAppName, debug:debug]
	}

	def verifyprofile(String username) {
		Map vp = wsChatContService.verifyProfile(username)
		def photos = vp.photos
		def actualuser = vp.actualuser
		def profile = vp.profile
		def model = [photos:photos,actualuser:actualuser,username:username,profile:profile]
		render template: '/profile/verifyprofile', model:model
	}

	def editprofile(String username) {
		def ep = wsChatContService.editProfile(username)
		def cdate = ep.cdate
		def chatuser = ep.chatuser
		def profile = ep.profile
		if (cdate) {
			def model = [cdate:cdate,profile:profile,chatuser:chatuser,username:username]
			render template: '/profile/editprofile', model:model
		}else{
			render "Not authorised!"
		}
	}

	def uploadPhoto(String username) {
		def chatuser = ChatUser.findByUsername(username)
		def profile = ChatUserProfile.findByChatuser(chatuser)
		ApplicationTagLib g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()
		def photoFile= g.createLink(controller: 'wsChat', action: 'photo', params: [username:username],  absolute: 'true' )
		def model = [photoFile:photoFile,profile:profile,chatuser:chatuser,username:username]
		if (wsChatContService.verifyUser(username)) {
			render template: '/profile/addphoto', model:model
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
		String photoRes = wsChatContService.addPhoto(params)
		if (photoRes) {
			flash.message="${photoRes}"
		}
		photo(params.username)
	}

	def viewPic(Long picId) {
		def photo = ChatUserPics.get( picId ?: params.id)
		if (photo) {
			byte[] image = photo.photo
			response.outputStream << image
		}
	}

	def updateProfile() {
		String output = wsChatContService.updateProfile(params)
		render output
	}

	def confirmBan(String username,String duration,String period) {
		if (isAdmin) {
			[username:username,duration:duration,period:period]
		}
	}

	def camsend(String user) {
		[user:user, chatTitle:wsconf.chatTitle, hostname:wsconf.hostname, addAppName:wsconf.addAppName]
	}

	def webrtcsend(String user, String rtc) {
		[user:user, chatTitle:wsconf.chatTitle, hostname:wsconf.hostname, iceservers:wsconf.iceservers,
			addAppName:wsconf.addAppName, rtc:rtc]
	}

	def webrtcrec(String user, String rtc) {
		def chatuser = session.wschatuser
		[user:user, hostname:wsconf.hostname, chatuser:chatuser, chatTitle:wsconf.chatTitle,
			iceservers:wsconf.iceservers, addAppName:wsconf.addAppName, rtc:rtc]
	}

	def camrec(String user) {
		def chatuser = session.wschatuser
		[user:user, hostname:wsconf.hostname, chatuser:chatuser, chatTitle:wsconf.chatTitle,
			addAppName:wsconf.addAppName]
	}

	def autocomplete() {
		render autoCompleteService.autocomplete(params)
	}


	def viewUsers(Integer max, String s) {
		if (isAdmin) {
			params.order = params.order ?: 'desc'
			int total = 0
			String pageSizes = params.pageSizes ?: '10'
			String order = params.order ?: "desc"
			String sortby = params.sortby ?: "lastUpdated"
			int offset = (params.offset ?: '0') as int
			def inputid = params.id
			params.max = Math.min(max ?: 10, 1000)
			def uList = wsChatUserService.genAllUsers()
			Map vu = wsChatContService.viewUsers(s ?: '', sortby, order, offset, params.max , inputid)
			s = vu.s
			def foundRec = vu.foundRec
			total = vu.total
			def model = [userList: foundRec, userListCount: total, divupdate: 'adminsContainer',
				pageSizes: pageSizes, offset: offset,inputid: inputid, s: s, order: order,
				sortby:sortby, action: 'list', allcat:ChatUser.list(), max:max, params:params, uList:uList]
			if (request.xhr) {
				render (template: '/admin/viewUsers', model: model)
			}
			else {
				render (view: '/admin/viewUsers', model: model)
			}
		}
	}

	def search(String mq) {
		if (isAdmin) {
			Map ss = wsChatUserService.search(mq)
			render (template: '/admin/userList', model: [ userList: ss.userList,uList:ss.uList])
		}
	}

	def findUser(String uid) {
		if (isAdmin) {
			def returnResult = wsChatUserService.findaUser(uid)
			render returnResult as JSON
		}
	}

	def addUser(String username) {
		if (isAdmin) {
			render (template: '/admin/addUser', model: [ username:username])
		}
	}

	def addEmail(String username) {
		if (isAdmin) {
			render (template: '/admin/addEmail', model: [ username:username])
		}
	}
	def addUserEmail(String username,String email) {
		if (isAdmin && email) {
			def profile=[email: email] as Map
			wsChatProfileService.addProfile(username, profile, false)
		}
		render "attempted add of ${email}"
	}

	def joinBooking(String id,String username) {
		Map vj = wsChatBookingService.verifyJoin(id,username)
		boolean goahead = vj.goahead
		String room = vj.room
		String startDate = vj.startDate
		String endDate = vj.endDate
		if (goahead) {
			session.wschatuser = username
			session.wschatroom = room
			redirect(controller: "wsChat", action: "chat")
		}
		[startDate:startDate, endDate:endDate]
	}

	def addBooking(){
		if (isAdmin) {
			def invite = params.invites
			if(invite instanceof String) {
				invite = [invite]
			}else{
				invite = invite as ArrayList
			}

			ArrayList invites = invite
			String dateTime = params.dateTime
			String endDateTime = params.endDateTime
			String conferenceName =  params.conferenceName
			Map results = wsChatBookingService.addBooking(invites, conferenceName, dateTime, endDateTime)
			render "Added: ${results.conference} : Returned Booking ID: ${results.confirmation}"
		}
	}

	def addaRoom() {
		if (isAdmin) {
			render template : '/room/addaRoom'
		}
	}

	def delaRoom() {
		if (isAdmin) {
			def roomList = ChatRoomList?.findAllByRoomType('chat')*.room?.unique()
			render template : '/room/delaRoom' , model:[ roomList:roomList ]
		}
	}

	def addRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.addManualRoom(room,'chat')
			render "Room ${room} added"
		}
	}

	def delRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.delaRoom(room,'chat')
			render "Room ${room} removed"
		}
	}

	def createConference() {
		if (isAdmin) {
			render (template: '/admin/book')
		}
	}

	def adminMenu() {
		if (isAdmin) {
			render template: '/admin/admin'
		}
	}

	private Boolean getIsAdmin() {
		wsChatUserService.validateAdmin(session.wschatuser)
	}
}
