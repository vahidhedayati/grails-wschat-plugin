<!-- The following line is essential for the "position: fixed" property to work correctly in IE -->
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
   		<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
     	<link rel="stylesheet" href="${resource(dir: 'css', file: 'jquery.ui.chatbox.css')}" type="text/css" >
    	<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.ui.chatbox.js')}"></script>
   		<script type="text/javascript" src="${resource(dir: 'js', file: 'chatboxManager.js')}"></script>
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'chat.css')}" type="text/css">
    	<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
    	<title><g:layoutTitle default="Grails"/></title>
<g:layoutHead>
		</g:layoutHead>
		<r:layoutResources />
	</head>
	<body>
		<g:layoutBody/>
		<r:layoutResources />
	</body>
</html>

