package grails.plugin.wschat.auth

import grails.plugin.wschat.ChatLogs
import grails.plugin.wschat.ChatPermissions
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.listeners.ChatSessions
import grails.transaction.Transactional

import javax.websocket.Session

@Transactional
class WsChatAuthService extends WsChatConfService  implements ChatSessions  {

	String validateLogin(String username) {
		def defaultPerm='user'
		if (dbSupport()) {

			String defaultPermission=config.defaultperm  ?: defaultPerm
			def perm,user
			ChatPermissions.withTransaction {
				perm=ChatPermissions.findOrSaveWhere(name: defaultPermission).save(flush:true)
			}
			ChatUser.withTransaction {
				user=ChatUser.findOrSaveWhere(username:username, permissions:perm).save(flush:true)
			}
			ChatLogs.withTransaction {
				def logit=new ChatLogs()
				logit.username=username
				logit.loggedIn=true
				logit.loggedOut=false
				logit.save(flush:true)
			}
			return user.permissions.name as String
		}else{
			return defaultPerm
		}
	}

	Boolean loggedIn(String user) {
		Boolean loggedin=false
		try {
			synchronized (chatroomUsers) {
				chatroomUsers?.each { crec->
					if (crec.isOpen()) {
						def cuser=crec.userProperties.get("username").toString()
						if (cuser.equals(user)) {
							loggedin=true
						}
					}
				}
			}
		} catch (IOException e) {
			log.info ("onMessage failed", e)
		}
		return loggedin
	}


	void validateLogOut(String username) {
		if (dbSupport()) {
			ChatLogs.withTransaction {
				def logit=new ChatLogs()
				logit.username=username
				logit.loggedIn=false
				logit.loggedOut=true
				logit.save(flush:true)
			}
		}
	}
}
