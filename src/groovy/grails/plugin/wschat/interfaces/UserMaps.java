package grails.plugin.wschat.interfaces;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

public interface UserMaps {
	static final ConcurrentMap<String, Session> chatroomUsers = new ConcurrentHashMap<String, Session>();
	static final ConcurrentMap<String, Session> camUsers = new ConcurrentHashMap<String, Session>();
			

}
