function registerOnLoad() {
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
		validateRegisterForm();
	});
	$("#username").blur(function() {
		validateRegisterForm();
	});
	$("#password").keyup(function() {
		validateRegisterForm();
	});
	$("#password").blur(function() {
		validateRegisterForm();
	});
}

function validateRegisterForm() {
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
		$("#errorTable").append(createTableRowWithText("Username should not be longer than 32 characters."));
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
	return username.length > 32;
}

function isPasswordTooShort(password) {
	return password.length < 6;
}

function isPasswordTooLong(password) {
	return password.length > 64;
}

function dashboardOnLoad() {
	getFeed("after");
	attachInputListener();
	attachShareKluchListener();
	attachInfiniteScrollingListener();
	pollFeed();
}

function attachInputListener() {
	$("#kluchTextArea").keyup(function() {
		checkCharacterCount();
	});
}

function checkCharacterCount() {
	var textArea = $("#kluchTextArea");
	var charactersLeft = charactersRemaining(textArea.val(), 250);
	var charactersLeftElement = $("#charactersLeft");
	if(charactersLeft < 0) {
		var text = textArea.val();
		text = text.slice(0, 250);
		textArea.val(text);
	}
	charactersLeftElement.empty();
	charactersLeftElement.append(charactersRemaining(textArea.val(), 250));
}

function charactersRemaining(text, length) {
	return length - text.length;
}

function attachShareKluchListener() {
	$("#kluchForm").submit(function(event) {
		event.preventDefault();
		ajaxPostKluch();
	});
}

function ajaxPostKluch() {
	var kluchText = $("#kluchTextArea").val();
	if(kluchText.length === 0) {
		return;
	}
	$.ajax({
		type: "POST",
		url: "/kluch",
		data: {"kluch" : kluchText },
		error: function(xhr, status, error) {	
			var something = xhr.getAllResponseHeaders();
			
		},
		success: function(data, status, xhr) {
			clearTextArea();
			addKluchToFeed(getUsername(), new Date(), kluchText, false);
		}
	});
}

function getFeed() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	var page = $("#data").attr("data-page");
	if(page == -1) {
		return;
	}
	setGettingFeed(1);
	var timestamp = parseInt($("#data").attr("data-last-timestamp"));	
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed",
		data: {
			"timestamp" : timestamp,
			"direction" : "after"
		},
		error: function(xhr, status, error) {	
			setGettingFeed(0);			
		},
		success: function(data, status, xhr) {
			addKluchsToFeed(data.kluchs.content, true);
			setPage(data);
			setGettingFeed(0);
		}
	});
}

function getFeedBefore() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	var timestamp = parseInt($("#data").attr("data-first-timestamp"));	
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed",
		data: {
			"timestamp" : timestamp,
			"direction" : "before"
		},
		error: function(xhr, status, error) {	
			setGettingFeed(0);			
		},
		success: function(data, status, xhr) {
			addKluchsToFeed(data.kluchs.content, false);
			setGettingFeed(0);
		}
	});
}

function setPage(data) {
	var page = data.kluchs;
	if(page.last) {
		$("#data").attr("data-page", -1);
	} 
}

function clearTextArea() {
	$("#kluchTextArea").val("");
}

function addKluchsToFeed(kluchs, append) {
	for(var i = 0; i < kluchs.length; i++) {
		var kluch = kluchs[i];		
		addKluchToFeed(kluch.author, millisToText(kluch.timestamp), kluch.text, append);
	}
	if(kluchs.length > 0) {	
		if(append) {
			setLastTimestamp(kluchs[kluchs.length - 1]);
			setFirstTimestamp(kluchs[0]);
		} else {
			setLastTimestamp(kluchs[0]);
			setFirstTimestamp(kluchs[kluchs.length - 1]);
		}
	}
}

function addKluchToFeed(author, timeText, text, append) {
	if(typeof author === "undefined") {
		
	}
	var outerDiv = document.createElement("div");
	append ? $("#kluchFeed").append(outerDiv) : $("#kluchFeed").prepend(outerDiv);
	outerDiv.classList.toggle("kluch");
	var authorDiv = document.createElement("div");
	//authorDiv.classList.toggle("author");
	$("<span class = 'author'>" + author + "</span>").appendTo(authorDiv);
	$("<span class = 'dashboardTime'>" + timeText + "</span>").appendTo(authorDiv);
	$(authorDiv).appendTo(outerDiv);
	var textAreaDiv = document.createElement("div");
	textAreaDiv.classList.toggle("kluchTextArea");
	textAreaDiv.classList.toggle("opacityAnimation");
	$("<abc>" +  text + "</abc>").appendTo(textAreaDiv);
	$(textAreaDiv).appendTo(outerDiv);
}

function getUsername() {
	return $("#data").attr("data-username");
}

function attachInfiniteScrollingListener() {
	$(window).scroll(function(ev) {
		var windowInnerHeight = window.innerHeight;
		var scrollY = window.scrollY;
		var bodyHeight = document.body.offsetHeight;
	    if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
	        getFeed("after");
	    }
	});
}

function setGettingFeed(data) {
	$("#data").attr("data-getting-feed", data);
}

function setLastTimestamp(kluch) {
	var kluchTimestamp = kluch.timestamp;
	var currentLastTimestamp = parseInt($("#data").attr("data-last-timestamp"));
	if(currentLastTimestamp > kluchTimestamp) {
		$("#data").attr("data-last-timestamp", kluchTimestamp);
	}
}

function setFirstTimestamp(kluch) {
	var kluchTimestamp = kluch.timestamp;
	var currentFirstTimestamp = parseInt($("#data").attr("data-first-timestamp"));
	if(currentFirstTimestamp < kluchTimestamp) {
		$("#data").attr("data-first-timestamp", kluchTimestamp);
	}
}

function pollFeed() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		setTimeout(pollFeed, 5000);
		return;
	}
	var timestamp = $("#data").attr("data-first-timestamp");
	if(timestamp == null) {
		setTimeout(pollFeed, 5000);
	}
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed/poll",
		data: {
			"timestamp" : timestamp,
			"direction" : "after"
		},
		error: function(xhr, status, error) {	
			setTimeout(pollFeed, 5000);
		},
		success: function(data, status, xhr) {
			setTimeout(pollFeed, 5000);
			if(data) {
				displayNewKluchElement();
			}
		}
	});
}

function displayNewKluchElement() {
	var newKluchElement = $("#newKluch");
	var hasChildren = newKluchElement.children().length != 0;
	if(!hasChildren) {
		$("<span id = 'newKluchText'>new kluchs available, click here to view!</span>").appendTo("#newKluch");
		var newKluchText = $("#newKluchText");
		newKluchText.addClass("opacityAnimation");
		newKluchText.addClass("centerText");
		newKluchText.addClass("roundedCorners");
		newKluchText.addClass("verticalCenter");
		newKluchText.addClass("cursorPointer");
		newKluchText.click(clickNewKluchs);	
	}
}

function clickNewKluchs() {
	hideNewKluchElement();
	getFeedBefore();
}

function hideNewKluchElement() {
	$("#newKluch").empty();
}

function millisToText(millis) {
	var date = new Date(millis);
	var now = new Date();
	var millisNow = now.getTime();
	var millisDifference = millisNow - date.getTime();
	var minutesPassed = Math.floor(this.minutesPassed(millisDifference));
	if(minutesPassed < 60) {
		if(minutesPassed === 0) {
			return "less than a minute ago";
		} 
		if(minutesPassed === 1) {
			return "a minute ago";
		}
		return minutesPassed + " minutes ago";
	}
	var hoursPassed = Math.floor(this.hoursPassed(minutesPassed));
    if(hoursPassed < 24) {
    	if(hoursPassed === 1) {
    		return "an hour ago";
    	}
    	return hoursPassed + " hours ago";
    }
    var daysPassed = Math.floor(this.daysPassed(hoursPassed));
    if(daysPassed === 1) {
    	return "a day ago";
    }
    return daysPassed + " days ago";
}

function minutesPassed(millis) {
	var seconds = millis / 1000;
	return seconds / 60;
}

function hoursPassed(minutes) {
	return minutes / 60;
}

function daysPassed(hours) {
	return hours / 24;
}



