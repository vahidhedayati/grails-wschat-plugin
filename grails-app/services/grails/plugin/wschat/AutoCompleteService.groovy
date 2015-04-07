package grails.plugin.wschat

import grails.converters.JSON
import grails.transaction.Transactional


class AutoCompleteService {
	
	
	
	def grailsApplication
	
	@Transactional
	def autocomplete (params) {
		def domainClass = grailsApplication.getDomainClass(params.domain).clazz
		def results = domainClass?.createCriteria().list {
			ilike params.searchField, params.term + '%'
			maxResults(Integer.parseInt(params.max,10))
			order(params.searchField, params.order)
		}
		if (results.size()< 5){
			results = domainClass?.createCriteria().list {
				ilike params.searchField, "%${params.term}%"
				maxResults(Integer.parseInt(params.max,10))
				order(params.searchField, params.order)
			}
		}
		results = results?.collect {     [label:it."${params.collectField}"] }?.unique()
		return results as JSON
	}
	
	
}
