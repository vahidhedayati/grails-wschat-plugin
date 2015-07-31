package grails.plugin.wschat

import grails.converters.JSON
import grails.plugin.wschat.beans.ConnectTagBean
import grails.plugin.wschat.beans.InitiationBean
import grails.plugin.wschat.beans.LoginBean
import grails.plugin.wschat.beans.RoomBean
import grails.plugin.wschat.beans.SearchBean
import grails.plugin.wschat.beans.UserBean

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib



class WsChatController extends WsChatConfService {

	def wsChatRoomService
	def autoCompleteService
	def wsChatUserService
	def wsChatProfileService
	def wsChatBookingService
	def wsChatContService

	def index(ConnectTagBean bean) {
		bean.addLayouts=true
		bean.setRooms(config.rooms as ArrayList)
		if (!bean.rooms) {
			bean.setRooms(wsChatRoomService.returnRoom())
		}
		if (bean.process) {
			render "Default sign in page disabled"
			return
		}
		[bean:bean]
	}

	def chat(ConnectTagBean bean) {
		bean.addLayouts=true
		bean.chatuser = session.wschatuser
		bean.room = session.wschatroom ?: wsChatRoomService.returnRoom(true)
		[bean:bean]
	}

	def sendfile(RoomBean bean) {
		bean.chatuser = session.wschatuser
		boolean sender = false
		if (bean.room == bean.chatuser) {
			bean.sender = true
		}
		[bean:bean]
	}

	def sendmedia(RoomBean bean) {
		bean.chatuser = session.wschatuser
		boolean sender = false
		if (bean.room == bean.chatuser) {
			bean.sender = true
		}
		[bean:bean]
	}

	def login(LoginBean bean) {
		if (bean.process) {
			render "Default sign in page disabled"
			return
		}
		if (!bean.validate()) {
			flash.message = bean.errors
			redirect(controller: "wsChat",action: "index")
			return
		}else{
			session.wschatuser = bean.username
			session.wschatroom = bean.room
			redirect(controller: "wsChat", action: "chat")
			return
		}
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

	def camsend(UserBean bean) {
		[bean:bean]
	}

	def webrtcsend(UserBean bean) {
		[bean:bean]
	}

	def webrtcrec(UserBean bean) {
		bean.chatuser = session.wschatuser
		[bean:bean]
	}

	def camrec(UserBean bean) {
		bean.chatuser = session.wschatuser
		[bean:bean]
	}

	def autocomplete() {
		render autoCompleteService.autocomplete(params)
	}


	def viewUsers(SearchBean bean) {
		if (isAdmin) {
			bean.uList = wsChatUserService.genAllUsers()
			def gUsers = wsChatContService.viewUsers(bean.s ?: '', bean.sortby, bean.order, bean.offset, bean.max , bean.inputid)
			bean.s = gUsers.s
			bean.userList = gUsers.foundRec
			bean.userListCount = gUsers.total
			bean.allcat=ChatUser.list()
			Map model = [bean:bean]
			if (request.xhr) {
				render (template: '/admin/viewUsers', model: model)
			}
			else {
				render (view: '/admin/viewUsers', model: model)
			}
		}
		render ''
	}

	def search(String mq) {
		if (isAdmin) {
			Map ss = wsChatUserService.search(mq)
			render (template: '/admin/userList', model: [bean:[userList:ss.userList, uList:ss.uList]])
		}
		render ''
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
		render ''
	}

	def addEmail(String username) {
		if (isAdmin) {
			render (template: '/admin/addEmail', model: [ username:username])
		}
		render ''
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
		render ''
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
		render ''
	}

	def addRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.addManualRoom(room,'chat')
			render "Room ${room} added"
		}
		render ''
	}

	def delRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.delaRoom(room,'chat')
			render "Room ${room} removed"
		}
		render ''
	}

	def createConference() {
		if (isAdmin) {
			render (template: '/admin/book')
		}
		render ''
	}

	def adminMenu() {
		if (isAdmin) {
			render template: '/admin/admin'
		}
		render ''
	}

	private Boolean getIsAdmin() {
		wsChatUserService.validateAdmin(session.wschatuser)
	}
}
