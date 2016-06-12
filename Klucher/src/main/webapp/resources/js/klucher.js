function whenReady() {
	checkAvailibility(null);
	validateStart();
}

var usernameAvailable = true;

function checkAvailibility(lastUsername) {
	var currentUsername = $("#username").val();
	if(lastUsername === currentUsername) {
		setTimeout(checkAvailibility, 2500, currentUsername);
		return;
	}
	if(currentUsername == null || currentUsername === "") {
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
			var currentUsernameElement = $("#username");
			usernameAvailable = data;
			if(data) {
				currentUsernameElement.removeClass("unavailable");
				currentUsernameElement.addClass("available");				
			} else {
				currentUsernameElement.removeClass("available");
				currentUsernameElement.addClass("unavailable");			
			}
			addUsernameAvailableMessage();
			setTimeout(checkAvailibility, 1000, currentUsername);			
		}
	});
}

function validateStart() {
	$("#username").keyup(function() {
		validate();
	});
	$("#username").blur(function() {
		validate();
	});
	$("#password").keyup(function() {
		validate();
	});
	$("#password").blur(function() {
		validate();
	});
}

function validate() {
	clearErrorTable();
	addUsernameAvailableMessage();
	validateUsername();
	validatePassword();
}

function clearErrorTable() {
	$("#errorTable").empty();
}

function validateUsername() {
	var usernameElement = $("#username");
	usernameElement.removeClass("invalidImg");
	usernameElement.addClass("validImg");
	var username = usernameElement.val();
	var isTooShort = isUsernameTooShort(username);
	var isTooLong = isUsernameTooLong(username);
	if(isTooShort || isTooLong) {
		usernameElement.removeClass("validImg");
		usernameElement.addClass("invalidImg");
	}
	if(isTooShort) {
		$("#errorTable").append(createTableRowWithText("Username should be at least 4 characters long."));
	}
	if(isTooLong) {
		$("#errorTable").append(createTableRowWithText("Username should not be longer than 64 characters."));
	}
}

function validatePassword() {
	var passwordElement = $("#password");
	passwordElement.removeClass("invalidImg");
	passwordElement.addClass("validImg");
	var username = passwordElement.val();
	var isTooShort = isPasswordTooShort(username);
	var isTooLong = isPasswordTooLong(username);
	if(isTooShort || isTooLong) {
		passwordElement.removeClass("validImg");
		passwordElement.addClass("invalidImg");
	}
	if(isTooShort) {
		$("#errorTable").append(createTableRowWithText("Password should be at least 6 characters long."));
	}
	if(isTooLong) {
		$("#errorTable").append(createTableRowWithText("Password should not be longer than 64 characters."));
	}
}

function addUsernameAvailableMessage() {
	if(!usernameAvailable) {
		$("#errorTable").prepend(createTableRowWithText("Username exists already."));
	}
}

function createTableRowWithText(text) {
	return "<tr><td align = \"center\">"+text+"</td></tr>";
}

function isUsernameTooShort(username) {
	return username.length < 4;
}

function isUsernameTooLong(username) {
	return username.length > 64;
}

function isPasswordTooShort(password) {
	return password.length < 6;
}

function isPasswordTooLong(password) {
	return password.length > 64;
}