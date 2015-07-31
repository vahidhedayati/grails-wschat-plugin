package grails.plugin.wschat.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

public interface UserMaps {
	static final ConcurrentMap<String, Map<String, Session>> chatroomUsers = new ConcurrentHashMap<String, Map<String,Session>>();
	static final ConcurrentMap<String, Session> camUsers = new ConcurrentHashMap<String, Session>();
	static final ConcurrentMap<String, Session> fileroomUsers = new ConcurrentHashMap<String, Session>();			
	static Set<HashMap<String,String>> clientMaster = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
	static final Set<HashMap<String,String>> clientSlave = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
}
