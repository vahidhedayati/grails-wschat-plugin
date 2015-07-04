<!DOCTYPE html>
<html>
<head>
<g:render template="includes" model="${[bean:bean]}"/>
<title>
	${chatTitle ?: 'Error'}
</title>
</head>
<body>

	<g:if test="${startDate }">
Booking has expired or has not started<br/>
Booking is  between ${startDate } - ${endDate }
	</g:if>
	<g:else>
Something has gone wrong - booking has not been accepted.
</g:else>
</body>
</html>