
<div class='modal-header'>
<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>×</button>
<div id='myModalLabel'><h3>${message(code: 'wschat.'+actionName+'.label', default: ''+actionName+'')}</h3></div>
</div>

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		Remove Room
	</div>
	
	<g:if test="${roomList}" >

	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'delRoom']" 
	update="profileconfirmation"  
	onComplete="closeDelModal()"
	>

	<div class="modal-body">
		<div class='row'>
			
		<div class='col-sm-2'>
		<div class='form-group'>
		<label for="age">
		<g:message code="wschat.firstname.label" default="Your Age" />
		</label>
		<g:select id="delroomName" name="room" from="${roomList}" required="required"  class="one-to-one"/>
		</div>
		</div>
	
		</div>				
		
		</div>	
		<div class="modal-footer">
			<g:submitToRemote class="btn btn-primary" url="[controller:'wsChat',action:'delRoom']"	update="profileconfirmation"  onComplete="closeDelModal()" value="Remove room" />
		</div>	
		</g:formRemote>
					

	</g:if>
	<g:else>
		No rooms stored on DB	
	</g:else>
	
	
	
