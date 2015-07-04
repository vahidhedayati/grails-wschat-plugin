package grails.plugin.wschat.users

import grails.plugin.wschat.ChatBooking
import grails.plugin.wschat.ChatBookingInvites
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatUserProfile
import grails.plugin.wschat.WsChatConfService
import groovy.time.TimeCategory

import java.rmi.server.UID
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat

import org.springframework.transaction.annotation.Transactional
import org.grails.plugins.web.taglib.ApplicationTagLib

class WsChatBookingService  extends WsChatConfService {

	def mailService
	def wsChatRoomService

	static prng = new SecureRandom()
	
	@Transactional
	public Map verifyJoin(String token,String username)	{
		boolean goahead = false
		def found = ChatBookingInvites.findByTokenAndUsername(token,username)
		String room,startDate,endDate
		if (found) {
			room = found.booking?.conferenceName
			startDate = found.booking?.dateTime
			endDate = found.booking?.endDateTime
			if (username == found.username) {
				goahead = isValid(startDate,endDate)
			}
		}else{
			log.error "Could not find ${username} ${token}"
		}
		return [goahead: goahead, room: room, startDate:startDate, endDate:endDate]
	}

	Boolean isValid(String startDate,String endDate ) {
		Boolean yesis = false

		def now = new Date()

		def validStart,validEnd
		String dFormat = config.db.booking ?: "yyyy-MM-dd HH:mm:ss"
		SimpleDateFormat df = new SimpleDateFormat(dFormat)

		def dateTime = df.parse(startDate)
		def endDateTime
		if (endDate) {
			endDateTime = df.parse(endDate)
		}
		else{
			endDateTime = df.parse(startDate)
		}

		use(TimeCategory) {
			validStart = dateTime -5.minutes
			validEnd = endDateTime +5.minutes
		}
		if ((now.after(validStart)) && (now.before(validEnd))) {
			yesis = true
		}
		return yesis
	}

	@Transactional
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
		def myConference = ChatBooking.findOrSaveWhere(conferenceName: conference, dateTime:dateTime, endDateTime:endDateTime)
		String defaultsubject = "You have a chat Scheduled for ${startDate} "
		ApplicationTagLib  g = new ApplicationTagLib()
		String defaultbody = """Dear [PERSON],
A request has been made to join chat room ${conference}, scheduled between (${startDate}/${endDate}.
The following members have also been invited ${invites}.

Please book the request in your calendar and click the following link on date/time of booking.
The room will be only be available during set period and a 5 minutes  pre and post schedule date.
Please join chat on [CHATURL]
"""
		String body  = 	config?.msgbody ?: defaultbody
		String subject = config?.subject ?: defaultsubject
		wsChatRoomService.addManualRoom(conference,'booking')
		invites.each { user->
			def found=ChatUser.findByUsername(user)
			if (found) {
				def foundprofile=ChatUserProfile.findByChatuser(found)

				def uid = found.username + new UID().toString() + prng.nextLong() + System.currentTimeMillis()
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(uid.getBytes("UTF-8"));
				def token = hash.encodeBase64()
				String parsedToken = token.toString().replaceAll('[^a-zA-Z0-9[:space:]]','')

				def sendMap = [username: found.username]
				def chaturl = g.createLink(controller: 'wsChat', action: 'joinBooking', id: parsedToken, params: sendMap, absolute: 'true' )

				def myMap = [username: found.username, emailAddress: foundprofile.email,
					token: parsedToken, booking:myConference]
				def inviteInstance = new ChatBookingInvites(myMap)
				if (!inviteInstance.save(flush: true)) {
					log.error "Error saving Booking ${inviteInstance.errors}"
				}else{
					String sendbody = body.replace('[PERSON]', found.username).replace('[CHATURL]', chaturl)
					if (config.debug) {
						log.debug "MSG: ${subject}\n${sendbody}"
					}
					SendMail(foundprofile.email,'',subject,sendbody)
				}
			}
		}
		return [conference:conference, confirmation: myConference ]
	}

	void SendMail(toconfig, mycc, mysubject, mybody) {
		doSendMail toconfig, mycc, mysubject, mybody, false
	}

	void SendHMail(toconfig, mycc, mysubject, mybody) {
		doSendMail toconfig, mycc, mysubject, mybody, true
	}

	private void doSendMail(toconfig, mycc, mysubject, mybody, boolean html) throws Exception {
		List<String> recipients = []
		String email = calculateAddresses(recipients, toconfig)
		List<String> ccrecipients = []
		String ccuser = calculateAddresses(recipients, mycc)
		try {
			mailService.sendMail {
				if (recipients) {
					to recipients
				}
				else {
					to email
				}

				if (ccrecipients) {
					cc ccrecipients
				}
				else {
					if (ccuser) {
						cc ccuser
					}
				}
				subject mysubject
				if (html) {
					if (mybody.indexOf('<html>') == -1) {
						mybody = "<html><body bgcolor=#FFF>" + mybody + "</body></html>"
					}
					if (mybody) {
						html mybody
					}
				}
				else if (mybody) {
					body mybody
				}
			}
		}
		catch (e) {
			throw new Exception(e.message)
			//log.error messageSource.getMessage('default.issue.sending.email.label', ["${e.message}"].toArray(), "Problem sending email ${e.message}", LCH.getLocale()),e
		}
	}


	private String calculateAddresses(List<String> recipients, config) {
		String address = ''
		if (config) {
			if (config.toString().indexOf('@') > -1) {
				address = config
			}
			else {
				address = config.mailconfig[config] ?: ''
				if (address.toString().indexOf(',') > -1) {
					recipients.addAll(address.split(',').collect { it.trim() })
				}
			}
			if (config.toString().indexOf(',') > -1) {
				recipients.addAll(config.split(',').collect { it.trim() })
			}
		}
		return address
	}

}
