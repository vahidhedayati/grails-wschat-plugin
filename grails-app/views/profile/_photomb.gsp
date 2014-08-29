
	<div id="photobox1" class="modal fade">
	
	<div id="photoContainer">
			
	</div>
	</div>

<g:javascript>
    
    
    $(".modal").css('position','absolute');
    $(".modal").css('left','0');

	$(document).keyup(function(e) {
  		if (e.keyCode == 27) { closeModal(); }   
	});

	function closeModal() {
		$('#photobox1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>
	
	
