package grails.plugin.wschat

import grails.converters.JSON


class AutoCompleteService {
	
	static transactional  =  false
	
	def autocomplete (params) {
		def results = ChatUser?.createCriteria().list {
			ilike 'username', params.term + '%'
			maxResults(Integer.parseInt(params.max,10))
			order(params.searchField, params.order)
		}
		if (results.size()< 5){
			results = ChatUser?.createCriteria().list {
				ilike 'username', "%${params.term}%"
				maxResults(Integer.parseInt(params.max,10))
				order('username', 'asc')
			}
		}
		results = results?.collect {[label:it.username]}?.unique()
		return results as JSON
	}
	
	
}
