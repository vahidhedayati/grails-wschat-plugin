package grails.plugin.wschat.rooms

import grails.plugin.wschat.ChatRoomList
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional

import javax.websocket.Session

class WsChatRoomService extends WsChatConfService {

	def wsChatMessagingService
	def wsChatUserService
	
	public static List DEFAULT_ROOM = ChatRoomList.DEFAULT_ROOM.collect{['room':it]}
	
	void sendRooms(Session userSession) {
		wsChatMessagingService.messageUser(userSession,roomList())
	}

	void  listRooms() {
		wsChatMessagingService.broadcast2all(roomList())
	}

	@Transactional
	def returnRoom(boolean displayString=false) {
		List dbrooms = (config?.rooms.collect{it}  +ChatRoomList?.findAllByRoomType(ChatRoomList.DEFAULT_ROOM_TYPE)*.room?.unique()?.collect{it}) ?: ChatRoomList.DEFAULT_ROOM
		if (displayString) {
			return dbrooms[0] as String
		}else{
			return dbrooms
		}
	}

	@Transactional
	void addRoom(Session userSession,String roomName, String roomType=ChatRoomList.DEFAULT_ROOM_TYPE) {
		if (isAdmin(userSession)) {
			def nr = new ChatRoomList()
			nr.room = roomName
			nr.roomType = roomType
			if (!nr.save()) {
				log.error "Error saving ${roomName} ${nr.errors}"
			}
		}
		listRooms()
	}

	@Transactional
	void addManualRoom(String roomName, String roomType=ChatRoomList.DEFAULT_ROOM_TYPE) {
		def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
		if (!record) {
			def nr = new ChatRoomList()
			nr.room = roomName
			nr.roomType = roomType
			if (!nr.save()) {
				log.error "Error saving ${roomName} ${nr.errors}"
			}
		}
	}

	@Transactional
	void delaRoom(String roomName, String roomType=ChatRoomList.DEFAULT_ROOM_TYPE) {
		def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
		if (record) {
			record.delete()
		}
	}

	@Transactional
	void delRoom(Session userSession,String roomName) {
		if (isAdmin(userSession)) {
			chatNames.each { String cuser, Map<String,Session> records ->
				Session crec = records.find{it.key==roomName}?.value
				if (crec && crec.isOpen() && roomName.equals(crec.userProperties.get("room"))) {
					wsChatUserService.kickUser(userSession,cuser)
				}
			}
			def nr = ChatRoomList?.findByRoomAndRoomType(roomName,'chat')
			if (nr) {
				nr.delete()
			}
			listRooms()
		}
	}

	@Transactional
	Map roomList() {
		List uList = (config?.rooms.collect{['room':it]}  +ChatRoomList?.findAllByRoomType(ChatRoomList.DEFAULT_ROOM_TYPE)*.room?.unique()?.collect{['room':it]}) ?: DEFAULT_ROOM
		Map finalList = [rooms: uList]
		return finalList 
	}
}
