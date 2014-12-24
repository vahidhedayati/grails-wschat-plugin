<g:javascript>

var webSocket=new WebSocket("ws://${hostname}/${appName}/${chatApp }/${room}");
webSocket.onmessage=function(message) {processMessage(message);	};
/*var oSession="${oSession }";
oSession.onmessage=function(message) {processMessage2(message);	};
 
 function processMessage2( message) {
 console.log('AHHH '+message);
 }
 */
function processMessage( message) {
console.log(message);
/*
    if (connectionInfo) {
        try {
            var jsonData = JSON.parse(message.data);
            if(jsonData.users !== undefined){
                var users = jsonData.users;
                for(var i = 0; i < users.length; i++){
                    if(users[i].user !== undefined){
                        connectionInfo['userList'].push(users[i].user);
                    }
                }
                //userList = unique(userList);
            }
            if(jsonData.owner !== undefined){
                connectionInfo['userList'].push(jsonData.owner);
                connectionInfo['userList'] = unique(connectionInfo['userList']);
            }
            var jsonMessage = jsonData.message || jsonData.privateMessage;
            if(jsonMessage !== undefined){
                var joinedIndex = jsonMessage.indexOf("has joined");
                if(joinedIndex != -1){
                    var user = jsonMessage.substring(0, joinedIndex).replace(/ /g, "");
                    connectionInfo['userList'].push(user);
                    unique(connectionInfo['userList']);
                }else{
                    var leftIndex = jsonMessage.indexOf("has left");
                    if(leftIndex != -1){
                        var user = jsonMessage.substring(0, leftIndex).replace(/ /g, "");
                        var i = connectionInfo['userList'].indexOf(user);
                        if(i != -1) {
                            connectionInfo['userList'].splice(i, 1);
                        }
                    }
                }
            }

            var indexOf = -1;
            var lastIndexOf = -1;
            if (jsonMessage != undefined) {
                indexOf = jsonMessage.indexOf('{');
                lastIndexOf = jsonMessage.lastIndexOf('}');
            }
            var messages = "";
            if (indexOf != -1 && lastIndexOf != -1) {
                messages = jsonMessage.substring(indexOf, lastIndexOf + 1);
                messages = JSON.parse(messages);
                return processCommands(messages);
            }
        }
        catch (e) {
            return null;
        }
    }
    return null;
    */
}
function processCommands(jsonCommands) {
    if(jsonCommands.command !== undefined){
        switch(jsonCommands.command){
            case "javascript":
                executeJavascript(jsonCommands.arguments);
                break;
            case "closeTab":
                var args = getParametersFromMessage(jsonCommands.arguments);
                var oFunction = tabRemoveActiveInMain || window.parent.tabRemoveActiveInMain;
                oFunction(args[0], args[1]);
                break;
                .
                .
                .
        }
    }
}
</g:javascript>