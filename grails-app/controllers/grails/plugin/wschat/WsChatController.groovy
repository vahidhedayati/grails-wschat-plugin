package grails.plugin.wschat

import grails.converters.JSON
import grails.plugin.wschat.beans.ConnectTagBean

import grails.plugin.wschat.beans.LoginBean
import grails.plugin.wschat.beans.RoomBean
import grails.plugin.wschat.beans.SearchBean
import grails.plugin.wschat.beans.SignupBean
import grails.plugin.wschat.beans.UserBean
import grails.plugin.wschat.beans.CustomerChatTagBean
import grails.plugin.wschat.beans.LiveChatBean
import grails.plugin.springsecurity.annotation.Secured

class WsChatController extends WsChatConfService {

	def wsChatRoomService
	def autoCompleteService
	def wsChatUserService
	def wsChatProfileService
	def wsChatBookingService
	def wsChatContService
	def wsChatAuthService
	def chatUserUtilService
	def springSecurityService


	def signup() {
		def bean = new SignupBean()
		render (view:'/login/signup', model:[bean:bean])
	}

	def register(SignupBean bean) {
		if (!bean.validate()) {
			render(view:'/login/signup', model:[bean:bean])
			return
		}
		wsChatAuthService.addSecurityUser(bean)
		redirect(controller: "wsChat",action: "index")
	}

	//---------------------- Start Index
	/**
	 * index default action checks if configuration has enabled security
	 * if so sent to authIndex
     */
	def index(ConnectTagBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderIndex(bean)
	}

	@Secured(['ROLE_USER','ROLE_ADMIN'])
	def authIndex(ConnectTagBean bean) {
		session.wschatuser=springSecurityService.currentUser as String
		bean.chatuser = session.wschatuser
		renderIndex(bean)
	}

	private void renderIndex(ConnectTagBean bean) {
		bean.addLayouts=true
		bean.setRooms(wsChatRoomService.returnRoom())
		if (bean.process) {
			render "Default sign in page disabled"
			return
		}
		render (view: 'index', model: [bean:bean])
	}
	//---------------------- End Index

	//---------------------- Start login
	def login(LoginBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}", params:params)
			return
		}
		renderLogin(bean)
	}
	@Secured(['ROLE_USER','ROLE_ADMIN'])
	def authLogin(LoginBean bean) {
		renderLogin(bean)
	}
	private void renderLogin(LoginBean bean) {
		if (bean.process) {
			render "Default sign in page disabled"
			return
		}
		if (!bean.username && session.wschatuser) {
			bean.username=session.wschatuser
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
	//---------------------- End login

	//---------------------- Start chat
	def chat(ConnectTagBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderChat(bean)
	}

	@Secured(['ROLE_USER','ROLE_ADMIN'])
	def authChat(ConnectTagBean bean) {
		session.wschatuser=springSecurityService.currentUser as String
		renderChat(bean)
	}

	private void renderChat(ConnectTagBean bean) {
		bean.setAddLayouts(true)
		bean.chatuser = session.wschatuser
		bean.room = session.wschatroom ?: wsChatRoomService.returnRoom(true)
		wsChatAuthService.addBotToChatRoom(bean.room, 'chat', bean.enable_Chat_Bot, bean.botMessage, bean.uri)
		render (view: 'chat', model: [bean:bean])
	}
	//---------------------- End chat

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
			return
		}
		render "Not authorised!"
	}

	def uploadPhoto(String username) {
		def chatuser = ChatUser.findByUsername(username)
		def profile = ChatUserProfile.findByChatuser(chatuser)
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

	def xo(ConnectTagBean bean) {
		bean.chatuser = session.wschatuser
		bean.room = bean.chatuser
		ChatUser cu = wsChatUserService.currentUser(bean.chatuser)
		String uri="${bean.wsProtocol}://${bean.hostname}${bean.addAppName?'/'+bean.appName:''}/ticTacToe/start/${cu.id}/${bean.chatuser}"
		[bean:bean, uri:uri]
	}

	def xojoin(UserBean bean) {
		bean.chatuser = session.wschatuser
		ChatUser cu = wsChatUserService.currentUser(bean.user)
		bean.room = bean.user
		String uri="${bean.wsProtocol}://${bean.hostname}${bean.addAppName?'/'+bean.appName:''}/ticTacToe/join/${cu.id}/${bean.chatuser}"
		render view: 'xo', model: [bean:bean, uri:uri]
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

	//---------------------- start liveChatsRooms
	def liveChatsRooms() {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderLiveChatsRooms()
	}

	@Secured(['ROLE_ADMIN'])
	def authLiveChatRooms() {
		renderLiveChatsRooms()
	}

	private void  renderLiveChatsRooms() {
		if (isAdmin) {
			Map bean = [:]
			bean.uList= wsChatUserService.genAllLiveRooms()
			bean.userListCount=bean.uList?.size()
			render (template: '/admin/viewLiveChatRooms', model: [bean:bean])
			return
		}
		render "Not authorised!"
		return
	}
	//---------------------- End liveChatsRooms

	def autocomplete() {
		render autoCompleteService.autocomplete(params)
	}
	//---------------------- start viewUsers
	def viewUsers(SearchBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderViewUsers(bean)
	}

	@Secured(['ROLE_ADMIN'])
	def authViewUsers(SearchBean bean) {
		renderViewUsers(bean)
	}

	private void renderViewUsers(SearchBean bean) {
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
			} else {
				render (view: '/admin/viewUsers', model: model)
			}
			return
		}
		render ''
	}
	//---------------------- end viewUsers

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
		}
	}

	def addUser(String username) {
		if (isAdmin) {
			render (template: '/admin/addUser', model: [username:username])
			return
		}
		render ''
	}

	def addEmail(String username) {
		if (isAdmin) {
			render (template: '/admin/addEmail', model: [username:username])
			return
		}
		render ''
	}
	def addUserEmail(String username,String email) {
		if (isAdmin && email) {
			wsChatProfileService.addProfile(username, [email: email], false)
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
	//---------------------- start addBooking
	def addBooking() {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderAddBooking()
	}

	@Secured(['ROLE_ADMIN'])
	def authAddBooking() {
		renderAddBooking()
	}

	private void renderAddBooking() {
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
	//---------------------- end addBooking

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

	//---------------------- start joinLiveChat
	def joinLiveChat(String roomName,String username) {
		Map bean=[roomName:roomName,username:username]
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderJoinLiveChat(bean)
	}

	@Secured(['ROLE_ADMIN'])
	def authJoinLiveChat(Map bean) {
		renderJoinLiveChat(bean.roomName,bean.username)
	}

	private void renderJoinLiveChat(Map bean) {
		boolean isLiveAdmin = chatUserUtilService.isLiveAdmin(bean.username)
		if (isLiveAdmin) {
			session.wschatuser = bean.username
			session.wschatroom = bean.roomName
			session.livechat = true
			redirect(controller: "wsChat", action: "liveChat")
			return
		}
		render 'Not Authorized'
	}
	//---------------------- end joinLiveChat

	def liveChat(LiveChatBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderLiveChat(bean)

	}

	@Secured(['ROLE_ADMIN'])
	def authLiveChat(LiveChatBean bean) {
		renderLiveChat(bean)
	}
	private void renderLiveChat(LiveChatBean bean) {
	bean.addLayouts=true
		bean.chatuser = session.wschatuser
		bean.livechat = session.livechat
		bean.room = session.wschatroom ?: wsChatRoomService.returnRoom(true)
		render view: '/customerChat/livechat', model: [bean:bean]
	}

	//---------------------- start viewLiveChats
	def viewLiveChats(CustomerChatTagBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderViewLiveChats(bean)
	}
	@Secured(['ROLE_ADMIN'])
	def authViewLiveChat(CustomerChatTagBean bean) {
		renderViewLiveChats(bean)
	}

	private void renderViewLiveChats(CustomerChatTagBean bean) {
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
	//---------------------- end viewLiveChats

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
		}
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

	private String upperCaseFirst(String s) {
		s.substring(0,1).toUpperCase() + s.substring(1)
	}

}
