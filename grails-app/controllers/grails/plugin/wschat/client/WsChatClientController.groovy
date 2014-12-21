package grails.plugin.wschat.client

import grails.converters.JSON
import grails.plugin.wschat.interfaces.ClientSessions


class WsChatClientController implements ClientSessions {
	
	def index() { 
		[ clientMaster:clientMaster, clientSlave:clientSlave]
	}
	
	def masterList() {
		render clientMaster as JSON
	}
	
	def slaveList() {
		render clientSlave as JSON
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
