	<div class="modal fade" id="invitecontainer1" role="dialog">
	<div class="modal-dialog">
	<div class="modal-content">
	<div class="modal-body">
	<div id="inviteConfirmation"></div>
	<div id="inviteUserContainer">

		
	</div>
	</div>
	</div>
	</div>
</div>

<g:javascript>

	function closeInviteModal() {
		$('#invitecontainer1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
		webSocket.send("/listRooms");
	}

</g:javascript>