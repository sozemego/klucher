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