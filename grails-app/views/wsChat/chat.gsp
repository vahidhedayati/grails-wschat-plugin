<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<g:render template="/assets"/>
    </g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
   		<g:render template="/resources"/>
   	 </g:else>
</g:else>    
    
   <title>${chatTitle }</title>
</head>
<body>
<g:render template="chat"/>
</body>
</html>