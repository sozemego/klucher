function onDevLoad() {
	$("#post").submit(function (event) {
		ajaxSend();
		return false;
	});
}

function ajaxSend() {
	var username = $("#username").val();
	if(username == "" || username == "undefined" || username == null) {
		return;
	}
	var number = $("#number").val();
	if(number == "" || number == "undefined" || !isValidInteger(number)) {
		return;
	}
	var millis = $("#millis").val();
	if(millis == "" || millis == "undefined" || !isValidInteger(millis)) {
		millis = "";
	}
	var fastMode = $("#fastMode").is(":checked");
	var mode = $("#mode").val();
	if(mode == "" || mode == "undefined" || mode == null) {
		mode = null;
	}
	$.ajax({
		type: "POST",
		url: "dev/post",
		data: {
			"username" : username,
			"number" : number,
			"millis" : millis,
			"fastMode" : fastMode,
			"mode" : mode
		},
		error: function(xhr, status, error) {	
			
		},
		success: function(data, status, xhr) {
			var lol = 5;
		}
	});
}

function isValidInteger(number) {
	return /^\d+$/.test(number);
}