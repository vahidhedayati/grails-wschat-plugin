package grails.plugin.wschat

import grails.plugin.springsecurity.annotation.Secured

class WsChatAdminController extends WsChatConfService {
	@Secured(['ROLE_ADMIN'])
	def index() {
		render 'this is the index'
	}

	def index2() {
		render "hello"
	}
}