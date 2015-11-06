
<g:javascript>
	var loggedInUsers=[];
	var user="${instance.user }";
	var webSocket${instance.job}=new WebSocket('${instance.uri}/${instance.job}');
	webSocket${instance.job}.onopen=function(message) {processOpen(message);};
	webSocket${instance.job}.onclose=function(message) {processClose(message);};
	webSocket${instance.job}.onerror=function(message) {processError(message);};
	webSocket${instance.job}.onmessage=function(message) {processMessage(message);	};


	var userList=[];

	function processMessage( message) {
		//console.log(JSON.stringify(message.data));
		var jsonData = JSON.parse(message.data);
		if (jsonData.message!=null) {
			var itJson=isJson(jsonData.message);
			if (itJson==true) {
				var jsonData1 = JSON.parse(jsonData.message);
				var appendValue, appendName, updated, updateValue, nextValue,formatting,domainDepth='';
				//auto comp
				var cId,updateList='';
				if (jsonData1.updateThisDiv!=null) {
					setId=jsonData1.updateThisDiv;
				}
				if (jsonData1.appendValue!=null) {
					appendValue=jsonData1.appendValue;
				}
				if (jsonData1.domainDepth!=null) {
					domainDepth=jsonData1.domainDepth;
				}
				
				if (jsonData1.appendName!=null) {
					appendName=jsonData1.appendName;
				}
				if (jsonData1.formatting!=null) {
					formatting=jsonData1.formatting;	
				}	
				if (jsonData1.nextValue!=null) {
					nextValue=jsonData1.nextValue;
				}
				
				if (jsonData1.updated!=null) {
					updated=jsonData1.updated;
				}
				if (jsonData1.updateValue!=null) {
					updateValue=jsonData1.updateValue;
				}
				
				// Select function
				if (jsonData1.result!=null) {
				    var jsonResult = jsonData1.result;
				    updateView(jsonResult, setId, appendName, appendValue, updated, updateValue, nextValue,formatting);
					var to = parseInt(domainDepth)
					if ((to==undefined)||(to==null)) {
						to=3;
					}
					for (a=3; a < to; a++) {
						var c=a;
						try {
							var cid = eval('jsonData1.setId'+c);
					 		if (cid!=null) {
					 			var jsonResult = eval('jsonData1.result'+c);
					 			updateOtherView(jsonResult, cid, updateValue,formatting,appendName,appendValue);
							}
						} catch(ex) {
						}	
					}
				}
				
				//Auto Complete function
				if (jsonData1.cId!=null) {
					cId = jsonData1.cId;
				}
				if (jsonData1.updateList!=null) {
					updateList = jsonData1.updateList;
				}
				if (jsonData1.updateAutoValue!=null) {
					updateAutoValue = jsonData1.updateAutoValue;
				}
				if (jsonData1.autoResult!=null) {
					var autoResult = jsonData1.autoResult;
					 updateAutoView(autoResult, cId, setId, appendName, appendValue, updated, updateValue, nextValue,formatting, updateList);
				}
			}	
		}

	// Log out user if system tells it to	
	if (jsonData.system != null) {
		if (jsonData.system == "disconnect") { 
			webSocket${instance.job}.send("DISCO:-"+user);
			webSocket${instance.job}.close();
		}
	}
}


function updateOtherView(jsonResult,cid, updateValue,format,appendName,appendValue) {
	var id, name,resarray='';
	//reset select box
	var rselect = document.getElementById(cid);
	if (rselect) {
		var l = rselect.length
		while (l > 0) {
    		l--
    		rselect.remove(l)
  		}
  	}
  	if (appendName!="") { 	
		var opt = document.createElement('option');
		opt.value = appendValue;
		opt.text = appendName;
		try {
			rselect.add(opt, null);
		} catch(ex) {
			rselect.add(opt);
		}
	}
	jsonResult.forEach(function(entry) {
		var rselect = document.getElementById(cid);
		if (rselect) {
			var opt = document.createElement('option');
			if (entry.id != null) {
				id=entry.id;
			}
			if (entry.name != null) {
				name=entry.name;
			}
			if (entry.resarray != null) {
				resarray=entry.resarray;
			}
			
			
			if (format == "JSON") {
				opt.value = JSON.stringify(resarray);
			}else{
				opt.value = name;
			}
			opt.text=id
			if (id == updateValue) {
				//opt.checked=true;
				opt.setAttribute('selected', true);
			}
			try {
				rselect.add(opt, null)
			} catch(ex) {
				rselect.add(opt)
			}
			
		}
	});
}

function updateAutoView(jsonResult, cId, setId, appendName, appendValue, updated, updateValue,
 nextValue,format, updateList) {

	var dataList = document.getElementById(updateList);
	var input = document.getElementById(setId);
	while (dataList.firstChild){
       dataList.removeChild(dataList.firstChild);
    }
	jsonResult.forEach(function(item) {
        var option = document.createElement('option');
        //if (format == "JSON") {
        	 option.setAttribute('data-value', JSON.stringify(item.resarray));
        //}
        option.value = item.id
        option.textContent = item.name;

        dataList.appendChild( option );
       
    });
} 


function updateView(jsonResult, setId, appendName, appendValue, updated, updateValue, nextValue,format) {
	var id, name,resarray='';
	var rselect = document.getElementById(setId);
	var l = rselect.length;
	while (l > 0) {
		l--
		rselect.remove(l);
	}

	if ((appendName!="")&&(updated=="yes")) { 	
		var opt = document.createElement('option');
		opt.value = appendValue;
		opt.text = appendName;
		try {
			rselect.add(opt, null);
		} catch(ex) {
			rselect.add(opt);
		}
	}
				
	jsonResult.forEach(function(entry1) {
		var opt = document.createElement('option');
		if (entry1.id!=null) {
			id=entry1.id;
		}
		if (entry1.resarray!=null) {
			resarray = entry1.resarray;
		}
		if (entry1.name!=null) {
			name = entry1.name;
		}
		
		if (format == "JSON") {
				opt.value = JSON.stringify(resarray);
			}else{
				opt.value = name;
			}
		opt.text=id
			if (name==nextValue) {
				opt.setAttribute('selected', true);
			}
		try {
			rselect.add(opt, null)
		} catch(ex) {
			rselect.add(opt)
		}
	});
} 

function isJson(message) {
	var input='{';
	return new RegExp('^' + input).test(message);
}
function isPm(message) {
	var input='/pm';
	return new RegExp('^' + input).test(message);
}

function isReceivedMsg(message) {
	var input='_received';
	return new RegExp( input +'$').test(message);
}

function processError(message) {
	console.log(message);
}

function verifyIsOn(uid) {
	var ison="false";
	var idx = loggedInUsers.indexOf(uid);
	if (idx != -1) {
		ison="true";
	}
	return ison;
}	

function addUser(uid) {
	var idx = loggedInUsers.indexOf(uid);
	if(idx == -1) {
		loggedInUsers.push(uid);
	}	
}

function processClose(message) {
	webSocket${instance.job}.send("DISCO:-"+user);
	webSocket${instance.job}.close();
}


// Open connection only if we have frontuser variable    
function processOpen(message) {
	<g:if test="${instance.frontuser}">
		webSocket${instance.job}.send("CONN:-${instance.frontuser}");
	</g:if>
	<g:else>
		webSocket${instance.job}.send("DISCO:-");
		webSocket${instance.job}.close();
	</g:else>
}



window.onbeforeunload = function() {
	webSocket${instance.job}.send("DISCO:-");
	webSocket${instance.job}.onclose = function() { }
	webSocket${instance.job}.close();
}
</g:javascript>