
<input type="text" name="${instance.id}" id="${instance.id}"  onclick="this.value='';clearAll('${instance.setId}');"
onChange="<g:if test="${instance.autoCompleteToSelect }">actionThis(this.value, '${instance.setId}');</g:if>
<g:else>updateList(this.value, '${instance.id}', '${instance.sdataList}', '${instance.setId}')</g:else>"
  list="${instance.dataList}" name = "${instance.name}" placeholder="${instance.placeHolder ?: 'AutoComplete' }"/>
 
<g:hiddenField name="${instance.hiddenField?:'HIDDEN_'}${instance.id}" value=""/>
<g:hiddenField name="${instance.jsonField?:'JSON_'}${instance.id}" value=""/>
  
<datalist id="${instance.dataList}"></datalist>


<script>
	function getHiddenValue${instance.id}() {
		return  $('${instance.hiddenField?:'HIDDEN_'}${instance.id}').val();
	}
	function clearAll(setId) {
		$("#"+setId).val('');
	}
	$('#${instance.id}').change(function(){
    	var c =  $('#${instance.id}').val();
    	$('#${instance.id}').val(getTextValue${instance.id}(c));
    	$('#${instance.hiddenField?:'HIDDEN_'}${instance.id}').val(c);
	});
	function getTextValue${instance.id}(c){
  		var listing = $('#${instance.dataList}');
  		var endVal = $(listing).find('option[value="'+ c + '"]');
  		var dataValue = $('#${instance.dataList} [value="' + c + '"]').data('value');
  		if (dataValue) {
	  		$('#${instance.jsonField?:'JSON_'}${instance.id}').val(JSON.stringify(dataValue));
  		}
  		return endVal.text();  
	}	
</script>