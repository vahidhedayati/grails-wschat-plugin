package grails.plugin.boselecta

import grails.converters.JSON
import grails.plugin.boselecta.interfaces.ClientSessions

import javax.websocket.Session

import org.codehaus.groovy.grails.web.json.JSONObject

public class ClientProcessService extends ConfService implements ClientSessions {

	static transactional  =  false
	
	def clientListenerService
	def autoCompleteService

	public void processResponse(Session userSession, String message) {
		String username = userSession.userProperties.get("username") as String
		boolean disco = true
		if (message.startsWith("/pm")) {
			def values = parseInput("/pm ",message)
			String user = values.user as String
			String msg = values.msg as String
			if (user == username) {
				checkMessage(userSession, username, msg)
			}
		}else if (message.startsWith('{')) {
			JSONObject rmesg=JSON.parse(message)

			checkMessage(userSession, username, rmesg)

			String actionthis=''
			String msgFrom = rmesg.msgFrom
			boolean pm = false

			String disconnect = rmesg.system
			if (rmesg.privateMessage) {
				JSONObject rmesg2=JSON.parse(rmesg.privateMessage)
				checkMessage(userSession, username, rmesg2)
				String command = rmesg2.command
			}
			if (disconnect && disconnect == "disconnect") {
				clientListenerService.disconnect(userSession)
			}
			if (msgFrom ) {
				actionthis = rmesg.privateMessage
				pm = true
			}

			def rmessage = rmesg.message
			if (rmessage) {
				def matcher = (rmessage =~ /(.*): (.*)/)
				if (matcher.matches()){
					msgFrom = matcher[0][1]
					if (msgFrom) {
						actionthis = matcher[0][2]
					}
				}
			}

			if (actionthis) {
				if (actionthis == 'close_connection') {
					clientListenerService.disconnect(userSession)
				}else if (actionthis == 'close_my_connection') {
					if (pm) {
						clientListenerService.sendPM(userSession, msgFrom,"close_connection")
					}
				}else{
					if (!msgFrom.endsWith(username)) {
						clientListenerService.sendBackEndPM(userSession, username,actionthis)
					}
					if (disco) {
						clientListenerService.disconnect(userSession)
					}
				}
			}
		}
	}

	def checkMessage(Session userSession, String username, JSONObject rmesg) {
		// Initial connection
		String secondary = rmesg.secondary
		String primary = rmesg.primary
		String collectfield = rmesg.collectfield
		String searchField = rmesg.searchField


		String primaryCollect = rmesg.primaryCollect
		String primarySearch = rmesg.primarySearch


		String  setId = rmesg.setId
		String formatting = rmesg.formatting
		String bindId = rmesg.bindId
		String appendValue = rmesg.appendValue ?: ''
		String appendName = rmesg.appendName ?: ''
		//String jobName = rmesg.job

		// Related to auto complete - initial map
		String order = rmesg.order ?: 'DESC'
		String max = rmesg.max ?: '10'

		//Return via Javascript upon click
		//String cjobName = rmesg.cjobName
		String updateValue = rmesg.updateValue
		String updateDiv = rmesg.updateDiv
		String updated = rmesg.updated ?: 'yes'
		String nextValue = rmesg.nextValue ?: ''
		Integer domainDepth = rmesg.domainDepth?: depth

		// Related to Auto complete:
		String updateList = rmesg.updateList
		String updateAutoValue = rmesg.updateAutoValue  // params.term
		String cId = rmesg.cId

		String sdataList = rmesg.sdataList
		String dataList = rmesg.dataList

		boolean autoCompletePrimary = rmesg?.autoCompletePrimary?.toBoolean() ?: false

		if (setId) {
			Set<HashMap<String,String>> storedMap = ([] as Set).asSynchronized()
			//Set<HashMap<String,String>> storedMap= Collections.synchronizedSet(new HashSet<HashMap<String,String>>())
			def myMap = [jobUser:username,  setId: setId,  domainDepth:domainDepth, secondary: secondary,collectfield:collectfield, searchField:searchField, bindId:bindId,
				appendValue:appendValue, primary:primary, appendName:appendName, nextValue:nextValue, formatting:formatting, order: order,
				max:max, dataList:dataList, sdataList:sdataList, primaryCollect:primaryCollect, primarySearch: primarySearch]
			
			for (int a=3; a <= domainDepth; a++ ) {
				myMap += [("setId${a}"): rmesg."setId${a}", ("domain${a}"): rmesg."domain${a}",("domainDepth${a}"):domainDepth,
					("searchField${a}"): rmesg."searchField${a}", ("collectfield${a}"): rmesg."collectfield${a}",
					("bindId${a}"): rmesg."bindId${a}"]
			}
			def myMaper = userSession.userProperties.get("currentMap")
			storedMap.add(myMap)
			if (myMaper) {
				myMaper.each { ss->
					storedMap.add(ss)
				}
			}
			userSession.userProperties.put("currentMap", storedMap)
			if (autoCompletePrimary) {
				def result = autoCompleteService.returnAutoList(primary, primarySearch, primaryCollect)

				Map mresult = [autoResult: result,updateThisDiv: cId, appendName: appendName, appendName: appendName, nextValue:nextValue, domainDepth:domainDepth,
					updated:updated, updateValue:updateValue, formatting:formatting, cId:cId, updateList:dataList, updateAutoValue:updateAutoValue]

				sleep(2000)

				clientListenerService.sendFrontEndPM(userSession, username,(mresult as JSON).toString())
			}
		}else if (updateValue) {
			def myMaper = userSession.userProperties.get("currentMap")
			Map currentSelection = [:]
			if (myMaper) {
				boolean go = false
				myMaper.each { s ->
					go = false
					if ((s.setId == updateDiv)&&(s.jobUser==parseFrontEnd(username)))  {
						go = true
						secondary = s.secondary
						collectfield = s.collectfield
						searchField = s.searchField
						bindId = s.bindId
						appendValue = s.appendValue
						appendName = s.appendName
						formatting = s.formatting
						nextValue = s.nextValue
						primary = s.primary
						dataList = s.dataList
						sdataList = s.sdataList
						domainDepth=s.domainDepth
					}
					if (go) {
						ArrayList result
						if (bindId.endsWith('.id')) {
							result = autoCompleteService.selectDomainClass(secondary, collectfield, searchField, bindId, updateValue )
						}else{
							result = autoCompleteService.selectNoRefDomainClass(primary, secondary, collectfield, searchField, bindId, updateValue )
						}
						Map mresult = [result: result,updateThisDiv: updateDiv, appendName: appendName, appendName: appendName,
							nextValue:nextValue,updated:updated, updateValue:updateValue, formatting:formatting, cId:cId, domainDepth:domainDepth]
				
						for (int a=3; a <= domainDepth; a++ ) {
							if (mapValue(s, "domain${a}")) {
								def res
								if (mapValue(s, "bindId${a}").endsWith('.id')) {
									res = autoCompleteService.selectDomainClass(mapValue(s, "domain${a}"), mapValue(s, "collectfield${a}"),
											mapValue(s, "searchField${a}"), mapValue(s, "bindId${a}"), updateValue )
								}else{
									res = autoCompleteService.selectNoRefDomainClass(primary,  mapValue(s, "domain${a}"), mapValue(s, "collectfield${a}"),
											mapValue(s, "searchField${a}"), mapValue(s, "bindId${a}"), updateValue  )
								}
								//res << ["appendName${a}":appendName,"appendValue${a}":appendValue] 
								if (res) {
									mresult.put("result${a}", res)
									mresult.put("setId${a}", mapValue(s, "setId${a}"))
									mresult.put("appendValue${a}", appendValue)
									mresult.put("appendName${a}", appendName)
									mresult.put("domainDepth${a}", domainDepth)
								}
							}
						}
						clientListenerService.sendFrontEndPM(userSession, username,(mresult as JSON).toString())
					}
				}
			}
			// Auto complete segment
		}else if (updateAutoValue) {
			def myMaper = userSession.userProperties.get("currentMap")
			Map currentSelection = [:]
			if (myMaper) {
				boolean go = false
				
				myMaper.each { s ->
					go = false
					if (s.setId == updateDiv) {
						go = true
						setId = s.setId
						secondary = s.secondary
						collectfield = s.collectfield
						searchField = s.searchField
						bindId = s.bindId
						appendValue = s.appendValue
						appendName = s.appendName
						formatting = s.formatting
						nextValue = s.nextValue
						primary = s.primary
						order = s.order
						max = s.max
						cId = s.cId
						dataList = s.dataList
						sdataList = s.sdataList
						primaryCollect = s.primaryCollect
						primarySearch = s.primarySearch
					}
					if (go && secondary) {
						def res
						if (bindId.endsWith('.id')) {
							res = autoCompleteService.selectDomainClass(secondary,  searchField,  collectfield, bindId, updateAutoValue)
						}else{
							if (primary && primary != 'null') {
								res = autoCompleteService.selectNoRefDomainClass(primary, secondary, collectfield, searchField, bindId)
							}
						}

						Map mresult = [autoResult: res,  setId: setId, updateThisDiv: updateDiv, cId: cId,  appendName: appendName,
							appendName: appendName, nextValue:nextValue,updated:updated, updateValue:updateValue, formatting:formatting,
							updateList:updateList]

						clientListenerService.sendFrontEndPM(userSession, username,(mresult as JSON).toString())

					}
				}
			}

		}
	}

	private String parseFrontEnd(String username) {
		if (username.endsWith(frontend)) {
			username=username.substring(0, username.indexOf(frontend))
		}
		return username
	}

	private String mapValue(Map s, String search) {
		def se= s.find{ it.key  == search}
		if (se) {
			return se.value as String
		}
	}
}

