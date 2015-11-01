  <div id="adminConfirmation"></div>

	<div class="modal fade" id="admincontainer1" role="dialog">
	<div class="modal-dialog">
	<div class="modal-content">
	<div id="adminsContainer">

			
				

	
	
		
	
	</div>
	</div>
	</div>

	</div>

<g:javascript>

	var user="${bean.chatuser}";

	function closeAdminModal() {
		$('#admincontainer1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
		//webSocket.send("/listRooms");
	}
	
</g:javascript>
	
	
	<div id="invitecontainer" style="display:none;">
		<g:render template="/admin/inviteContainer"/>
	</div>
	