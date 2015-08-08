<div id="chatReturn"></div>
<button onclick="runChat()" title="Chat"> Chat</button>
<script type="text/javascript">
	function runChat() {
		$.get('${createLink(controller:"wsChat", action: "loadChat")}?user=${bean?.user}&controller=${bean?.controller}&action=${bean?.action}&roomName=${bean?.roomName}', function(e){
			if (e) {
				$('#chatReturn').hide().html(e).fadeIn('slow'); 
			}
			});
 	 }
</script>
