	<div class="modal fade" id="roomcontainer1" role="dialog">
	<div class="modal-dialog">
	<div class="modal-content">
	<div id="roomsContainer">

			
				

	
	
		
	
	</div>
	</div>
	</div>

	</div>

<g:javascript>

	function closeModal() {
		$('#roomcontainer1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
		webSocket.send("/listRooms");
	}
	function closeDelModal() {
		var nroom = document.getElementById('delroomName').value;
		$('#roomcontainer1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
		webSocket.send("/delRoom "+nroom);
	
	}
</g:javascript>
	
	
