function validateRegister() {
	var valid = false;
	valid = validateUsername();
	if(!valid) {
		return valid;
	}
	valid = validatePassword();
	if(!valid) {
		return valid;
	}
	return valid;
}

function validateUsername() {
	var username = document.getElementById("username");
	var usernameLength = username.value.length;
	if(usernameLength > 64) {
		username.value = username.value.slice(0, 65);
		return false;
	}
	return true;
}

function validatePassword() {
	var password = document.getElementById("password");
	var passwordLength = password.value.length;
	if(passwordLength > 64) {
		password.value = password.value.slice(0, 65);
		return false;
	}
	return true;
}

function whenReady() {
	checkAvailibility(null);
}

function checkAvailibility(lastUsername) {
	var currentUsername = document.getElementById("username").value;
	if(lastUsername === currentUsername) {
		setTimeout(checkAvailibility, 2500, currentUsername);
		return;
	}
	$.ajax({
		type: "GET",
		url: "register/available/" + currentUsername,
		error: function(xhr, status, error) {	
			setTimeout(checkAvailibility, 2500, currentUsername);
		},
		success: function(data) {
			var currentUsernameElement = document.getElementById("username");
			if(data) {
				currentUsernameElement.className = document.getElementById("username").className.replace(/(?:^|\s)unavailable(?!\S)/g , 'available');
				if(!currentUsernameElement.className.match(/(?:^|\s)available(?!\S)/)) {
					currentUsernameElement.className += "available";
				}
			} else {
				currentUsernameElement.className = document.getElementById("username").className.replace(/(?:^|\s)available(?!\S)/g , 'unavailable');
				if(!currentUsernameElement.className.match(/(?:^|\s)unavailable(?!\S)/)) {
					currentUsernameElement.className += "unavailable";
				}
			}
			setTimeout(checkAvailibility, 1000, currentUsername);
			
		}
	});
}
