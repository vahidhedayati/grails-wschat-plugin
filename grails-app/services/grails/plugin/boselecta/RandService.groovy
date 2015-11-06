package grails.plugin.boselecta

import java.rmi.server.UID
import java.security.SecureRandom


class RandService {

	static transactional  =  false
	
	static prng = new SecureRandom()
	
	String randomise(String user) {
		return (user + new UID().toString() + prng.nextLong() +
			 System.currentTimeMillis()).toString()
			 .replaceAll('[^a-zA-Z0-9[:space:]]','')
	}
	
	String shortRand(String user) {
		return (user+prng.nextLong()).replaceAll('[^a-zA-Z0-9[:space:]]','')
	}
	
}
