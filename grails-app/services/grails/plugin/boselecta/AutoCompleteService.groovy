package grails.plugin.boselecta

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject

import grails.transaction.Transactional

class AutoCompleteService {

	def grailsApplication

	// No reference selection method i.e. belongsTo=UpperClass
	@Transactional
	ArrayList selectNoRefDomainClass(String domainClaz, String domainClaz2, String searchField, String collectField, String bindName, String recordId) {
		def primarySelectList = []
		if ((domainClaz2) && (domainClaz) &&( recordId)) {
			def domainClass2 = grailsApplication?.getDomainClass(domainClaz2)?.clazz
			def domainClass = grailsApplication?.getDomainClass(domainClaz)?.clazz
			def domaininq=domainClass?.get(parseRecord(recordId).toLong())
			if (domaininq) {
				domaininq."${bindName}".each { dq ->
					def primaryMap = [:]
					primaryMap.put('id',dq."${collectField}")
					primaryMap.put('name', dq."${searchField}")
					primaryMap.put('resarray', [selected: dq."${searchField}", selectedText:dq."${collectField}"])
					primarySelectList.add(primaryMap)
				}
			}
		}
		return primarySelectList
	}

	ArrayList selectDomainClass(String domainClaz, String searchField, String collectField, String bindName, String recordId) {
		def primarySelectList=[]
		if (domainClaz && bindName) {
			def domainClass = grailsApplication?.getDomainClass(domainClaz)?.clazz
			def query = domainClass.withCriteria {
				eq  (bindName as String,  parseRecord(recordId).toLong())
				projections {
					property(collectField)
					property(searchField)
				}
				order(searchField)
			}
			if (query) {
				primarySelectList=resultSet(query as List)
			}
		}
		return primarySelectList
	}

	def resultSet(def results) {
		def primarySelectList=[]
		if (results) {
			results.each {
				def primaryMap = [:]
				primaryMap.put('id', it[0])
				primaryMap.put('name', it[1])
				primaryMap.put('resarray', [selectedText: it[0], selected:it[1]])
				primarySelectList.add(primaryMap)
			}
		}
		return primarySelectList
	}

	List returnPrimaryList(String className) {
		if (!className.equals('')) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			clazz?.list()
		}
	}
	
	@Transactional
	def returnAutoList(String className, String searchField, String collectField) {
		def results
		if (className) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			def res = clazz.findAll()
			results = res?.collect {[	'id': it."${collectField}", 'name': it."${searchField}" ,
					'resarray': [selected: it."${collectField}",  selectedText:it."${searchField}" ]]}?.unique()
			return results
		}
	}

	private String parseRecord(String input) {
		String cId = input
		if (input.startsWith('{')) {
			JSONObject res=JSON?.parse(input)
			if (res) {
				cId = res?.get('selected')
			}
		}
		return cId
	}

	@Transactional
	def returnPrimaryList(String className,String searchField, String collectField ) {
		def results
		if (className) {
			Class clazz = grailsApplication?.getDomainClass(className)?.clazz
			def res = clazz.findAll()
			results = res?.collect {[	'id': it."${collectField}", 'name': it."${searchField}" ,
					'resarray': [selected: it."${collectField}", selectedText: it."${searchField}" ]]}?.unique()
			return results
		}
	}
}
