<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('resources')}">
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
   		<g:render template="/resources"/>
   	 </g:else>
</g:if>
<g:else>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="achat"/>
    </g:if>
    <g:else>
    	<g:render template="/assets"/>
    </g:else>
</g:else>    
    
   <title>${chatTitle }</title>
</head>
<body>
<div class="container" >
<div id="pageHeader" class="page-header2">
	<h2>${chatHeader }</h2>
	<small>  
		${now}
	</small>
	</div>
		<form id="form1" method="post" action="login">
	 	<div class="form-group">
		<div class="nickname">Nickname: </div>
			<input type="text" name="username">
			<input type="submit" value="login" >
		</div>
	</form>
	</div>

</body>
</html>
