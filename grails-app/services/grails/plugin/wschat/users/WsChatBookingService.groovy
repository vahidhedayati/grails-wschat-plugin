package grails.plugin.wschat.users

import grails.plugin.wschat.ChatBooking
import grails.plugin.wschat.ChatBookingInvites
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatUserProfile

import java.rmi.server.UID
import java.security.*
import java.text.SimpleDateFormat

import org.springframework.transaction.annotation.Transactional

@Transactional
class WsChatBookingService  {

	def wsChatRoomService
	static prng = new SecureRandom()

	public Map addBooking(ArrayList invites, String conference, String startDate, String endDate) {

		def curr = new Date()
		def dFormat = "dd/MM/yyyy HH:mm"
		def cFormat = "dd_MM_yyyy_HH_mm"

		SimpleDateFormat df = new SimpleDateFormat(dFormat)
		SimpleDateFormat cf = new SimpleDateFormat(cFormat)

		def current = cf.format(curr)

		conference = conference+"_"+current
		def dateTime = df.parse(startDate)
		def endDateTime = df.parse(endDate)
		def myConference
		//ChatBooking.withTransaction {
			myConference = ChatBooking.findOrSaveWhere(conferenceName: conference, dateTime:dateTime, endDateTime:endDateTime).save(flush:true)
		//}
		wsChatRoomService.addManualRoom(conference,'booking')
		invites.each { user->
			def found=ChatUser.findByUsername(user)
			if (found) {
				def foundprofile=ChatUserProfile.findByChatuser(found)
				def uid = found.username + new UID().toString() + prng.nextLong() + System.currentTimeMillis()
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(uid.getBytes("UTF-8"));
				def token = hash.encodeBase64()
				def myMap = [username: found.username, emailAddress: foundprofile.email, token: token as String, booking:myConference]
				ChatBookingInvites.withTransaction {
					def inviteInstance = new ChatBookingInvites(myMap)
					if (!inviteInstance.save(flush: true)) {
						//inviteInstance.errors.allErrors.each{println it}
						log.error "Error saving Booking"
					}
				}
			}
		}
		return [conference:conference, confirmation: myConference ]
	}
}
