package grails.plugin.wschat.interfaces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.websocket.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public interface UserMaps {
	public static final ConcurrentMap<String, Map<String, Session>> chatroomUsers = new ConcurrentHashMap<String, Map<String,Session>>();
	public static final ConcurrentMap<String, Session> camUsers = new ConcurrentHashMap<String, Session>();
	public static final ConcurrentMap<String, Session> fileroomUsers = new ConcurrentHashMap<String, Session>();

	public static Set<HashMap<String,String>> clientMaster = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
	public static final Set<HashMap<String,String>> clientSlave = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
}
