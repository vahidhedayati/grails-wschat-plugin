<!DOCTYPE html>
<html>
<head>
	<g:if test="${!request.xhr }">
    	<meta name='layout' content="chat"/>
    </g:if>
    <g:else>
    	
     	   	    <link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
     	<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.ui.chatbox.css')}" type="text/css" >
    	<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.ui.chatbox.js')}"></script>
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
     	
     	
     	
    </g:else>
   <title>${chatTitle }</title>

</head>
<body>
<g:render template="/wsChat/${loadtemplate}" model="[hostname:hostname, chatTitle:chatTitle, chatHeader:chatHeader]"/>
</body>
</html>