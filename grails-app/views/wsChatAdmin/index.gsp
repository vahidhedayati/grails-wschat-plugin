<!DOCTYPE html>
<html>
<head>
	<title><g:message code="wschat.signup.title" default="signup page"/></title>
	<meta name='layout' content="achat"/>
	<asset:javascript src="jquery.min.js" />
	<asset:javascript src="jquery-ui.min.js" />
    <asset:stylesheet src="jquery-ui.min.css" />
    <asset:stylesheet src="font-awesome.css"/>
	<asset:javascript src="bootstrap.min.js" />
    <asset:stylesheet href="bootstrap.min.css" />
    <asset:stylesheet href="chat.css" id="chat_theme" />
    <asset:stylesheet href="chat-ui.css" />
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