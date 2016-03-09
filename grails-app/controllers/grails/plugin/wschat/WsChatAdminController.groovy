package grails.plugin.wschat

import grails.plugin.springsecurity.annotation.Secured
import grails.plugin.wschat.beans.SearchBean

class WsChatAdminController extends WsChatConfService {

	def wsChatUserService
	def springSecurityService
	def wsChatContService
	//---------------------- Start Index
	/**
	 * index default action checks if configuration has enabled security
	 * if so sent to authIndex
	 */
	def index() {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderIndex()
	}
	@Secured(['ROLE_ADMIN'])
	def authIndex() {
		session.wschatuser=springSecurityService.currentUser as String
		renderIndex()
	}

	private void renderIndex() {
		if (session.wschatuser && isAdmin) {
			render(view: 'index')
			return
		}
		render 'Not authorized'
	}
	//---------------------- End Index



	def viewUsers(SearchBean bean) {
		if (wsconf.enableSecurity) {
			redirect(controller: "${controllerName}",action: "auth${upperCaseFirst(actionName)}")
			return
		}
		renderViewUsers(bean)
	}
	@Secured(['ROLE_ADMIN'])
	def authViewUsers(SearchBean bean) {
		session.wschatuser=springSecurityService.currentUser as String
		renderViewUsers(bean)
	}

	private void renderViewUsers(SearchBean bean) {
		if (isAdmin) {
			bean.uList = wsChatUserService.genAllUsers()
			def gUsers = wsChatContService.viewUsers(bean.s ?: '', bean.sortby, bean.order, bean.offset, bean.max , bean.inputid)
			bean.s = gUsers.s
			bean.userList = gUsers.foundRec
			bean.userListCount = gUsers.total
			bean.allcat=ChatUser.list()
			Map model = [bean:bean]
			if (request.xhr) {
				render (template: '/admin/viewUsers', model: model)
			} else {
				render (view: '/admin/viewUsers', model: model)
			}
			return
		}
		render 'not Authorized'
	}



	private Boolean getIsAdmin() {
		wsChatUserService.validateAdmin(session.wschatuser)
	}

	private String upperCaseFirst(String s) {
		s.substring(0,1).toUpperCase() + s.substring(1)
	}
}