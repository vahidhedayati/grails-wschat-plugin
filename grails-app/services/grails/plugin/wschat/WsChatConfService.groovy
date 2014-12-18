package grails.plugin.wschat

import java.text.SimpleDateFormat

import javax.websocket.Session


class WsChatConfService {
	
	static transactional = false
	
	def grailsApplication

	Boolean dbSupport() {
		Boolean dbsupport=false
		String dbsup=config.dbsupport  ?: 'yes'
		if ((dbsup.toLowerCase().equals('yes'))||(dbsup.toLowerCase().equals('true'))) {
			dbsupport=true
		}
		return dbsupport
	}
	
	 Boolean isAdmin(Session userSession) {
		Boolean useris=false
		String userLevel=userSession.userProperties.get("userLevel") as String
		if (userLevel.toString().toLowerCase().startsWith('admin')) {
			useris=true
		}
		return useris
	}
	
	Boolean isBanned(String username) {
		 Boolean yesis=false
		 if (dbSupport()) {
			 def now=new Date()
			 def current = new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss').format(now)
			 ChatBanList.withTransaction {
				 def found=ChatBanList.findAllByUsernameAndPeriodGreaterThan(username,current)
				 def dd=ChatBanList.findAllByUsername(username)
				 if (found) {
					 yesis=true
				 }
			 }
		 }
		 return yesis
	 }

	def getConfig() {
		grailsApplication.config.wschat
	}

}
