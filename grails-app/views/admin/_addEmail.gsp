	<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>

</div>
</div>
	
	
		<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'addUserEmail']" 
	update="inviteConfirmation"  
	onComplete="closeInviteModal();verifyValue('${username}')"
	>
		<g:hiddenField name="username" value="${username }" />
	<div class='row'>
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="username">
		<g:message code="wschat.new.email.address.label" default="Email Address" />
		</label>
		<g:textField name="email" />
		</div>
		</div>
			<div class='col-sm-4'>
		<div class='form-group'>
		<label for="firstname">
		</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'wschat.add.chat.user.email.label', default: 'Add Chat User Email')}" />
			
		
		</div>
		</div>
		
		
</div>

	</g:formRemote>	