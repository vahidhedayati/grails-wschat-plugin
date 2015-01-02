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
    
   <title>${chatTitle ?: 'Error'}</title>
</head>
<body>

<g:if test="${startDate }">
Booking has expired - was valid between ${startDate } - ${endDate }
</g:if>
<g:else>
Something has gone wrong - booking has not been accepted.
</g:else>
</body>
</html>