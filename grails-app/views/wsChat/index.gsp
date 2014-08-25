<!DOCTYPE html>
<html>
<head>

<g:if test="${enduser?.verifyAppVersion().equals('resources')}">
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
   	    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
     	<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.ui.chatbox.css')}" type="text/css" >
    	<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.ui.chatbox.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'chatboxManager.js')}"></script>
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
    
    </g:else>
</g:if>
<g:else>
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
