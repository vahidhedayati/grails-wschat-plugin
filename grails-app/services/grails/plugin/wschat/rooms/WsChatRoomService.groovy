package grails.plugin.wschat.rooms

import grails.plugin.wschat.ChatRoomList
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional

import javax.websocket.Session

class WsChatRoomService extends WsChatConfService {

	def wsChatMessagingService
	def wsChatUserService


	def sendRooms(Session userSession) {
		wsChatMessagingService.messageUser(userSession,roomList())
	}

	def  listRooms() {
		wsChatMessagingService.broadcast2all(roomList())
	}

	@Transactional
	def returnRoom(boolean dbSupport, Boolean displayString=null) {
		ArrayList dbrooms = config.rooms as ArrayList
		if (!dbrooms && dbSupport) {
			ChatRoomList dbroom = ChatRoomList?.findAllByRoomType('chat')
			dbrooms = dbroom.room
		}
		if (!dbrooms) {
			dbrooms = ['wschat']
		}
		if (displayString) {
			return dbrooms[0] as String
		}else{
			return dbrooms
		}
	}

	@Transactional
	public void addRoom(Session userSession,String roomName, String roomType) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			def nr = new ChatRoomList()
			nr.room = roomName
			if (roomType) {
				nr.roomType = roomType
			}
			if (!nr.save(flush:true)) {
				log.error "Error saving ${roomName} ${nr.errors}"
			}
		}
		listRooms()
	}

	@Transactional
	public void addManualRoom(String roomName, String roomType) {
		if (!roomType) {
			roomType = 'chat'
		}
		if 	(dbSupport())  {
			def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
			if (!record) {
				def nr = new ChatRoomList()
				nr.room = roomName
				if (roomType) {
					nr.roomType = roomType
				}
				if (!nr.save(flush:true)) {
					log.error "Error saving ${roomName} ${nr.errors}"
				}
			}
		}
	}

	@Transactional
	void delaRoom(String roomName, String roomType) {
		if (!roomType) {
			roomType = 'chat'
		}

		def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
		if (record) {
			record.delete(flush:true)
		}
	}

	@Transactional
	void delRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			chatNames.each { String cuser, Session crec ->
				if (crec && crec.isOpen() && roomName.equals(crec.userProperties.get("room"))) {
					wsChatUserService.kickUser(userSession,cuser)
				}
			}
			def nr = ChatRoomList?.findByRoomAndRoomType(roomName,'chat')
			if (nr) {
				nr.delete(flush: true)
			}
			listRooms()
		}
	}

	@Transactional
	Map roomList() {
		def uList = []
		def room = config.rooms
		if (room) {
			uList = []
			room.each {
				def myMsg = [:]
				myMsg.put("room",it)
				uList.add(myMsg)
			}
		}
		def dbrooms
		def finalList = [:]
		if (dbSupport()) {
			dbrooms = ChatRoomList?.findAllByRoomType('chat')*.room?.unique()
		}
		if (dbrooms) {
			dbrooms.each {
				def myMsg = [:]
				myMsg.put("room",it)
				uList.add(myMsg)
			}
		}
		if (!room && !dbrooms) {
			room = 'wschat'
			def myMsg = [:]
			myMsg.put("room",room)
			uList.add(myMsg)
		}
		finalList.put("rooms", uList)
		return finalList as Map
	}
}
