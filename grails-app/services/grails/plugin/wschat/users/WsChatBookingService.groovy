package grails.plugin.wschat.users

import grails.plugin.wschat.ChatBooking
import grails.plugin.wschat.ChatBookingInvites
import grails.plugin.wschat.ChatCustomerBooking
import grails.plugin.wschat.ChatMessage
import grails.plugin.wschat.ChatUser
import grails.plugin.wschat.ChatUserProfile
import grails.plugin.wschat.WsChatConfService
import grails.plugin.wschat.beans.ConfigBean
import grails.plugin.wschat.beans.CustomerChatTagBean
import groovy.time.TimeCategory

import java.rmi.server.UID
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.transaction.annotation.Transactional

class WsChatBookingService  extends WsChatConfService {

	def mailService
	def wsChatRoomService
	def wsChatAuthService
	
	static prng = new SecureRandom()


	@Transactional
	ArrayList findLiveLogs(String username) {
		ChatCustomerBooking ccb = ChatCustomerBooking.findByUsername(username)
		Map resultSet = [:]
		ArrayList finalResults=[]
		if (ccb) {
			def cm = ChatMessage.findAllByLog(ccb.log)
			cm?.each {
				resultSet = [:]
				resultSet << [ message: it.contents, date: it.dateCreated, user: it.user ]
				finalResults << resultSet
			}
		}
		return finalResults
	}
	
	@Transactional 
	ChatCustomerBooking saveCustomerBooking(CustomerChatTagBean bean) {
		ChatCustomerBooking ccb = ChatCustomerBooking.findByUsername(bean.user)
		if (!ccb) {
			ccb = new ChatCustomerBooking()
			ccb.username = bean.user
			ccb.log = wsChatAuthService.addLog()
			ccb.guestUser = bean.guestUser
		}
		ccb.roomName = bean.roomName
		ccb.controller = bean.controller
		ccb.action = bean.action
		ccb.startTime = new Date()
		ccb.active = true
		ccb.guestUser = ccb?false:true
		//if (inputParams) {
		//	ccb.params = inputParams
		//}
		ccb.save()
	}
	
	@Transactional
	Map verifyJoin(String token,String username)	{
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

	
	

	/**
	 * sendLiveEmail figures out who should get the email and passes it to liveChatRequest below
	 * @param ccb
	 * @param msgFrom
	 * @param room
	 */
	void sendLiveEmail(ChatCustomerBooking ccb,String msgFrom, String room) {
		String contactEmail2 = config.liveContactEmail
		String contactUsername = config.liveChatUsername
		String contactGroup = config.liveChatPerm ?: config.defaultperm
		try {
			ConfigBean cb = new ConfigBean()
			if (contactEmail2 && contactUsername) {
				liveChatRequest(ccb, cb.url, msgFrom, room, contactEmail2, config.liveContactName ?: 'Site Administrator', contactUsername)
			}
			String query= """
										select new map(p.email as email, u.username as username) from ChatUserProfile p join p.chatuser u
										join u.permissions e where e.name=:contactGroup
										"""
			Map inputParams = [contactGroup:contactGroup]
			def results = ChatUserProfile.executeQuery(query,inputParams,[readonly:true,timeout:20,max:5])
			results?.each {
				if (it.email) {
					liveChatRequest(ccb, cb.url,  msgFrom, room, it.email, it.username ,it.username)
				}
			}
		} catch (Exception e) {
			//e.printStackTrace()
			log.debug "It is likely you have not enabled SMTP service for mail to be sent"
		}
	}
	
	/**
	 *  This sends an email with a custom body overridable by 
	 * 	wschat.liveChatBody  and wschat.liveChatSubject configured
	 *  in your application.groovy/BuildConfig.groovy  
	 * @param ccb
	 * @param url
	 * @param thisUser
	 * @param room
	 * @param contactEmail
	 * @param contactName
	 * @param adminUsername
	 */
	void liveChatRequest(ChatCustomerBooking ccb, String url, String thisUser, String room, String contactEmail, String contactName, String adminUsername) {
		def now = new Date().format("dd_MM_yyyy_HH_mm")
		String defaultsubject = "You have a live chat ${now} "
		String defaultbody = """Dear ${contactName},
			A live chat request has been made ${now}
			----------------------------------------------------------------------------
			${ccb?.name} 
			Controller: ${ccb?.controller}
			Action: ${ccb?.action}

			They are logged into ${room} with the id of ${thisUser} .
			----------------------------------------------------------------------------

			Please can you go to:
 
			${url}joinLiveChat?roomName=${room}&username=${adminUsername}


			Where the user is waiting for your help 
		"""
		
		String body  = 	config?.liveChatBody ?: defaultbody
		log.debug body
		String subject = config?.liveChatSubject ?: defaultsubject
		SendMail(contactEmail,'',subject,body)
	}
	
	@Transactional
	Map addBooking(ArrayList invites, String conference, String startDate, String endDate) {
		def current = new Date().format("dd_MM_yyyy_HH_mm")
		def dFormat = "dd/MM/yyyy HH:mm"
		SimpleDateFormat df = new SimpleDateFormat(dFormat)
		conference = conference+"_"+current
		def dateTime = df.parse(startDate)
		def endDateTime = df.parse(endDate)
		def myConference = ChatBooking.findOrSaveWhere(conferenceName: conference, dateTime:dateTime, endDateTime:endDateTime)
		String defaultsubject = "You have a chat Scheduled for ${startDate} "
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
				ApplicationTagLib  g = new ApplicationTagLib()
				def chaturl = g.createLink(controller: 'wsChat', action: 'joinBooking', id: parsedToken, params: sendMap, absolute: 'true' )

				def myMap = [username: found.username, emailAddress: foundprofile.email,
					token: parsedToken, booking:myConference]
				def inviteInstance = new ChatBookingInvites(myMap)
				if (!inviteInstance.save()) {
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
				if (config.emailFrom) {
					from "${config.emailFrom}"
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
