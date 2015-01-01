
<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3></div>
</div>

	<div class="container">
<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'conferenceName', 'error')} required">
<label for="manager">
<g:message code="dateTime.label" default="Conference Name" />
</label>
	<input type="text" name="conferenceName" id="conferenceName" value="" />
</div>

<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'masterUser', 'error')}">
<label for="masterUser">
<g:message code="wschat.room.owner.label" default="Room owner" />
</label>
	
<chat:complete 
id="masterUser" name="masterUser"
domain='grails.plugin.wschat.ChatUser'
searchField='username'
collectField='username'
value=''/>
</div>

<div id="selectedValues">
</div>

<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'dateTime', 'error')} required">
<label for="manager">
<g:message code="dateTime.label" default="Conference Schedule date/Time" />
</label>
	<input type="text" name="dateTime" id="dateTime" value="${current}" />
</div>
</div>

<g:javascript>
$().ready(function() {
	$('#dateTime').datetimepicker({
		controlType: 'slider',
		timeFormat: 'HH:mm',
		dateFormat: "dd/mm/yy"
	});
	
});

	//function showModal(){
	//	$('#invitecontainer').show();
	//}
</g:javascript>



</body>
</html>