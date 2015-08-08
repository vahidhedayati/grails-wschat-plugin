package grails.plugin.wschat.beans

import java.util.Date;
import java.util.List;
import java.util.Map;

import grails.converters.JSON
import grails.validation.Validateable

@Validateable
class CustomerChatTagBean extends ClientTagBean {

	String name
	//String username
	String emailAddress
	String roomName
	String controller
	String action
	String params
	ArrayList uList
	List userList
	final String assistant = getConfig('liveChatAssistant') ?: 'assistant'
	final String liveChatTitle = getConfig('liveChatTitle') ?:'Customer Chat'
	String id
	Integer max = Math.min(max ?: 10, 1000)
	String s
	String order = 'desc'
	Integer userListCount = 0
	String divupdate='adminsContainer'
	String pageSizes ='10'
	String sortby = "lastUpdated"
	def offset
	String inputid = id
	Map viewUsers
	
	Integer getOffset() {
		int off=0
		if (offset && offset instanceof String) {
			off = offset as int
		}
		return off
	}
	
	Date startTime
	Boolean guestUser=true
	Boolean active=true
	
	static constraints = {
		name(nullable:true)
		//username(nullable:true)
		emailAddress(nullable:true)
		roomName(nullable:true)
		controller(nullable:true)
		action(nullable:true)
		params(nullable:true)
		active(nullable:true)
		uList(nullable:true)
		userList(nullable:true)
		s(nullable:true)
		offset(nullable:true)
		viewUsers(nullable:true)
		id(nullable:true)
		//id(nullable: false, validator: validateInput)
	}
}
