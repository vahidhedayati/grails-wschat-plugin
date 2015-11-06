

<g:javascript>
	function actionThis(value, divid) {
		webSocket${instance.job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value}));
	}

	function actionNonAppendThis(value, divid) {
		webSocket${instance.job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateValue : value, updated: 'no'}));
	}

	function updateList(value, id, dataList, divid) {
		webSocket${instance.job}.send("/pm "+user+","+JSON.stringify({updateDiv : divid, updateList : dataList, updateAutoValue : value,  cId: id}));
	}
	//function updatePlaceHolder(value, id, dataList) {
 		//var input = document.getElementById(id);
		//var dataList = document.getElementById(dataList);
		//var x = input.value;
        //var val = $(dataList).find('option[value="' + x + '"]');
        // all dataList elements now store JSON results in id attribute.
        //var endval = val.attr('id');
 	//}

</g:javascript>



