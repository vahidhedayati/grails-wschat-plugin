package grails.plugin.wschat.rooms

import grails.plugin.wschat.ChatRoomList
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.interfaces.ChatSessions

import javax.websocket.Session

//@Transactional
class WsChatRoomService extends WsChatConfService  implements ChatSessions {

	def wsChatMessagingService
	def wsChatUserService


	def sendRooms(Session userSession) {
		wsChatMessagingService.messageUser(userSession,roomList())
	}

	def  ListRooms() {
		wsChatMessagingService.broadcast2all(roomList())
	}

	def returnRoom(String dbSupport) {
		def dbrooms
		def crooms = config.rooms as ArrayList
		def room
		if (crooms) {
			room = crooms[0]
		}
		if (dbSupport.toLowerCase().equals('yes')) {
			dbrooms = ChatRoomList?.findByRoomType('chat', [max:1])*.room?.unique()
		}
		if (dbrooms) {
			room = dbrooms
		} else if (!room && !dbrooms) {
			room = 'wschat'
		}
		return room
	}

	public void addRoom(Session userSession,String roomName, String roomType) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			ChatRoomList.withTransaction {
				def nr = new ChatRoomList()
				nr.room = roomName
				if (roomType) {
					nr.roomType = roomType
				}
				if (!nr.save(flush:true)) {
					log.error "Error saving ${roomName}"
					if (!nr.save(flush:true)) {
						if (config.debug == "on") {
							nr.errors.allErrors.each{println it}
						}
					}
				}
			}
			ListRooms()
		}
	}
	public void addManualRoom(String roomName, String roomType) {
		if (!roomType) {
			roomType = 'chat'
		}
		if 	(dbSupport())  {
			def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
			if (!record) {
				ChatRoomList.withTransaction {
					def nr = new ChatRoomList()
					nr.room = roomName
					if (roomType) {
						nr.roomType = roomType
					}
					if (!nr.save(flush:true)) {
						log.error "Error saving ${roomName}"
						if (!nr.save(flush:true)) {
							if (config.debug == "on") {
								nr.errors.allErrors.each{println it}
							}
						}
					}
				}
			}
		}
	}
	void delaRoom(String roomName, String roomType) {
		if (!roomType) {
			roomType = 'chat'
		}

		def record = ChatRoomList?.findByRoomAndRoomType(roomName, roomType)
		if (record) {
			record.delete(flush:true)
		}
	}

	void delRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			try {
				synchronized (chatroomUsers) {
					chatroomUsers?.each { crec->
						if (crec && crec.isOpen() && roomName.equals(crec.userProperties.get("room"))) {
							def cuser = crec.userProperties.get("username").toString()
							//String croom  =  crec.userProperties.get("room") as String
							wsChatUserService.kickUser(userSession,cuser)
						}
					}

					ChatRoomList.withTransaction {
						def nr = ChatRoomList?.findByRoomAndRoomType(roomName,'chat')
						if (nr) {
							nr.delete(flush: true)
						}
					}
					ListRooms()
				}
			} catch (IOException e) {
				log.error ("onMessage failed", e)
			}
		}
	}

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
			ChatRoomList.withTransaction {
				//def rooms = ChatRoomList?.findAllByRoomType('chat')
				//if (rooms) {
				dbrooms = ChatRoomList?.findAllByRoomType('chat')*.room?.unique()
				//	dbrooms =rooms.room
				//}
			}
			if (dbrooms) {
				//uList = []
				dbrooms.each {
					def myMsg = [:]
					myMsg.put("room",it)
					uList.add(myMsg)
				}
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
