package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.WsChatConfService


class WsChatClientController extends WsChatConfService {
	
	def wsChatClientService
	
	def index() { 
		[ clientMaster:clientMaster, clientSlave:clientSlave]
	}
	
	def masterList() {
		if (clientMaster) {
			render clientMaster as JSON
			return
		}
		render ''
	}
	
	def slaveList() {
		if (clientSlave) {
			render clientSlave as JSON
			return
		}			
		render ''
	}
	
	def showMasters() {
		render (view: 'index', model: [clientMaster:clientMaster])
	}
	
	def showSlaves() {
		render (view: 'index', model: [clientSlave:clientSlave])
	}
	
	def truncateSlaves() {
		clientSlave.clear()
		render "clientSlave truncated"
	}
	
	def truncateMasters() {
		clientMaster.clear()
		render "clientMaster truncated"
	}
	
		
}
