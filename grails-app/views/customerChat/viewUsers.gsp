<!DOCTYPE html>
<html>
<head>
<g:if test="${!request.xhr }">
   	<meta name='layout' content="achat"/>
</g:if>
<g:else>
 	<g:render template="/assets"/>
</g:else>
<title>${chatTitle }</title>
</head>
<body>
<g:render template="/customerChat/viewUsers"/>
</body>
</html>
