function updateExportedNumbers(total){
//	
		
var arr = total.split(",")
	$("#total_num").html(arr[0]);
    $("#view_num").html(parseInt(arr[1])+1);
    if(parseInt(arr[1])+1 == parseInt(arr[0])){
    	$("#loading").hide();
    	$(".explore").show();
    }
}


function updateExportedviewName(v_name){
//	
	$("#exporting_view").html(v_name);
	
}

function updateprogressBar(percent){
//	
	var width = $("#progress_bar").width();
	
	$("#progress_bar").css('width',percent+"%");
	
	$("#progress_bar").html(parseInt(percent)+"%");
	
}


function copyToClipboard(id) {
	  var copyText = document.getElementById(id);
	  copyText.select();
	  copyText.setSelectionRange(0, 99999);
	  document.execCommand("copy");
	  
	  var tooltip = document.getElementById("myTooltip");
	  tooltip.innerHTML = "Copied: " + copyText.value;
	}

	function outFunc() {
	  var tooltip = document.getElementById("myTooltip");
	  tooltip.innerHTML = "Copy to clipboard";
	}