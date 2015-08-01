<g:if test="${bean?.jquery}">
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.min.js')}"></script>
</g:if>
<g:if test="${bean?.jqueryui}">
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'jquery-ui.min.css')}" type="text/css" media="screen" />
</g:if>	
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'jquery.ui.chatbox.css')}" type="text/css">
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.ui.chatbox.js')}"></script>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery.ui.videobox.js')}"></script>
<g:if test="${bean?.bootstrap}">
<script type="text/javascript"	src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
</g:if>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui-timepicker-addon.js')}"></script>
<script type="text/javascript"	src="${resource(dir: 'js', file: 'jquery-ui-timepicker-addon-i18n.min.js')}"></script>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'ticTacToe.css')}" type="text/css">
