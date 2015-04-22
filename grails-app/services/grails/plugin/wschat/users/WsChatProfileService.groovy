package grails.plugin.wschat.users

import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatUserProfile
import grails.transaction.Transactional



class WsChatProfileService  {

	def wsChatAuthService

	@Transactional
	def addProfile(String user, Map paramsMap, boolean update) {
		def found=ChatUser.findByUsername(user)
		if (found) {
			def foundprofile=ChatUserProfile.findByChatuser(found)
			if (foundprofile) {
				if (update) {
					foundprofile.properties=paramsMap
					if (!foundprofile.save(flush: true)) {
						log.error "Error updating ${user}'s profile"
					}
				}
			}else{
				def foundu=wsChatAuthService.addUser(user)
				found=foundu.user
				def perm=foundu.perm
				saveProfile(found.id,paramsMap)
			}

		}else{
			def foundu=wsChatAuthService.addUser(user)
			found=foundu.user
			def perm=foundu.perm
			saveProfile(found.id,paramsMap)
		}
	}

	@Transactional
	private saveProfile(Long found, Map paramsMap ) {
		paramsMap.put('chatuser', "${found}")
		def profileInstance = new ChatUserProfile(paramsMap)
		if (!profileInstance.save(flush: true)) {
			//profileInstance.errors.allErrors.each{println it}
			log.error "Error saving profile ${paramsMap}"
		}
	}
}
