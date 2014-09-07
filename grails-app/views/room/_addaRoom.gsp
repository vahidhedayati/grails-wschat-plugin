

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		Add a room
	</div>
	
	

	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'addRoom']" 
	update="profileconfirmation"  
	onComplete="closeModal()"
	>

	<div class="modal-body">
		<div class='row'>
		
		<div class='col-sm-4'>
		<div class='form-group'>
		<label for="firstname">
		<g:message code="wschat.new.room.name.label" default="New room" />
		</label>
		<g:textField name="room" />
		</div>
		</div>
		
		</div>	
				
				
			
				
		
		</div>	
		<div class="modal-footer">
			<g:submitToRemote class="btn btn-primary" url="[controller:'wsChat',action:'addRoom']"	update="profileconfirmation"  onComplete="closeModal()" value="Add Room" />
		</div>	
		</g:formRemote>
					

	
	
	
	
	
