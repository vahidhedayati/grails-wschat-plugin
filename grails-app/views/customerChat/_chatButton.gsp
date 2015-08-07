<div id="chatReturn"></div>
<button onclick="runChat()" title="Chat"> Chat</button>
<script type="text/javascript">
	function runChat() {
		$.get('${createLink(controller:"wsChat", action: "loadChat")}?user=${attrs?.user}&controller=${attrs?.controller}&action=${attrs?.action}', function(e){
			if (e) {
				$('#chatReturn').hide().html(e).fadeIn('slow'); 
			}
			});
 	 }
</script>
