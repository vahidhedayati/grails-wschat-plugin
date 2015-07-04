
<div id="userInputError">
<div id="userInput">
	<input type='text' class="${bean.clazz}" id='${bean.id}' value='${bean.value}' required="${bean.required?required:''}
	 style='${bean.styles}' name='${bean.name}' onClick="this.value='';resetMsg();"  onchange="verifyValue(this.value);" />
</div>
<div id="response_image"></div>
</div>
<div id="return_result"></div>

<g:javascript>
	$('#${bean.id}').autocomplete({ 
		source: '<g:createLink action='${bean.action}' controller="${bean.controller}"
	 	params="[ searchField: ''+bean.searchField+'', max: ''+bean.max+'', order: ''+bean.order+'', collectField: ''+bean.collectField+'']"/>',
		dataType: 'json',
		select: function(event, ui){
        if (ui.item && ui.item.value){
        	//$('${bean.id}_auto').val(ui.item.value);
            titleinput = ui.item.value;
            ui.item.value= $.trim(titleinput);
            verifyValue(ui.item.value);
        }         
	  }
	});

	function resetMsg() { 
		$('#return_result').html("");
		$('#response_image').html("");
	}

	function addSelection(email, data) {
		$('#selectedValues').append('<div class="btn"></div><input type="checkbox" checked name="invites" value="'+data+'">'+data+'</div>');
	}
	
	function verifyValue(data) {
		if (data!='') {
			$.getJSON('${createLink(controller:"${bean.controller}", action: "findUser")}?uid='+data,function(e){
			var found=e.status;
			var semail=e.email;
		
			if (found=="found") {
				if (semail != undefined) {
					$('#response_image').html('<div id="accept"></div>');
					$('#return_result').html('Email address found: '+semail);
					addSelection(semail, data);
					
					$('#${bean.id }').val(" ");
					resetMsg();
				}else{
					$('#response_image').html('<div id="question"></div>');
					var return_this='<div class="errors" role="alert">User '+data+' has no email, <a data-toggle="modal"  href="#invitecontainer1" onclick="javascript:createEmail('+wrapIt(user)+','+wrapIt(data)+');">setupEmail?</a></div>';
					$('#return_result').html(return_this);
				}
			} else{
				$('#response_image').html('<div id="reject"></div>');
				var return_this='<div class="errors" role="alert">User '+data+' not found, <a data-toggle="modal" href="#invitecontainer1"  onclick="javascript:addUser('+wrapIt(user)+','+wrapIt(data)+');">Invite?</a></div>';
				$('#return_result').html(return_this);
			}
			});
		}	
	}
	init();
function init(){
    var autoSuggestion = document.getElementsByClassName('ui-autocomplete');
    if(autoSuggestion.length > 0){
        autoSuggestion[0].style.zIndex = 2006;
    }
}
///function closeModal() {
	//$('#roomcontainer1').modal('hide');
	//$('body').removeClass('modal-open');
	//$('.modal-backdrop').remove();
	//	webSocket.send("/listRooms");
//}	

</g:javascript>