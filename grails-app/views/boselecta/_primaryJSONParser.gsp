<g:javascript>
	var message = '${instance.message}';
	var jsonData = JSON.parse(message);
	var jsonResult = jsonData.primarylist;
	var cid = jsonData.id;
	var format = jsonData.formatting;
	var updateValue = jsonData.updateValue;
	if (jsonResult != null ){
		var rselect = document.getElementById(cid);
		jsonResult.forEach(function(entry) {
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
					opt.value = JSON.stringify(entry.resarray);
				}else{
					opt.value = id;
				}
				opt.text=name
				if (id == updateValue) {
					opt.setAttribute('selected', true);
				}
				try {
					rselect.add(opt, null);
				} catch(ex) {
					//rselect.add(opt,null);
				}
			}
		});
}
</g:javascript>



