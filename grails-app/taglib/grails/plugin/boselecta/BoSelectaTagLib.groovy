package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.beans.BoBean
import grails.plugin.boselecta.beans.ConnectionBean
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.taglib.GroovyPageAttributes

class BoSelectaTagLib extends ConfService implements ClientSessions {

	static namespace  =  "bo"
	static returnObjectForTags = ['randomizeUser']
	
	def clientListenerService
	def autoCompleteService
	def randService

	
	/*
	 * Used in demo site to randomise username
	 * and keep a stale randomised user through out gsp:
	 *  user1="${bo.randomizeUser('user': 'random1') }"
	 *  <g:set var="user1" value="${bo.randomizeUser('user': 'random1') }" />
	 *  then user="${user1}" through out each call on same page
	 */
	def randomizeUser = { attrs ->
		out << randService.randomise(attrs.user?:'noUser')
	}

	/*
	 * <bo:connect
	 * Master page connection that handles all the I/O
	 * for each call below
	 */
	def connect  =  { attrs ->
		def cBean = new ConnectionBean(attrs)
		if (!cBean.validate()) {
			cBean.errors.allErrors.each {err ->
				throwTagError("Tag [connect] is missing required attribute [${err.field}]")
			}
		}
		// Make a socket connection as actual main user (backend connection)
		Session oSession = clientListenerService.p_connect(cBean)
		loadTemplate(attrs,'socketConnect', cBean)
		if (cBean.autodisco) {
			clientListenerService.disconnect(oSession)
		}
		loadTemplate(attrs,'socketProcess', cBean)
	}
	
	/*
	 * Main call locked down up to to domain2
	 * If you wish to expand depth use <bo:selecta2 and keep everything else the same
	 * this ties in the BoBean and validates input to ensure nothing extra gets in
	 */
	def selecta = {attrs ->
		def bo = new BoBean(attrs)
		if (!bo.validate()) {
			bo.errors.allErrors.each {err ->
				throwTagError("Tag [selecta] is missing required attribute [${err.field}]")
			}
		}
		if ((bo.domain) && (bo.bindid && (bo.bindid.endsWith('.id'))||(bo.norefPrimary))) {
			bo.loadPrimary = true
			if (bo.formatting == "JSON") {
				bo.primarylist = autoCompleteService.returnPrimaryList(bo.domain, bo.searchField, bo.collectField)
			}else{
				bo.primarylist = autoCompleteService.returnPrimaryList(bo.domain)
			}
		}
		//AutoComplete
		if (bo.autoComplete) {
			loadTemplate(attrs,'genAutoComplete', bo)
		//Select
		}else{
			def gsattrs=[optionKey: bo.collectField , optionValue: bo.searchField, id: bo.id, value: bo.value, name: bo.name]
			gsattrs['noSelection'] = attrs.noSelection
			gsattrs['from'] = []
			if (bo.loadPrimary && (bo.formatting != "JSON")) {
				gsattrs['from'] = bo.primarylist
			}
			if (bo.requireField) {
				gsattrs['required'] = 'required'
			}
			if (bo.selectToAutoComplete) {
				// auto complete javascript front end action
				gsattrs['onchange'] = "javascript:updateList(this.value, '${bo.id}',  '${bo.sdataList}', '${bo.setId}');"
			}else{
				//select javascript front end action
				gsattrs['onchange'] = "javascript:actionThis(this.value, '${bo.setId}');"
			}
			out << g.select(gsattrs)
		}
		// Generate Message which is initial map containing default containing results to append
		def message = [setId: bo.setId, secondary: bo.domain2, domainDepth: bo.domainDepth,  primaryCollect: bo.collectField,
			collectfield: bo.collectField2,	primarySearch: bo.searchField, searchField:  bo.searchField2, appendValue: bo.appendValue,
			appendName: bo.appendName, job:bo.job, formatting:bo.formatting, nextValue:bo.nextValue, primary: bo.domain, max:bo.max,
			order:bo.order, cId: bo.id, autoCompletePrimary:bo.autoCompletePrimary, dataList:bo.dataList, sdataList:bo.sdataList]

		if (bo.bindid) {
			message.put('bindId', bo.bindid)
		}
		def multiDomainMap = createDomainMap(attrs,bo.domainDepth)
		if (multiDomainMap) {
			message += multiDomainMap
		}
		def cc=message as JSON
		clientListenerService.sendBackPM(bo.user, cc as String)
		if (bo.value||bo.nextValue) {
			Map map = [value: bo.value, setId:bo.setId, user:bo.user, job:bo.job]
			loadTemplate(attrs,'actionNonAppendThis', map)
		}
		if (bo.loadPrimary && (bo.formatting == "JSON")) {
			Map jsonMap = [primarylist: bo.primarylist, id:bo.id, formatting: bo.formatting, updateValue:bo.value]
			loadTemplate(attrs,'primaryJSONParser', [message: jsonMap as JSON])
		}
	}

	/*
	 * <bo:selecta2
	 * infinite depth :- for more secure locked down call use:
	 * <bo:selecta which verifies inputvia BoBean. Only supports up to domain2
	 */
	def selecta2 = {attrs ->
		String job = attrs.remove('job')?.toString()
		String user = attrs.remove('user')?.toString()
		String id = attrs.remove('id')?.toString()
		String domain = attrs.remove('domain')?.toString()
		String domain2 = attrs.remove('domain2')?.toString()
		String bindid = attrs.remove('bindid')?.toString()
		String searchField = attrs.remove('searchField')?.toString()
		String searchField2 = attrs.remove('searchField2')?.toString()
		String collectField = attrs.remove('collectField')?.toString()
		String collectField2 = attrs.remove('collectField2')?.toString()
		String name = attrs.remove('name')?.toString()
		String setId = attrs.remove('setId')?.toString()
		String appendValue = attrs.remove('appendValue')?.toString()
		String appendName = attrs.remove('appendName')?.toString()
		String value = attrs.remove('value')?.toString()
		String nextValue = attrs.remove('nextValue')?.toString()
		String placeHolder = attrs.remove('placeHolder')?.toString()
		String hiddenField = attrs.remove('hiddenField')?.toString()
		String jsonField = attrs.remove('jsonField')?.toString()
		
		int domainDepth=1
		if (attrs.domainDepth) {
			domainDepth+=attrs.domainDepth as int
		}else{
		 	domainDepth=depth
		}
		String max = attrs.remove('max')?.toString()
		String order = attrs.remove('order')?.toString()
		boolean norefPrimary = attrs.remove('norefPrimary')?.toBoolean() ?: false
		boolean autoComplete = attrs.remove('autoComplete')?.toBoolean() ?: false
		boolean autoCompletePrimary = attrs.remove('autoCompletePrimary')?.toBoolean() ?: false
		boolean selectToAutoComplete = attrs.remove('selectToAutoComplete')?.toBoolean() ?: false
		boolean autoCompleteToSelect = attrs.remove('autoCompleteToSelect')?.toBoolean() ?: false
		// Format can be set as JSON
		String formatting = attrs.remove('formatting')?.toString() ?: config.formatting ?: 'none'
		boolean require = attrs.remove('require')?.toBoolean() ?: false
		if (!id) {
			throwTagError("Tag [multiSelect] is missing required attribute [id]")
		}
		searchField2 = searchField2 ?: searchField
		if (!searchField2) {
			throwTagError("Tag [multiSelect] is missing required attribute [searchField]")
		}
		collectField2 = collectField2 ?: collectField
		if (!collectField2) {
			collectField2 = searchField2
		}
		if (!collectField) {
			collectField=collectField2
		}
		if (!searchField) {
			searchField=searchField2
		}
		if (!setId) {
			setId = "selectPrimary"
		}
		if (!name) {
			name = id
		}
		if ((appendValue)&&(!appendName)) {
			appendName='Values Updated'
		}
		Boolean requireField=true
		if (require) {
			requireField=require
		}
		List primarylist = []
		boolean loadPrimary = false
		if ((domain) && (bindid && (bindid.endsWith('.id'))||(norefPrimary))) {
			loadPrimary = true
			if (formatting == "JSON") {
				primarylist = autoCompleteService.returnPrimaryList(domain, searchField, collectField)
			}else{
				primarylist = autoCompleteService.returnPrimaryList(domain)
			}
		}
		
		def multiDomainMap
		String dataList = "${id}-datalist"
		String sdataList = "${setId}-datalist"
		// AutoComplete box
		if (autoComplete) {
			Map map = [value: value, setId:setId, user:user, job:job, domainDepth: domainDepth, name:name, dataList:dataList,
				searchField:searchField, collectField: collectField, hiddenField:hiddenField, jsonField: jsonField,formatting:formatting,
				 id:id, placeHolder:placeHolder, sdataList:sdataList, autoCompleteToSelect:autoCompleteToSelect]
			loadTemplate(attrs,'genAutoComplete', map)
		//select function
		}else{
			def gsattrs=[optionKey: collectField , optionValue: searchField, id: id, value: value, name: name]
			gsattrs['noSelection'] = attrs.noSelection
			gsattrs['from'] = []
			if (loadPrimary && (formatting != "JSON")) {
				gsattrs['from'] = primarylist
			}
			if (requireField) {
				gsattrs['required'] = 'required'
			}
			if (selectToAutoComplete) {
				gsattrs['onchange'] = "javascript:updateList(this.value, '${id}',  '${sdataList}', '${setId}');"
			}else{
				gsattrs['onchange'] = "javascript:actionThis(this.value, '${setId}');"
			}
			multiDomainMap = createDomainMap(attrs,domainDepth)
			out << g.select(gsattrs)
		}
		def message = [setId: setId, secondary: domain2, domainDepth: domainDepth,  primaryCollect: collectField, collectfield: collectField2,	
			primarySearch: searchField,	searchField:  searchField2, appendValue: appendValue, appendName: appendName, job:job, 
			formatting:formatting, nextValue:nextValue,	primary: domain, max:max, order:order, cId: id,	
			autoCompletePrimary:autoCompletePrimary, dataList:dataList, sdataList:sdataList]
		if (bindid) {
			message.put('bindId', bindid)
		}
		if (multiDomainMap) {
			message += multiDomainMap
		}
		def cc=message as JSON
		clientListenerService.sendBackPM(user, cc as String)
		if (value||nextValue) {
			Map map = [value: value, setId:setId, user:user, job:job]
			loadTemplate(attrs,'actionNonAppendThis', map)
		}
		if (loadPrimary && (formatting == "JSON")) {
			Map jsonMap = [primarylist: primarylist, id:id, formatting: formatting, updateValue:value]
			loadTemplate(attrs,'primaryJSONParser', [message: jsonMap as JSON])
		}
	}

	
	/*  loadTemplate figures out if user has provided a template or if to load from
	 *  within plugin
	 *  Moved to gsp templates - so that you can override given template name
	 */
	private void loadTemplate(attrs, String template, def cBean=null, Map myMap=null) {
		def userTemplate = attrs."${template}" ?: config."${template}"
		def defaultTemplate = "/${VIEW}/${template}"
		if (userTemplate) {
			out << g.render(template:userTemplate, model: [instance:cBean?:myMap])
		}else{
			out << g.render(contextPath: pluginContextPath, template: defaultTemplate, model: [instance:cBean?:myMap])
		}
	}

	/*
	 *  creatDomainMap is a dynamic and append to attrs.
	 *  
	 */
	private Map createDomainMap(GroovyPageAttributes attrs,def domainDepth) {
		int a=3
		def multiDomainMap = [:]
		while (a < (domainDepth as int) ) {
			String sId = attrs.remove('setId'+a)?.toString()
			String sf=attrs.remove('searchField'+a)?.toString()
			String cf=attrs.remove('collectField'+a)?.toString()
			String dom = attrs.remove('domain'+a)?.toString()
			String bindit = attrs.remove('bindid'+a)?.toString()
			if (sId && dom) {
				multiDomainMap+=[ "setId${a}": sId, "searchField${a}" : sf, "collectfield${a}": cf,
					"domain${a}": dom, "bindId${a}": bindit]
			}
			a++
		}
		return multiDomainMap
	}

}
