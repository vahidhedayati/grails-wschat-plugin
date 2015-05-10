<meta name='layout' content="achat"/>
	<div id="photobox1" class="modal fade">
	
	<div id="photoContainer">
			
	</div>
	</div>

<g:javascript>
    
    
    $(".modal").css('position','absolute');
	$(".modal").css('left','0');
	$(".modal").css('margin-left','auto');
	$(".modal").css('margin-right','auto');    
$(".modal-body").css('margin-left','auto');
	$(".modal-body").css('margin-right','auto');    

	$(document).keyup(function(e) {
  		if (e.keyCode == 27) { closeModal(); }   
	});

	function closePhotos() {
		$('#photobox1').modal('hide');
		$('body').removeClass('modal-open');
		$('.modal-backdrop').remove();
	}
</g:javascript>
	
	
