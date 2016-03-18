package grails.plugin.wschat.beans

import grails.converters.JSON
import grails.validation.Validateable


class CustomerChatTagBean extends ClientTagBean  implements Validateable {
	String name
	//String username
	String emailAddress
	String roomName
	String controller
	String action
	String params
	ArrayList uList
	List userList
	public final String liveChatTitle = getConfig('liveChatTitle') ?:'Customer Chat'
	public final String botLiveMessage = getConfig('botLiveMessage') ?: 'Welcome, this is an automated message, attempting to retrieve a member of staff for you. Please wait'

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

	JSON inactiveLCTitle=["background":"black","color":"white"] as JSON
	JSON inactiveLCBody=["background":"#ddd","color":"#000"] as JSON
	JSON activeLCTitle=["background-colour":"#FF0000","background":"#c00","color":"white"] as JSON
	JSON activeLCBody=["background":"#FFF","color":"#000"] as JSON

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
		inactiveLCTitle(nullable:true)
		inactiveLCBody(nullable:true)
		activeLCTitle(nullable:true)
		activeLCBody(nullable:true)
		//id(nullable: false, validator: validateInput)
	}

	void setInactiveLCTitle(Map input) {
		if (input){
			inactiveLCTitle=input as JSON
		}
	}
	void setInactiveLCBody(Map input) {
		if (input){
			inactiveLCBody=input as JSON
		}
	}
	void setActiveLCTitle(Map input) {
		if (input){
			activeLCTitle=input as JSON
		}
	}
	void setActiveLCBody(Map input) {
		if (input){
			activeLCBody=input as JSON
		}
	}

	String getActiveLCBody() {
		return activeLCBody as String
	}
	String getInactiveLCBody() {
		return inactiveLCBody as String
	}
	String getActiveLCTitle() {
		return activeLCTitle as String
	}
	String getInactiveLCTitle() {
		return inactiveLCTitle as String
	}
}
