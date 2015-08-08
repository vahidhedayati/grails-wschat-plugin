	<div class="modal fade" id="liveChatLogcontainer1" role="dialog">
	<div class="modal-dialog">
	<div class="modal-content">
	<div class="modal-body">

	<div id="logContainer">


	</div>
	</div>
	</div>
	</div>
</div>

<g:javascript>
	function closeChatModal() {
		$('#liveChatLogcontainer1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>