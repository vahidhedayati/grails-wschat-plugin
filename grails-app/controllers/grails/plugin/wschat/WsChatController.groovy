package grails.plugin.wschat

import grails.converters.JSON
import grails.plugin.wschat.beans.ConnectTagBean
import grails.plugin.wschat.beans.CustomerChatTagBean
import grails.plugin.wschat.beans.LiveChatBean
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
	def wsChatAuthService

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
		wsChatAuthService.addBotToChatRoom(bean.room, 'chat', bean.enable_Chat_Bot, bean.botMessage, bean.uri)
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
	
	def xo(ConnectTagBean bean) {
		bean.chatuser = session.wschatuser
		bean.room = bean.chatuser
		ChatUser cu = wsChatUserService.currentUser(bean.chatuser)
		String uri="ws://${bean.hostname}${bean.addAppName?'/'+bean.appName:''}/ticTacToe/start/${cu.id}/${bean.chatuser}"
		[bean:bean, uri:uri]
	}
	
	def xojoin(UserBean bean) {
		bean.chatuser = session.wschatuser
		ChatUser cu = wsChatUserService.currentUser(bean.user)
		bean.room = bean.user
		String uri="ws://${bean.hostname}${bean.addAppName?'/'+bean.appName:''}/ticTacToe/join/${cu.id}/${bean.chatuser}"
		render view: 'xo', model: [bean:bean, uri:uri]
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
		render 'Not Authorized'
	}

	def search(String mq) {
		if (isAdmin) {
			Map ss = wsChatUserService.search(mq)
			render (template: '/admin/userList', model: [bean:[userList:ss.userList, uList:ss.uList]])
			return
		}
		render ''
	}

	def findUser(String uid) {
		if (isAdmin) {
			def returnResult = wsChatUserService.findaUser(uid)
			render returnResult as JSON
			return
		}
		render 'Not Authorized'
	}

	def addUser(String username) {
		if (isAdmin) {
			render (template: '/admin/addUser', model: [ username:username])
			return
		}
		render ''
	}

	def addEmail(String username) {
		if (isAdmin) {
			render (template: '/admin/addEmail', model: [ username:username])
			return
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
			return
		}
		render 'Not Authorized'
	}
	
	/* liveChat */
	def loadChat(String user, String controller, String action,String roomName) {
		CustomerChatTagBean bean = new CustomerChatTagBean()
		if (user) {
			bean.user = user
			bean.guestUser = false
		} else { 
			bean.guestUser = true
			bean.user = 'Guest'+session.id
		} 
		bean.controller = controller
		bean.action = action
		bean.roomName = roomName
		wsChatBookingService.saveCustomerBooking(bean)
		render view: '/customerChat/chatPage', model: [bean:bean]
	}
	
	def joinLiveChat(String roomName,String username) {
		boolean isLiveAdmin = wsChatUserService.isLiveAdmin(username)
		if (isLiveAdmin) {
			session.wschatuser = username
			session.wschatroom = roomName
			session.livechat = true
			redirect(controller: "wsChat", action: "liveChat")
			return
		}	
		render 'Not Authorized'
	}
	
	def liveChat(LiveChatBean bean) {
		bean.addLayouts=true
		bean.chatuser = session.wschatuser
		bean.livechat = session.livechat
		bean.room = session.wschatroom ?: wsChatRoomService.returnRoom(true)
		render view: '/customerChat/livechat', model: [bean:bean]
	}
	
	def viewLiveChats(CustomerChatTagBean bean) { 
		if (isAdmin) {
			bean.uList = ChatCustomerBooking.list()
			Map model = [bean:bean]
			if (request.xhr) {
				render (template: '/customerChat/viewUsers', model: model)
			}
			else {
				render (view: '/customerChat/viewUsers', model: model)
			}
			return
		}
		render 'Not Authorized'
	}
	
	def viewLiveLogs(String username) {
		def livelogs = wsChatBookingService.findLiveLogs(username)
		render template: '/customerChat/viewLiveLogs', model: [livelogs:livelogs]
	}
	
	/* end live chat */
	
	def viewLogs(String username) {
		def chatlogs = wsChatUserService.findLogs(username)
		render template: '/admin/viewLogs', model: [chatlogs:chatlogs]
	}
	
	def searchLiveChat(String mq) {
		if (isAdmin) {
			Map ss = wsChatUserService.search(mq)
			render (template: '/admin/userList', model: [bean:[userList:ss.userList, uList:ss.uList]])
			return
		}
		render ''
	}
	
	def addaRoom() {
		if (isAdmin) {
			render template : '/room/addaRoom'
			return
		}
		render ''
	}

	def delaRoom() {
		if (isAdmin) {
			def roomList = ChatRoomList?.findAllByRoomType('chat')*.room?.unique()
			render template : '/room/delaRoom' , model:[ roomList:roomList ]
			return
		}
		render ''
	}

	def addRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.addManualRoom(room,'chat')
			render "Room ${room} added"
			return
		}
		render ''
	}

	def delRoom(String room) {
		if (isAdmin) {
			wsChatRoomService.delaRoom(room,'chat')
			render "Room ${room} removed"
			return
		}
		render ''
	}

	def createConference() {
		if (isAdmin) {
			render (template: '/admin/book')
			return
		}
		render ''
	}

	def adminMenu() {
		if (isAdmin) {
			render template: '/admin/admin'
			return
		}
		render ''
	}

	private Boolean getIsAdmin() {
		wsChatUserService.validateAdmin(session.wschatuser)
	}
}
