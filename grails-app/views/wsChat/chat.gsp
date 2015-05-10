<!DOCTYPE html>
<html>
<head>
<g:if test="${!request.xhr }">
	<meta name='layout' content="achat" />
</g:if>
<g:else>
	<g:render template="/assets" />
</g:else>

<title>
	${chatTitle }
</title>
</head>
<body>
	<g:render template="chat" model="${[showtitle:showtitle, dbsupport:dbsupport , room:room,
                                       			chatuser:chatuser, chatTitle:chatTitle,chatHeader:chatHeader,
                                       			now:now, hostname:hostname, addAppName: addAppName, debug:debug]}"/>
</body>
</html>