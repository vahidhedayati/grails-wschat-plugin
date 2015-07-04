package grails.plugin.wschat.beans

import grails.validation.Validateable

class AutoCompleteBean extends ConfigBean implements Validateable {
	String id
	String clazz
	String name
	String cid
	String styles
	String controller = "wsChat"
	String action = "autocomplete"
	String collectField
	String searchField
	String domain
	def require
	def required
	Integer max = 10
	String value =""
	String order = "asc"

	String template='/autoComplete/AutoCompleteBasic'
	
	String userTemplate=getConfig('autocomplete')
	
	Boolean getRequire() {
		return validateBool(require)
	}
	Boolean getRequired() {
		return validateBool(required)
	}
	String getName() { 
		if (!name) {
			name=id
		}
		return name
	}
	String getSearchField() {
		if (!searchField) {
			searchField=collectField
		}
		return searchField
	}
	static constraints = {
		id(nullable: false, validator: validateInput)
		collectField(nullable:false)
		cid(nullable:true)  // what is this ?
		styles(nullable:true)
		clazz(nullable:true)
		userTemplate(nullable:true)
		value(nullable:true)
		require(nullable:true)
		required(nullable:true)
		domain(nullable:true)
	}

	static def validateInput={value,object,errors->
		if (!value) {
			return errors.rejectValue(propertyName,"invalid.$propertyName",[''] as Object[],'')
		}
	}

}
