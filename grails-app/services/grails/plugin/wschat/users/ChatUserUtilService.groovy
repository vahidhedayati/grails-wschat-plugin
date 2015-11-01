package grails.plugin.wschat.users

import grails.plugin.wschat.ChatCustomerBooking
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.WsChatConfService
import grails.transaction.Transactional

class ChatUserUtilService extends WsChatConfService  {

	
	Boolean isConfLiveAdmin(String username) {
		if (config.liveChatUsername && config.liveChatUsername==username) {
			log.debug "username: ${username} is LiveAdmin from configuration"
			return true
		}
	}
	
	@Transactional 
	Boolean isLiveAdmin(String username, Boolean defaultPermCheck=true) {
		boolean liveAdminChecked = false
		if (config.liveChatUsername && config.liveChatUsername==username) {
			log.debug "${defaultPermCheck} username: ${username} is LiveAdmin Appears to be from configuration"
			liveAdminChecked = true
		} else {
			def cu = ChatUser.findByUsername(username)
			def cb = ChatCustomerBooking.findByUsername(username)
			if (cu) {
				if (cu.permissions.name == defaultPermCheck?config.liveChatPerm?config.liveChatPerm:config.defaultperm:config.liveChatPerm && !cb) {
					liveAdminChecked = true
					log.debug "${defaultPermCheck} username: ${username} is LiveAdmin"
				}
			}
		}
		return liveAdminChecked
	}
	
}
