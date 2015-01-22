package grails.plugin.wschat.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;



public interface ChatSessions {
	static Set<HashMap<String[],String[]>> chatroomUsers1= Collections.synchronizedSet(new HashSet<HashMap<String[],String[]>>());
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());
	static final Set<Session> camsessions = Collections.synchronizedSet(new HashSet<Session>());
}
