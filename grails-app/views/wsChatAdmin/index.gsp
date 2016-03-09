<!DOCTYPE html>
<html>
<head>
	<title><g:message code="springSecurity.login.title"/></title>
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<meta name='layout' content="achat"/>
	<asset:javascript src="jquery.min.js" />
	<asset:javascript src="jquery-ui.min.js" />
    <asset:stylesheet src="jquery-ui.min.css" />
    <asset:stylesheet src="font-awesome.css"/>
	<asset:javascript src="bootstrap.min.js" />
    <asset:stylesheet href="bootstrap.min.css" />
    <asset:stylesheet href="chat.css" />
    <asset:stylesheet href="chat-ui.css" />
</g:if>
<g:else>
<meta name='layout' content="chat"/>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'font-awesome.css')}" type="text/css" />
<script type="text/javascript"	src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css"/>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'chat.css')}" type="text/css" media="screen" id="chat_theme" />
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'chat-ui.css')}" type="text/css"/>
</g:else>
</head>
<body>
<h1>Administration Menu</h1>
   <div class="col-md-4" >
        <div class="panel panel-default">
           <div class="panel-body">
           <g:link action="viewUsers"><g:message code="wschat.view.users" default="view users"/></g:link>
           </div>
        </div>
   </div>
    <div class="col-md-4" >
        <div class="panel panel-default">
           <div class="panel-body">
         others
           </div>
        </div>
   </div>
</body>
</html>