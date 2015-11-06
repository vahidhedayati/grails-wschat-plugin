package grails.plugin.boselecta

import grails.plugin.boselecta.interfaces.UserSessions

import java.util.concurrent.ConcurrentMap

import javax.websocket.Session



class ConfService implements UserSessions {

	static transactional  =  false

	def grailsApplication


	public ConcurrentMap<String, Session> getJobNames() {
		return jobUsers
	}

	public Collection<String> getJobData() {
		return Collections.unmodifiableSet(jobUsers.keySet())
	}
	
	public Session getJobUser(String job) {
		Session userSession = jobUsers.get(job)
		return userSession
	}
	
	public boolean jobUserExists(String username) {
		return jobData.contains(username)
	}

	public boolean destroyJobUser(String username) {
		return jobUsers.remove(username) != null
	}
	
	boolean isConfigEnabled(String input) {
		return Boolean.valueOf(input ?: false)
	}

	Integer getDepth() {
		return (config.depth ?: '4') as int
	}

	String getFrontend() {
		return config.frontenduser ?: '_frontend'
	}

	String getAppName(){
		String addAppName = config.add.appName ?: 'yes'
		if (addAppName) {
			grailsApplication.metadata['app.name']+"/"
		}else{
			return
		}
	}

	public Map<String, String> parseInput(String mtype,String message){
		def p1 = mtype
		def mu = message.substring(p1.length(),message.length())
		def msg
		def user
		def resultset = []
		if (mu.indexOf(",")>-1) {
			user = mu.substring(0,mu.indexOf(","))
			msg = mu.substring(user.length()+1,mu.length())
		}else{
			user = mu.substring(0,mu.indexOf(" "))
			msg = mu.substring(user.length()+1,mu.length())
		}
		Map<String, String> values  =  new HashMap<String, Double>();
		values.put("user", user);
		values.put("msg", msg);
		return values
	}

	def getConfig() {
		grailsApplication?.config?.boselecta
	}

}
