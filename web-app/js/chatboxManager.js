// Need this to make IE happy
// see http://soledadpenades.com/2007/05/17/arrayindexof-in-internet-explorer/
if(!Array.indexOf){
    Array.prototype.indexOf = function(obj){
	for(var i=0; i<this.length; i++){
	    if(this[i]==obj){
	        return i;
	    }
	}
	return -1;
    }
}


var chatboxManager = function() {

    // list of all opened boxes
    var boxList = new Array();
    // list of boxes shown on the page
    var showList = new Array();
    // list of first names, for in-page demo
    var nameList = new Array();

    var config = {
	width : 200, //px
	gap : 20,
	maxBoxes : 5,
	messageSent : function(dest, msg) {
	    // override this
	    $("#" + dest).chatbox("option", "boxManager").addMsg(dest, msg);
	}
    };

    var init = function(options) {
	$.extend(config, options)
    };


    var delBox = function(id) {
	// TODO
    };

    var getNextOffset = function() {
	return (config.width + config.gap) * showList.length;
    };

    var boxClosedCallback = function(id) {
	// close button in the titlebar is clicked
	var idx = showList.indexOf(id);
	if(idx != -1) {
	    showList.splice(idx, 1);
	    diff = config.width + config.gap;
	    for(var i = idx; i < showList.length; i++) {
		offset = $("#" + showList[i]).chatbox("option", "offset");
		$("#" + showList[i]).chatbox("option", "offset", offset - diff);
	    }
	}
	else {
	    alert("should not happen: " + id);
	}
    };

    // caller should guarantee the uniqueness of id
    var addBox = function(id, user, name) {
	var idx1 = showList.indexOf(id);
	var idx2 = boxList.indexOf(id);
	if(idx1 != -1) {
	    // found one in show box, do nothing
	}
	else if(idx2 != -1) {
	    // exists, but hidden
	    // show it and put it back to showList
	    $("#"+id).chatbox("option", "offset", getNextOffset());
	    var manager = $("#"+id).chatbox("option", "boxManager");
	    manager.toggleBox();
	    showList.push(id);
	}
	else{
	    var el = document.createElement('div');
	    el.setAttribute('id', id);
	    $(el).chatbox({id : id,
			   user : user,
			   title : user,
			   hidden : false,
			   width : config.width,
			   offset : getNextOffset(),
			   messageSent : messageSentCallback,
			   boxClosed : boxClosedCallback
			  });
	    boxList.push(id);
	    showList.push(id);
	    nameList.push(user);
	}
    };

    var messageSentCallback = function(id, user, msg) {
	var idx = boxList.indexOf(id);
	config.messageSent(nameList[idx], msg);
    };

    // not used in demo
    var dispatch = function(id, user, msg) {
	$("#" + id).chatbox("option", "boxManager").addMsg(user, msg);
    }

    return {
	init : init,
	addBox : addBox,
	delBox : delBox,
	dispatch : dispatch
    };
}();