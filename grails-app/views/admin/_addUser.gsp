		<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3>

</div>
</div>
	
	<div class="container">
		<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'addUserEmail']" 
	update="inviteConfirmation"  
	onComplete="closeInviteModal();verifyValue('${username}')"
	>
	<div class='row'>
	<div class='col-sm-5'>
		<div class='form-group'>
			<label for="username">
				<g:message code="wschat.new.username.label" default="Username" />
			</label>
			<g:textField name="username" value="${username }" />
		</div>
	</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-5'>
			<div class='form-group'>
				<label for="emailAddress">
				<g:message code="wschat.new.email.address.label" default="Email Address" />
				</label>
				<g:textField name="email" />
			</div>
		</div>
	</div>
	
	<div class='row'>
		<div class='col-sm-5'>
		<div class='form-group'>
			<label for="submit">
			</label>
			<g:submitButton name="send" class="btn btn-primary" value="${message(code: 'wschat.add.chat.user.label', default: 'Add Chat User')}" />
			</div>
		</div>
		</div>
		

	</g:formRemote>	
	</div>