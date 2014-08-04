<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	 <asset:stylesheet href="chat.css" />
    	 <asset:stylesheet href="bootstrap.min.css" />
    </g:else>
   <title>${chatTitle }</title>
   <g:javascript library="jquery"/>
</head>
<body>

<g:render template="/wsChat/chatpage" model="[hostname:hostname, chatTitle:chatTitle, chatHeader:chatHeader]"/>

</body>
</html>
