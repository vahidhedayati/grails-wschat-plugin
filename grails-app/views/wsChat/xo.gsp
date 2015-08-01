<!DOCTYPE html>
<html>
<head>
<g:if test="${enduser?.verifyAppVersion().equals('assets')}">
	<asset:stylesheet href="twitter-bootstrap.min.css" />
	<asset:stylesheet href="ticTacToe.css" />
	<asset:javascript src="twitter-bootstrap.min.js" />
</g:if>
<g:else>
<link rel="stylesheet"	href="${resource(dir: 'css', file: 'twitter-bootstrap.min.css')}" type="text/css">
<link rel="stylesheet" href="${resource(dir: 'css', file: 'ticTacToe.css')}" type="text/css">
<script type="text/javascript"	src="${resource(dir: 'js', file: 'twitter-bootstrap.min.js')}"></script>
</g:else>
   <title>${chatTitle}</title>
</head>
<body>
	<div class="modal-dialog">
	<div class="modal-content">
<span class="player-label">You:</span> ${username}<br />
   <span class="player-label">Opponent:</span>
   <span id="opponent"><i>Waiting</i></span>
   <div id="status">&nbsp;</div>
   <div id="gameContainer">
       <div class="row">
           <div id="r0c0" class="game-cell" onclick="move(0, 0);">&nbsp;</div>
           <div id="r0c1" class="game-cell" onclick="move(0, 1);">&nbsp;</div>
           <div id="r0c2" class="game-cell" onclick="move(0, 2);">&nbsp;</div>
       </div>
       <div class="row">
           <div id="r1c0" class="game-cell" onclick="move(1, 0);">&nbsp;</div>
           <div id="r1c1" class="game-cell" onclick="move(1, 1);">&nbsp;</div>
           <div id="r1c2" class="game-cell" onclick="move(1, 2);">&nbsp;</div>
       </div>
       <div class="row">
           <div id="r2c0" class="game-cell" onclick="move(2, 0);">&nbsp;</div>
           <div id="r2c1" class="game-cell" onclick="move(2, 1);">&nbsp;</div>
           <div id="r2c2" class="game-cell" onclick="move(2, 2);">&nbsp;</div>
       </div>
   </div>

   <div id="modalWaiting" class="modal hide fade" role="dialog">
   
       <div class="modal-header"><h3>Please Wait...</h3></div>
       <div class="modal-body" id="modalWaitingBody">&nbsp;</div>
   </div>
   
   <div id="modalError" class="modal hide">
       <div class="modal-header">
           <button type="button" class="close" data-dismiss="modal">&times;
           </button>
           <h3>Error</h3>
       </div>
       <div class="modal-body" id="modalErrorBody">A blah error occurred.
       </div>
       <div class="modal-footer">
           <button class="btn btn-primary" data-dismiss="modal">OK</button>
       </div>
   </div>
   
   <div id="modalGameOver" class="modal hide fade">
       <div class="modal-header">
           <button type="button" class="close" data-dismiss="modal">&times;
           </button>
           <h3>Game Over</h3>
       </div>
       <div class="modal-body" id="modalGameOverBody">&nbsp;</div>
       <div class="modal-footer">
           <button class="btn btn-primary" data-dismiss="modal">OK</button>
       </div>
   </div>
     </div>
   </div>
   <script type="text/javascript" language="javascript">
       var move;
       $(document).ready(function() {
          var modalError = $("#modalError");
          var modalErrorBody = $("#modalErrorBody");
          var modalWaiting = $("#modalWaiting");
          var modalWaitingBody = $("#modalWaitingBody");
          var modalGameOver = $("#modalGameOver");
          var modalGameOverBody = $("#modalGameOverBody");
          var opponent = $("#opponent");
          var status = $("#status");
          var opponentUsername;
          var username = "${bean.chatuser}";
          var myTurn = false;

          $('.game-cell').addClass('span1');

          if(!("WebSocket" in window))
          {
              modalErrorBody.text('WebSockets are not supported in this ' +
                      'browser. Try Internet Explorer 10 or the latest ' +
                      'versions of Mozilla Firefox or Google Chrome.');
              modalError.modal('show');
              return;
          }

          modalWaitingBody.text('Connecting to the server.');
          modalWaiting.modal({ keyboard: false, show: true });
          var server;
          try {
        	  var uri="${uri}";
        	  console.log(uri);
              server = new WebSocket(uri);
          } catch(error) {
              modalWaiting.modal('hide');
              modalErrorBody.text(error);
              modalError.modal('show');
              return;
          }

          server.onopen = function(event) {
        	  modalWaitingBody.text('Waiting on your opponent to join the game.');
              modalWaiting.modal({ keyboard: false, show: true });
          };

          window.onbeforeunload = function() {
              server.close();
          };

          server.onclose = function(event) {
              if(!event.wasClean || event.code != 1000) {
                  toggleTurn(false, 'Game over due to error!');
                  modalWaiting.modal('hide');
                  modalErrorBody.text('Code ' + event.code + ': ' +
                          event.reason);
                  modalError.modal('show');
              }
          };

          server.onerror = function(event) {
              modalWaiting.modal('hide');
              modalErrorBody.text(event.data);
              modalError.modal('show');
          };

          server.onmessage = function(event) {
              var message = JSON.parse(event.data);
              if(message.action == 'gameStarted') {
                  if(message.game.player1 == username)
                      opponentUsername = message.game.player2;
                  else
                      opponentUsername = message.game.player1;
                  opponent.text(opponentUsername);
                  toggleTurn(message.game.nextMoveBy == username);
                  modalWaiting.modal('hide');
              } else if(message.action == 'opponentMadeMove') {
                  $('#r' + message.move.row + 'c' + message.move.column)
                          .unbind('click')
                          .removeClass('game-cell-selectable')
                          .addClass('game-cell-opponent game-cell-taken');
                  toggleTurn(true);
              } else if(message.action == 'gameOver') {
                  toggleTurn(false, 'Game Over!');
                  if(message.winner) {
                      modalGameOverBody.text('Congratulations, you won!');
                  } else {
                      modalGameOverBody.text('User "' + opponentUsername +
                              '" won the game.');
                  }
                  modalGameOver.modal('show');
              } else if(message.action == 'gameIsDraw') {
                  toggleTurn(false, 'The game is a draw. ' +
                          'There is no winner.');
                  modalGameOverBody.text('The game ended in a draw. ' +
                          'Nobody wins!');
                  modalGameOver.modal('show');
              } else if(message.action == 'gameForfeited') {
                  toggleTurn(false, 'Your opponent forfeited!');
                  modalGameOverBody.text('User "' + opponentUsername +
                          '" forfeited the game. You win!');
                  modalGameOver.modal('show');
              }
          };

          var toggleTurn = function(isMyTurn, message) {
              myTurn = isMyTurn;
              if(myTurn) {
                  status.text(message || 'It\'s your move!');
                  $('.game-cell:not(.game-cell-taken)')
                          .addClass('game-cell-selectable');
              } else {
                  status.text(message ||'Waiting on your opponent to move.');
                  $('.game-cell-selectable')
                          .removeClass('game-cell-selectable');
              }
          };

          move = function(row, column) {
              if(!myTurn) {
                  modalErrorBody.text('It is not your turn yet!');
                  modalError.modal('show');
                  return;
              }
              if(server != null) {
                  server.send(JSON.stringify({ row: row, column: column }));
                  $('#r' + row + 'c' + column).unbind('click')
                          .removeClass('game-cell-selectable')
                          .addClass('game-cell-player game-cell-taken');
                  toggleTurn(false);
              } else {
                  modalErrorBody.text('Not connected to came server.');
                  modalError.modal('show');
              }
          };
       });
   </script>
</body>
</html>