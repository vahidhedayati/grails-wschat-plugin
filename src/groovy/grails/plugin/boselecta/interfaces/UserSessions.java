package grails.plugin.boselecta.interfaces;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;



public interface UserSessions {
	static final ConcurrentMap<String, Session> jobUsers = new ConcurrentHashMap<String, Session>();
}
