
<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3></div>
</div>

	<div class="container">
		<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'addBooking']" 
	update="adminConfirmation"  
	onComplete="closeAdminModal();"
	>
	<div class='row'>
	<div class='col-sm-6'>
		<div class='form-group'>
		
<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'conferenceName', 'error')} required">
<label for="manager">
<g:message code="wschat.conference.name.label" default="Conference Name" />
</label>
	<input type="text" name="conferenceName" id="conferenceName" value="" />
</div>
</div>
</div>
</div>
<div class='row'>
	<div class='col-sm-8'>
		<div class='form-group'>
		


<label for="masterUser">
<g:message code="wschat.room.owner.label" default="Invite members" />
</label>
	
<% def myuser = bo.randomizeUser('user': 'random1') %>
<bo:connect user="${myuser}" job="job3"/>
<bo:selecta 
	 autoComplete="true" autoCompletePrimary="true" 
    job="job3" user="${myuser}"id="masterUser" name="masterUser"
	domain='grails.plugin.wschat.ChatUser'
	searchField='username'
	collectField='username'
	setId="h1"
	value=''
/>


<div id="selectedValues">
</div>
</div>
</div>
</div>

<div class='row'>
	<div class='col-sm-8'>
		<div class='form-group'>
<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'dateTime', 'error')} required">
<label for="manager">
<g:message code="dateTime.label" default="Start date/Time" />
</label>
	<input type="text" name="dateTime" id="dateTime" value="${current}" />
</div>
</div>
</div>
</div>
<div class='row'>
	<div class='col-sm-8'>
		<div class='form-group'>
<div class="fieldcontain ${hasErrors(bean: bookingInstance, field: 'endDateTime', 'error')} required">
<label for="manager">
<g:message code="endDateTime.label" default="End date/Time" />
</label>
	<input type="text" name="endDateTime" id="endDateTime" value="${current}" />
</div>
</div>
</div>
</div>



	<div class='row'>
	<div class='col-sm-8'>
		<div class='form-group'>
		<label for="firstname">
		</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'wschat.add.booking.label', default: 'Add Booking')}" />
			
		</div>
		</div>
		</div>


	</g:formRemote>			
<g:javascript>
$().ready(function() {
	$('#dateTime').datetimepicker({
		controlType: 'slider',
		timeFormat: 'HH:mm',
		dateFormat: "dd/mm/yy"
	});
	$('#endDateTime').datetimepicker({
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
