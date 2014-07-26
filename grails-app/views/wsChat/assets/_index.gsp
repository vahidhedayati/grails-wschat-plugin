<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	 <asset:stylesheet href="application.css" />
    </g:else>
   <title>${chatTitle }</title>
   <g:javascript library="jquery"/>
</head>
<body>
<g:render template="/wsChat/indexpage"/>
</body>
</html>
