package grails.plugin.wschat

import grails.transaction.Transactional
import groovy.time.TimeCategory

import java.text.SimpleDateFormat

import javax.servlet.http.HttpSession

import org.springframework.web.context.request.RequestContextHolder


class WsChatContService extends WsChatConfService{

	@Transactional
	public Map verifyProfile(String username) {
		boolean actualuser = false
		def chatuser = ChatUser.findByUsername(username)
		def profile = ChatUserProfile?.findByChatuser(chatuser)
		def photos = ChatUserPics?.findAllByChatuser(chatuser,[max: 5, sort: 'id', order:'desc'])
		if (verifyUser(username)) {
			actualuser = true
		}
		return [actualuser:actualuser, profile:profile, photos:photos]
	}


	@Transactional
	public Map editProfile(String username) {
		def chatuser = ChatUser.findByUsername(username)
		def profile = ChatUserProfile.findByChatuser(chatuser)
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy")
		if (verifyUser(username)) {
			def bdate = profile?.birthDate
			def cdate
			if (bdate) {
				cdate = formatter.format(bdate)
			}else{
				Date cc = new Date()
				cc.clearTime()
				use(TimeCategory) {
					cc = cc -5.years
				}
				cdate = formatter.format(cc)
			}
			return [cdate:cdate, chatuser:chatuser, profile:profile]
		}
	}

	@Transactional
	public String addPhoto(params) {
		String result
		params.chatuser = ChatUser.findByUsername(params.username)
		if ((params.chatuser)&&(params.photo)) {

			def newRecord = new ChatUserPics(params)
			if (!newRecord.save(flush:true)) {
				result ="Something has gone wrong, could not upload photo"
			}
			result = "Record  ${newRecord.id} created. Create another?"
		}else{
			result = "Could not upload photo was a photo selected?"
		}
		return result
	}

	@Transactional
	public String updateProfile(params) {
		String output
		if (params.birthDate) {
			params.birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(params.birthDate)
		}
		params.chatuser = ChatUser.findByUsername(params.username)
		if (params.chatuser) {
			def exists = ChatUserProfile.findByChatuser(params.chatuser)
			if (!exists) {
				def newRecord = new ChatUserProfile(params)
				if (!newRecord.save(flush:true)) {
					output="Something has gone wrong"
				}
			}else{
				exists.properties = params
				if (!exists.save(flush:true)) {
					output="Something has gone wrong"
				}
			}
		}
		if (!output) {
			output="Infromation has been updated"
		}
		return output
	}

	@Transactional
	public Map viewUsers(String s, String sortby, String order, Integer offset, Integer max, String id=null) {
		def total, foundRec
		def paginationParams = [sort: sortby, order: order, offset: offset, max: max]
		switch (s) {
			case 'p':
				def permissions = ChatPermissions.get(id)
				if (permissions) {
					foundRec = ChatUser.findAllByPermissions( permissions, paginationParams)
					total = ChatUser.countByPermissions(permissions)
				}
				break
			default:
				s = ''
				foundRec = ChatUser.list(paginationParams)
				total = ChatUser.count()
		}

		return [s:s, foundRec:foundRec, total:total]
	}


	@Transactional
	private Boolean verifyUser(String username) {
		boolean userChecksOut = false
		def chatuser = ChatUser.findByUsername(username)
		if ((chatuser) && (username.equals(session.wschatuser))) {
			userChecksOut = true
		}
		return userChecksOut
	}


	private HttpSession getSession() {
		return RequestContextHolder.currentRequestAttributes().getSession()
	}

}
