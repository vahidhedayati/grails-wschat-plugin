package grails.plugin.wschat.interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface ClientSessions {
	
	static final String CONNECTOR = "CONN:-";
	static final String DISCONNECTOR = "DISCO:-";
	static final String CHATAPP = "WsChatEndpoint";
	static final String CHATVIEW = "wsChat";
	
	static Set<HashMap<String,String>> clientMaster = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
	static final Set<HashMap<String,String>> clientSlave = Collections.synchronizedSet(new HashSet<HashMap<String,String>>());
	
}
