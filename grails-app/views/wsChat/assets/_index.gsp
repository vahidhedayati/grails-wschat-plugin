<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<asset:stylesheet href="chat.css" />
     	<asset:stylesheet href="bootstrap.min.css" />
	  	<asset:stylesheet href="jquery.ui.chatbox.css" />
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery-ui.min.js"/>
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery.min.js"/>
    	<asset:javascript src="jquery.ui.chatbox.js"/>
    	<asset:javascript src="chatboxManager.js"/>
    </g:else>
   <title>${chatTitle }</title>
</head>
<body>
<g:render template="/wsChat/${loadtemplate }"  model="[hostname:hostname, chatTitle:chatTitle, chatHeader:chatHeader]"/>
</body>
</html>
