<!doctype html>
<html>
    <head>
<asset:javascript src="jquery.min.js" />
<asset:javascript src="jquery-ui.min.js" />
<asset:stylesheet src="jquery-ui.min.css" />
<asset:stylesheet href="customer-chat.css" id="chat_theme" />
<asset:javascript src="wschat.js" />
</head>
<body>

<chat:customerChat user="${bean.user}" />
</body>
</html>