package grails.plugin.wschat.rooms

import grails.plugin.wschat.ChatRoomList
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.listeners.ChatSessions
import grails.transaction.Transactional

import javax.websocket.Session

@Transactional
class WsChatRoomService extends WsChatConfService  implements ChatSessions {

	def wsChatMessagingService
	def wsChatUserService

	def sendRooms(Session userSession) {
		wsChatMessagingService.messageUser(userSession,roomList())
	}

	def  ListRooms() {
		wsChatMessagingService.broadcast2all(roomList())
	}
	
	void addRoom(Session userSession,String roomName) {
		if ((dbSupport()) && (isAdmin(userSession)) ) {
			ChatRoomList.withTransaction {
				def nr=new ChatRoomList()
				nr.room=roomName
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
					Iterator<Session> iterator=chatroomUsers?.iterator()
					if (iterator) {
						while (iterator?.hasNext())  {
							def crec=iterator?.next()
							if (crec.isOpen() && roomName.equals(crec.userProperties.get("room"))) {
								def cuser=crec.userProperties.get("username").toString()
								//String croom = crec.userProperties.get("room") as String
								wsChatUserService.kickUser(userSession,cuser)
							}
						}

						ChatRoomList.withTransaction {
							def nr=ChatRoomList.findByRoom(roomName)
							if (nr) {
								nr.delete(flush: true)
							}
						}
						ListRooms()
					}
				}
			} catch (IOException e) {
				log.error ("onMessage failed", e)
			}
		}
	}

	Map roomList() {
		def uList=[]
		def room=config.rooms
		if (room) {
			uList=[]
			room.each {
				def myMsg=[:]
				myMsg.put("room",it)
				uList.add(myMsg)
			}
		}
		def dbrooms
		def finalList=[:]
		if (dbSupport()) {
			ChatRoomList.withTransaction {
				dbrooms=ChatRoomList?.findAll()*.room.unique()
			}
			if (dbrooms) {
				//uList=[]
				dbrooms.each {
					def myMsg=[:]
					myMsg.put("room",it)
					uList.add(myMsg)
				}
			}
		}
		if (!room && !dbrooms) {
			room='wschat'
			def myMsg=[:]
			myMsg.put("room",room)
			uList.add(myMsg)
		}
		finalList.put("rooms", uList)
		return finalList as Map
	}
}
