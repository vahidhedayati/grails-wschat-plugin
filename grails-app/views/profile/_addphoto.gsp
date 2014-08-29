
<div id="profileContainer">

	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal">x</button>
		Add photos to ${username }'s profile
	</div>
	


		<div class="modal-body">
		<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			
			<iframe frameborder="0" scrolling="auto" allowtransparency=true width="50%" height="100%" src="${photoFile}"></iframe>
			</div>

	
	
	
	
</div>