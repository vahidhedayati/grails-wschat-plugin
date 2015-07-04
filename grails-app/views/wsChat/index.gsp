<!DOCTYPE html>
<html>
<head>

<g:render template="includes" model="${[bean:bean]}"/>

<title>
	${bean.chatTitle }
</title>
</head>
<body>
	<g:if test="${flash.message}">
		<div class="message" role="status">
			${flash.message}
		</div>
	</g:if>
	<div class="container">
		<div id="pageHeader" class="page-header2">
			<h2>
				${bean.chatHeader }
			</h2>
			<small> ${bean.now}
			</small>
		</div>
		<g:form id="form1" method="post" controller="wsChat" action="login">
			<div class="form-group">
				<div class="nickname">Nickname:</div>
				<input type="text" name="username">
				
				<g:select name="room" from="${rooms}" />
				
				<input type="submit" value="login">

			</div>
		</g:form>
	</div>
</body>
</html>
