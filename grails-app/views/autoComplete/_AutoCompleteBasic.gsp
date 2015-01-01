
<div id="userInputError">
<div id="userInput">
<input type='text' ${clazz} id='${attrs.id}' value = '${attrs.value}' ${required} ${styles} ${name }   onClick="this.value='';resetMsg();"  onchange="verifyValue(this.value);" />
</div>
<div id="response_image"></div>
</div>
<div id="return_result"></div>

<g:javascript>
	$('#${attrs.id}').autocomplete({ 
		source: '<g:createLink action='${attrs.action}' controller="${attrs.controller}"
	 	params="[domain: ''+attrs.domain+'', searchField: ''+attrs.searchField+'', max: ''+attrs.max+'', order: ''+attrs.order+'', collectField: ''+attrs.collectField+'']"/>',
		dataType: 'json',
		select: function(event, ui){
        if (ui.item && ui.item.value){
        	//$('${attrs.id}_auto').val(ui.item.value);
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
			$.getJSON('${createLink(controller:"${attrs.controller}", action: "findUser")}?uid='+data,function(e){
			var found=e.status;
			var semail=e.email;
		
			if (found=="found") {
				if (semail != undefined) {
					$('#response_image').html('<div id="accept"></div>');
					$('#return_result').html('Email address found: '+semail);
					addSelection(semail, data);
					
					$('#${attrs.id }').val("");
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