package grails.plugin.wschat.beans

import grails.validation.Validateable

@Validateable
class SearchBean {
	String id
	Integer max = Math.min(max ?: 10, 1000)
	String s
	String order = 'desc'
	Integer userListCount = 0
	String divupdate='adminsContainer'
	String pageSizes ='10'
	String sortby = "lastUpdated"
	List allcat
	ArrayList uList
	List userList
	String action='list'
	def offset 
	String inputid = id 
	Map viewUsers
	String username
	Boolean hasAdmin
	
	Integer getOffset() { 
		int off=0
		if (offset && offset instanceof String) {
			off = offset as int
		}
		return off
	}
	
	static constraints = {
		id(nullable: false, validator: validateInput)
		s(nullable:true)
		offset(nullable:true)
		viewUsers(nullable:true)
		allcat(nullable:true)
		uList(nullable:true)
		userList(nullable:true)
		username(nullable:true)
		hasAdmin(nullable:true)
	}

	String getUsername() {
		if (username) {
			return username.trim().replace(' ', '_').replace('.', '_')
		}
	}
	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}

}
