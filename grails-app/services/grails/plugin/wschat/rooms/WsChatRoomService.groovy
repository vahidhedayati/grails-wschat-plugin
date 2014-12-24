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
		def room = config.rooms[0]
		if (dbSupport.toLowerCase().equals('yes')) {
			dbrooms = ChatRoomList?.get(0)?.room
		}
		if (dbrooms) {
			room = dbrooms
		} else if (!room && !dbrooms) {
			room = 'wschat'
		}
		return room
	}
	
	public void addRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			ChatRoomList.withTransaction {
				def nr = new ChatRoomList()
				nr.room = roomName
				if (!nr.save(flush:true)) {
					log.error "Error saving ${roomName}"
				}
			}
			ListRooms()
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
						def nr = ChatRoomList.findByRoom(roomName)
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
				dbrooms = ChatRoomList?.findAll()*.room.unique()
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
