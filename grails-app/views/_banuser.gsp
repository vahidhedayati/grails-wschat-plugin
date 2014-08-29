	<div class="modal fade" id="banuser1" role="dialog">
	<div class="modal-dialog"><div class="modal-content">

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		<h3 id="prompt">Ban <span id="banuserField"></span></h3>
	</div>
	
	<div class="form-group">
	<g:formRemote 
	name="urlParams"  
	class="form-horizontal" 
	url="[controller:'wsChat', action:'confirmBan']" 
	update="bannedconfirmation"  
	onComplete="closeModal()"
	>
  	
	<div class="modal-body">
	
			
			<div id="contact-area">
				
				<div class="required">
				<label for="duration">
				<g:message code="wschat.username.label" default="Username" />
				</label>
				<g:textField name="username" readonly="readonly" id="banUsername"  value=""/>
				</div>
				 
				<div class="required">
				<label for="duration">
				<g:message code="wschat.duration.label" default="Duration" />
				</label>
				<g:select id="duration" name="duration" from="${1..60}" required="" value="${duration}" class="many-to-one"/>
				<g:select id="period" name="period" from="${['minutes':'Minutes','hours':'Hours','days':'Days','years':'Years']}"  optionKey="key" optionValue="value"   value="${period}" class="many-to-one"/>
				</div>
				
			</div>
		</div>	
		<div class="modal-footer">
			<g:submitToRemote class="btn btn-danger" url="[controller:'wsChat', action:'confirmBan']" update="bannedconfirmation" onComplete="closeModal()" value="Ban user" />
		</div>	
		</g:formRemote>
					
	</div>
	</div>
	</div>
	</div>
<g:javascript>
	function closeModal() {
		$('#banuser1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>
	
	
