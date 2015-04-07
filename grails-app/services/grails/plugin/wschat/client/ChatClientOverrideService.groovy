package grails.plugin.wschat.client


public class ChatClientOverrideService {
	
	static transactional  =  false

	// Override this to get value from another DB
	// like commented out example
	private String getGlobalReceiverNameFromUserId(String userId) {
		//def user=ChatUser.get(userId as Long)
		//return user.username as String
		return userId
	}
}
