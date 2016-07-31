function registerOnLoad() {
	checkAvailibility(null);
	validateStart();
	attachSubmitRegisterListener();
}

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
		url: "/register/available/" + currentUsername,
		error: function(xhr, status, error) {	
			setTimeout(checkAvailibility, 2500, currentUsername);
		},
		success: function(data) {
			var currentUsernameElement = $("#username");
			if(data) {
				currentUsernameElement.removeClass("unavailable");
				currentUsernameElement.addClass("available");				
			} else {
				currentUsernameElement.removeClass("available");
				currentUsernameElement.addClass("unavailable");			
			}
			addUsernameAvailableMessage(data);
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

function attachSubmitRegisterListener() {
	$("#registerForm").submit(function(event) {
		validateRegisterForm();
		var errors = $("#errorTable").children().length;
		if(errors > 0) {
			event.preventDefault();
			return false;
		}
		$("#registerForm").submit();
		
	});
}

function validateRegisterForm() {
	clearErrorTable();
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
	var whiteSpace = hasWhiteSpace(username);
	if(isTooShort || isTooLong || whiteSpace) {
		usernameElement.removeClass("validImg");
		usernameElement.addClass("invalidImg");
	}
	if(isTooShort) {
		$("#errorTable").append(createTableRowWithText("Username should be at least 1 character long."));
	}
	if(isTooLong) {
		$("#errorTable").append(createTableRowWithText("Username should not be longer than 32 characters."));
	}
	if(whiteSpace) {
		$("#errorTable").append(createTableRowWithText("Username cannot contain white space."));
	}
}

function validatePassword() {
	var passwordElement = $("#password");
	passwordElement.removeClass("invalidImg");
	passwordElement.addClass("validImg");
	var password = passwordElement.val();
	var isTooShort = isPasswordTooShort(password);
	var isTooLong = isPasswordTooLong(password);
	var whiteSpace = hasWhiteSpace(password);
	if(isTooShort || isTooLong || whiteSpace) {
		passwordElement.removeClass("validImg");
		passwordElement.addClass("invalidImg");
	}
	if(isTooShort) {
		$("#errorTable").append(createTableRowWithText("Password should be at least 6 characters long."));
	}
	if(isTooLong) {
		$("#errorTable").append(createTableRowWithText("Password should not be longer than 64 characters."));
	}
	if(whiteSpace) {
		$("#errorTable").append(createTableRowWithText("Password cannot contain white space."));
	}
}

function addUsernameAvailableMessage(bool) {
	if(!bool) {
		$("#errorTable").prepend(createTableRowWithText("Username exists already."));
	}
}

function createTableRowWithText(text) {
	return "<tr><td align = \"center\">"+text+"</td></tr>";
}

function isUsernameTooShort(username) {
	return username.length == 0;
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

function hasWhiteSpace(text) {
	var toReturn = /\s/g.test(text);
	return toReturn;
}

function dashboardOnLoad() {
	getFeed("after");
	attachInputListener();
	attachShareKluchListener();
	attachInfiniteScrollingListener();
	pollFeed();
	attachSubmitButtonListener();
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
	setGettingFeed(1);
	$.ajax({
		type: "POST",
		url: "/kluch",
		data: {"kluch" : kluchText },
		error: function(xhr, status, error) {	
			var something = xhr.getAllResponseHeaders();
			setGettingFeed(0);
		},
		success: function(data, status, xhr) {
			var currentTimestamp = Date.now();
			setFirstTimestamp(currentTimestamp);
			clearTextArea();
			addKluchToFeed(getUsername(), millisToText(currentTimestamp), kluchText, false);
			checkCharacterCount();	
			setGettingFeed(0);
		}
	});
	focusInputArea();
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
			setLastTimestamp(kluchs[kluchs.length - 1].timestamp);
			setFirstTimestamp(kluchs[0].timestamp);
		} else {
			setLastTimestamp(kluchs[0].timestamp);
			setFirstTimestamp(kluchs[kluchs.length - 1].timestamp);
		}
	}
}

function addKluchToFeed(author, timeText, text, append) {
	text = escapeHtml(text);
	if(typeof author === "undefined") {
		return;
	}
	var outerDiv = document.createElement("div");
	append ? $("#kluchFeed").append(outerDiv) : $("#kluchFeed").prepend(outerDiv);
	outerDiv.classList.toggle("kluch");
	outerDiv.classList.toggle("opacityAnimation");
	var authorDiv = document.createElement("div");
	authorDiv.classList.toggle("authorDiv");
	$("<span class = 'author'>" + author + "</span>").appendTo(authorDiv);
	$("<span class = 'dashboardTime'>" + timeText + "</span>").appendTo(authorDiv);
	$(authorDiv).appendTo(outerDiv);
	var textAreaDiv = document.createElement("div");
	textAreaDiv.classList.toggle("kluchTextArea");
	textAreaDiv.classList.toggle("preWrap");
	$("<span>" +  text + "</span>").appendTo(textAreaDiv);
	$(textAreaDiv).appendTo(outerDiv);
}

function escapeHtml(text) {
	text = text.replace(/</g, "&lt");
	text = text.replace(/>/g, "&gt");
	return text;
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
	        getFeed();
	    }
	    displayLastPageMessage();
	});
}

function setGettingFeed(data) {
	$("#data").attr("data-getting-feed", data);
}

function setLastTimestamp(millis) {
	var currentLastTimestamp = parseInt($("#data").attr("data-last-timestamp"));
	if(currentLastTimestamp > millis) {
		$("#data").attr("data-last-timestamp", millis);
	}
}

function setFirstTimestamp(millis) {
	var currentFirstTimestamp = parseInt($("#data").attr("data-first-timestamp"));
	if(currentFirstTimestamp < millis) {
		$("#data").attr("data-first-timestamp", millis);
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
				displayNewKluchElement(clickNewKluchs);
			}
		}
	});
}

function displayNewKluchElement(newKluchCallback) {
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
		newKluchText.click(newKluchCallback);	
	}
}

function focusInputArea() {
	$("#kluchTextArea").focus();
}

function clickNewKluchs() {
	hideNewKluchElement();
	getFeedBefore();
}

function clickNewKluchsUnauthorized() {
	hideNewKluchElement();
	getFeedUnauthorizedBefore();
}

function hideNewKluchElement() {
	$("#newKluch").empty();
}

function attachSubmitButtonListener() {
	$("#submitButton").click(function () {
		$("#kluchForm").submit();
	});
}

function displayLastPageMessage() {
	var lastPage = $("#data").attr("data-page");
	if(lastPage == -1) {
		$("#lastPage").removeClass("hidden");
	}
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

function userOnLoad() {
	getFeedUnauthorizedBefore();
	attachInfiniteScrollingListener();
	pollFeedUnauthorized();	
	createUserButtonContainer();
	addFollowButton();
	showLoginPage(false);
	attachMouseOverOutListenersToLoginElement();
}

function pollFeedUnauthorized() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		setTimeout(pollFeedUnauthorized, 5000);
		return;
	}
	var username = $("#data").attr("data-username");
	var timestamp = $("#data").attr("data-first-timestamp");
	if(timestamp == null) {
		setTimeout(pollFeed, 5000);
	}
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed/poll/" + username,
		data: {
			"timestamp" : timestamp,
			"direction" : "after"
		},
		error: function(xhr, status, error) {	
			setTimeout(pollFeedUnauthorized, 5000);
		},
		success: function(data, status, xhr) {
			setTimeout(pollFeedUnauthorized, 5000);
			if(data) {
				displayNewKluchElement(clickNewKluchsUnauthorized);
			}
		}
	});
}

function getFeedUnauthorizedBefore() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	var timestamp = parseInt($("#data").attr("data-first-timestamp"));	
	var username = $("#data").attr("data-username");
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed" + "/" + username,
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

function createUserButtonContainer() {
	$("#userButtonContainer").removeClass("hidden");
	$("#userButtonContainer").addClass("userButtonContainer");
}

function addFollowButton() {
	var loggedIn = isLoggedIn();
	if(!loggedIn) {
		createFollowButtonLeadsToLogin();
	}
	if(loggedIn) {
		var follows = doesFollow();
		if(follows) {
			createUnfollowButton();
		} else {
			createFollowButton();
		}		
	}
}

function isLoggedIn() {
	return $("#data").attr("data-logged-in") == "true";
}

function doesFollow() {
	return $("#data").attr("data-follows") == "true";
}

function createFollowButton() {
	$("#followText").empty();
	var followText = "Follow";
	var followTextToAppend = "<span class = 'followText'>" + followText + "</span>";
	$(document.createTextNode(followText)).appendTo("#followText");
	var src = "../../resources/images/lasso.png";
	$("#followImage").attr("src", src);
	addFollowListener();
}

function addFollowListener() {
	$("#followButton").unbind("click");
	$("#followButton").click(function() {
		followUserAjax();
	});
}

function followUserAjax() {
	var loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	var follows = doesFollow();
	if(follows) {
		return;
	}
	var username = $("#data").attr("data-username");
	$.ajax({
		type: "POST",
		url: "/user/follow",
		data: {
			"follow" : username
		},
		error: function(xhr, status, error) {	
					
		},
		success: function(data, status, xhr) {
			createUnfollowButton();
			setFollow(true);
		}
	});
}

function createUnfollowButton() {
	$("#followText").empty();
	var followText = "Unfollow";
	var followTextToAppend = "<span class = 'followText'>" + followText + "</span>";
	$(document.createTextNode(followText)).appendTo("#followText");
	var src = "../../resources/images/invisible.png";
	$("#followImage").attr("src", src);
	addUnfollowListener();
}

function addUnfollowListener() {
	$("#followButton").unbind("click");
	$("#followButton").click(function() {
		unfollowUserAjax();
	});
}

function unfollowUserAjax() {
	var loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	var follows = doesFollow();
	if(!follows) {
		return;
	}
	var username = $("#data").attr("data-username");
	$.ajax({
		type: "POST",
		url: "/user/unfollow",
		data: {
			"follow" : username
		},
		error: function(xhr, status, error) {	
					
		},
		success: function(data, status, xhr) {
			createFollowButton();
			setFollow(false);
		}
	});
}

function setFollow(bool) {
	$("#data").attr("data-follows", bool);
}

function createFollowButtonLeadsToLogin() {
	createFollowButton();
	addRedirectToLoginListener();
}

function addRedirectToLoginListener() {
	$("#followButton").unbind("click");
	$("#followButton").click(function() {
		showLoginPage(true);
	});
}

function redirectToLogin() {
	window.location.href = "/login";
}

function showLoginPage(bool) {
	if(!bool) {
		$("#loginTable").addClass("hidden");
		removeOverlay();
	} else {
		$("#loginTable").removeClass("hidden");
		addOverlay();
	}
	
}

function addOverlay() {
	var overlay = "<div class = 'darkOverlay'></div>";
	$(overlay).appendTo("body");
	setTimeout(attachClickOutsideLoginElementListener, 50);
}

function removeOverlay() {
	$("body").children(".darkOverlay").remove();
	detachClickOutsideLoginElementListener();
}

function attachClickOutsideLoginElementListener() {
	$("body").click(function(event) {
		var isActive = $("#loginTable").hasClass("active");
		if(!isActive) {
			showLoginPage(false);
		}
 	});
}



function detachClickOutsideLoginElementListener() {
	$("body").off("click");
}

function attachMouseOverOutListenersToLoginElement() {
	$("#loginTable").hover(function () {
		$("#loginTable").addClass("active");
	}, function() {
		$("#loginTable").removeClass("active");
	});
}

function toggleLoginTableActiveClass() {
	$("#loginTable").toggleClass("active");
}


