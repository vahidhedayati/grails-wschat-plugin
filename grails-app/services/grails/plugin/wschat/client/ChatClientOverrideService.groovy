package grails.plugin.wschat.client

import grails.plugin.wschat.interfaces.ClientSessions

public class ChatClientOverrideService {


	// Override this to get value from another DB
	// like commented out example
	private String getGlobalReceiverNameFromUserId(String userId) {
		//def user=ChatUser.get(userId as Long)
		//return user.username as String
		return userId
	}
}
