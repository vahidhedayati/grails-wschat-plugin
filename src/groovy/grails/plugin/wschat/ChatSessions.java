package grails.plugin.wschat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;

public interface ChatSessions {
	/*
	 * private static List camusers = Collections.synchronizedList(new ArrayList())
	 * private static List camusers = Collections.synchronizedList(new ArrayList())
	 * static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>())
	 * static final Set<Session> camsessions = Collections.synchronizedSet(new HashSet<Session>())
	 * Map<String,String> chatroomUsers = new ConcurrentHashMap<String,String>()
	 * List<String> chatroomUsers = new CopyOnWriteArrayList<String>()
	*/
	
	//static final Set<Session> camsessions = ([] as Set).asSynchronized();
	//static final Set<Session> chatroomUsers = ([] as Set).asSynchronized();
	static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());
	static final Set<Session> camsessions = Collections.synchronizedSet(new HashSet<Session>());
}
