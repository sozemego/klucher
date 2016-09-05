/*jshint -W065 */

function registerOnLoad() {
	checkAvailability(null);
	attachValidationListeners();
	attachSubmitRegisterListener();
}

//sends an Ajax request to check if given username is available
function checkAvailability(lastUsername) {

	var currentUsername = $("#username").val();
	if(lastUsername === currentUsername) {
		setTimeout(checkAvailability, 2500, currentUsername);
		return;
	}

	if(currentUsername === null || currentUsername === "") {
		setTimeout(checkAvailability, 2500, currentUsername);
		return;
	}

	$.ajax({
		type: "GET",
		url: "/register/available/" + currentUsername,
		error: function(xhr, status, error) {
			// slow down the checks to let server breathe
			setTimeout(checkAvailability, 10000, currentUsername);
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
			setTimeout(checkAvailability, 1000, currentUsername);
		}
	});
}

function attachValidationListeners() {
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


function addUsernameAvailableMessage(available) {
	if(!available) {
		$("#errorTable").prepend(createTableRowWithText("Username exists already."));
	}
}

function createTableRowWithText(text) {
	return "<tr><td align = \"center\">"+text+"</td></tr>";
}

function isUsernameTooShort(username) {
	return username.length === 0;
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
	return (/\s/g).test(text);
}

function dashboardOnLoad() {
	// call getFeed with maximum (latest) allowable timestamp
	getFeed("before", Number.MAX_SAFE_INTEGER, true);
	attachCharacterCountListener();
	attachShareKluchListener();
	attachInfiniteScrollingListener();
	attachSubmitButtonListener();
	attachNewKluchElementListener();
	pollFeed();
}

function attachCharacterCountListener() {
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

function attachSubmitButtonListener() {
	$("#submitButton").click(function () {
		$("#kluchForm").submit();
	});
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
		data: {"kluchText" : kluchText },
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
			setGettingFeed(0);
		},
		success: function(data, status, xhr) {
			clearTextArea();
			addKluchToFeed(data, false);
			checkCharacterCount();
			setGettingFeed(0);
		}
	});
	focusInputArea();
}

function focusInputArea() {
	$("#kluchTextArea").focus();
}

function getKluch(username, timestamp, text) {
	return {
		author : username,
		timestamp : timestamp,
		text : text
	};
}

function getFeed(direction, timestamp, append) {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	var username = getUsername();
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed/" + username,
		data: {
			"timestamp" : timestamp,
			"direction" : direction
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
			setGettingFeed(0);
		},
		success: function(data, status, xhr) {
			addKluchsToFeed(data.elements, append);
			setGettingFeed(0);
		}
	});
}

// remembers if we've reached a Feed's last page
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
		addKluchToFeed(kluch, append);
	}
}

function addKluchToFeed(kluch, append) {
	var outerDiv = document.createElement("div");
	append ? $("#kluchFeed").append(outerDiv) : $("#kluchFeed").prepend(outerDiv);

	outerDiv.classList.toggle("kluch");
	outerDiv.classList.toggle("opacityAnimation");

	var authorDiv = document.createElement("div");
	authorDiv.classList.toggle("authorDiv");
	$("<a class = 'author' href = '/u/" + kluch.author + "'>" + kluch.author + "</span>").appendTo(authorDiv);
	$("<span class = 'dashboardTime'>" + millisToText(kluch.timestamp) + "</span>").appendTo(authorDiv);
	$(authorDiv).appendTo(outerDiv);

	var textAreaDiv = document.createElement("div");

	var loggedIn = isLoggedIn();
	if(loggedIn) {
		var username = getUsername();
		if(kluch.author == username) {
			textAreaDiv.classList.toggle("ownKluch");
		}
	}

	textAreaDiv.classList.toggle("kluchTextArea");
	textAreaDiv.classList.toggle("preWrap");

	var processedKluchText = processKluchText(kluch.text);
	$("<span>" + processedKluchText + "</span>").appendTo(textAreaDiv);
	$(textAreaDiv).appendTo(outerDiv);

	assignTimestamps(kluch);
}

function processKluchText(text) {
	text = escapeHtml(text);
	text = addLinks(text, /(?:^|\s)(@\w+)/g, getUserLinkStyle);
	text = addLinks(text, /(?:^|\s)(#\w+)/g, getHashtagStyle);
	text = addLinks(text, /(?:(?:https?|ftp|file):\/\/|www\.|ftp\.)(?:\([-A-Z0-9+&@#\/%=~_|$?!:;,.]*\)|[-A-Z0-9+&@#\/%=~_|$?!:;,.])*(?:\([-A-Z0-9+&@#\/%=~_|$?!:;,.]*\)|[A-Z0-9+&@#\/%=~_|$])/ig, getLinkStyle);
	return text;
}

function escapeHtml(text) {
	text = text.replace(/</g, "&lt");
	text = text.replace(/>/g, "&gt");
	return text;
}

/**
* finds all matches given by regex and applies styleCallback to all matches
* returns changed text
*/
function addLinks(text, regex, styleCallback) {
	var result = regex.exec(text);

	while(result !== null) {
		/**
		* since exec method returns a matched group and captured group
		* but the resulting index field points to the matched group
		* which here resulted in index field pointing to the space preceding the hashtag
		*/
		if(result.length > 1 && result[0] != result[1]) {
			result.index += 1;
		}

		var replaced = styleCallback(text.substring(result.index, regex.lastIndex));
		text = text.substring(0, result.index) + replaced + text.substring(regex.lastIndex, text.length);

		//start the search after the inserted element, which invariably makes the text longer
		regex.lastIndex += replaced.length - result[result.length - 1].length;
		result = regex.exec(text);
	}
	return text;
}

function getHashtagStyle(hashtag) {
	var toReturn = "<a class = 'hashtagLink' href = '/hashtag/" + hashtagWithoutPound(hashtag) + "'>" + hashtag + "</a>";
	return toReturn;
}

function hashtagWithoutPound(hashtag) {
	return hashtag.replace("#", "");
}

function getUserLinkStyle(user) {
	var toReturn = "<a class = 'userLink' href = '/u/" + userWithoutAt(user) + "'>" + user + "</a>";
	return toReturn;
}

function userWithoutAt(user) {
	return user.replace("@", "");
}

function getLinkStyle(link) {
	var toReturn = "<a class = 'linkInKluch' href = 'http://" + link + "'>" + link + "</a>";
	return toReturn;
}

function getUsername() {
	return $("#data").attr("data-username");
}

// sets values for last (latest) and first (earliest) timestamps
function assignTimestamps(kluch) {
	var timestamp = kluch.timestamp;
	setLastTimestamp(timestamp);
	setFirstTimestamp(timestamp);
}

function setLastTimestamp(millis) {
	var currentLastTimestamp = parseInt($("#data").attr("data-last-timestamp"));
	if(millis > currentLastTimestamp) {
		$("#data").attr("data-last-timestamp", millis);
	}
}

function setFirstTimestamp(millis) {
	var currentFirstTimestamp = parseInt($("#data").attr("data-first-timestamp"));
	if(millis < currentFirstTimestamp) {
		$("#data").attr("data-first-timestamp", millis);
	}
}

function attachInfiniteScrollingListener() {
	$(window).scroll(function(ev) {
		var windowInnerHeight = window.innerHeight;
		var scrollY = window.scrollY;
		var bodyHeight = document.body.offsetHeight;
		if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
			getFeed("before", parseInt($("#data").attr("data-first-timestamp")), true);
		}
		displayLastPageMessage();
	});
}

function displayLastPageMessage() {
	var lastPage = $("#data").attr("data-page");
	if(lastPage == -1) {
		$("#lastPage").removeClass("hidden");
	}
}

function setGettingFeed(data) {
	$("#data").attr("data-getting-feed", data);
}

function pollFeed() {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		setTimeout(pollFeed, 5000);
		return;
	}

	var timestamp = $("#data").attr("data-last-timestamp");
	var username = getUsername();
	if(username == "") {
		return;
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
			setTimeout(pollFeed, 5000);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			setTimeout(pollFeed, 5000);
			if(data) {
				displayNewKluchElement();
			}
		}
	});
}

function attachNewKluchElementListener() {
	$("#newKluch").click(clickNewKluchs);
}

function displayNewKluchElement() {
	$("#newKluch").removeClass("invisible");
}

function clickNewKluchs() {
	hideNewKluchElement();
	getFeed("after", parseInt($("#data").attr("data-last-timestamp")), false);
}

function hideNewKluchElement() {
	$("#newKluch").removeClass("invisible");
}

// converts millisecond (unix time) difference between now and given parameter to readable text
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
	getFeed("before", Number.MAX_SAFE_INTEGER, true);
	attachInfiniteScrollingListener();
	pollFeed();
	createUserButtonContainer();
	addFollowButton();
	showLoginPage(false);
	attachMouseOverOutListenersToLoginElement();
	configureSubheaderButtons();
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
	// users can get to those mappings only if they are authorised
	if(window.location.pathname == "/dashboard") {
		return true;
	}
	if(window.location.pathname == "/notifications") {
		return true;
	}
	return $("#data").attr("data-logged-in") == "true";
}

// data-follows should store whether or not you follow the current user (at /u/* mappings)
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
			displayAlert(xhr.responseJSON.message);
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
			displayAlert(xhr.responseJSON.message);
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

function configureLogoutButton() {
	var loggedIn = isLoggedIn();
	if(loggedIn) {
		return;
	}
	var logoutSpan = $("#logoutButton .buttonText");
	logoutSpan.text("login");
	var logoutImg = $("#logoutButton .logoutButtonImg");
	logoutImg.attr("src", "../../resources/images/login.png");
	var logoutButtonElement = $("#logoutButton");
	logoutButtonElement.attr("href", "#");
	logoutButtonElement.click(function(event) {
		showLoginPage(true);
	});
}

function hashtagOnLoad() {
	attachInifiteScrollingListenerHashtag();
	configureSubheaderButtons();
	showLoginPage(false);
	attachMouseOverOutListenersToLoginElement();
	getHashtagFeed(parseInt($("#data").attr("data-first-timestamp")), true);
}

function attachInifiteScrollingListenerHashtag() {
	$(window).scroll(function(ev) {
		var windowInnerHeight = window.innerHeight;
		var scrollY = window.scrollY;
		var bodyHeight = document.body.offsetHeight;
			if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
				getHashtagFeed(parseInt($("#data").attr("data-first-timestamp")), true);
			}
	});
}

function getHashtagFeed(timestamp, append) {
	var isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	var hashtag = $("#data").attr("data-hashtag");
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/hashtag/feed/" + hashtag,
		data: {
			"timestamp" : timestamp
		},
		error: function(xhr, status, error) {
			setGettingFeed(0);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			addKluchsToFeed(data.elements, append);
			setGettingFeed(0);
		}
	});
}

// configures dashboard/messages/settings buttons. basically, hides them if the user is not logged in
function configureSubheaderButtons() {
	configureLogoutButton();
	var loggedIn = isLoggedIn();
	if(!loggedIn) {
		$("#dashboardButton").toggleClass("invisible");
		$("#notificationsButton").toggleClass("invisible");
		$("#settingsButton").toggleClass("invisible");
	}
}

function displayAlert(text) {
	$("#alertContent").text(text);
	$("#alertElement").animate(
		{
			height: "120px"
		},
		150, attachHideAlert);
}

function attachHideAlert() {
	$("body").click(animateAlertUp);
}

function animateAlertUp() {
	removeAnimateAlertUp();
	$("#alertElement").animate(
		{
			height: "-120px"
		},
		150);
}

function removeAnimateAlertUp() {
	$("body").off("click", animateAlertUp);
}

function subheaderOnLoad() {
	pollNotifications();
}

function pollNotifications() {
	var loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	$.ajax({
		type: "GET",
		url: "/notification/poll/",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
			setTimeout(pollNotifications, 2500);
		},
		success: function(data, status, xhr) {
			setTimeout(pollNotifications, 2500);
			displayNewNotifications(data);
		}
	});
}

function displayNewNotifications(number) {
	var notificationsTextElement = $("#notificationsText");
	if(number <= 0) {
		notificationsTextElement.text("notifications");
	} else {
		notificationsTextElement.text("notifications (" + number + ")");
	}
}


function notificationsOnLoad() {
	getNotifications();
}

function getNotifications() {
	$.ajax({
		type: "GET",
		url: "/notification/",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			handleNotifications(data.elements);
		}
	});
}

function handleNotifications(notifications) {
	var kluchIds = [];
	var newFollowers = [];
	for(var i = 0; i < notifications.length; i++) {
		if(notifications[i].kluchId != null) {
			kluchIds.push(notifications[i].kluchId);
		}
		if(notifications[i].follow != null) {
			newFollowers.push(notifications[i].follow);
		}
	}
	getKluchs(kluchIds);
	displayNewFollowers(newFollowers);
	setTimeout(sendMarkNotificationsAsRead, 750);
}

function getKluchs(kluchIds) {
	if(kluchIds.length == 0) {
		return;
	}
	$.ajax({
		dataType: "json",
		contentType : "application/json; charset=utf-8",
		type: "GET",
		data: {
			"kluchIds" : kluchIds
		},
		url: "/feed/notification",
		error: function(xhr, status, error) {
			
		},
		success: function(data, status, xhr) {
			addKluchsToFeed(data.elements);			
		}
	});	
}

function sendMarkNotificationsAsRead() {
$.ajax({
		type: "POST",
		url: "/notification/read",
		error: function(xhr, status, error) {
			
		},
		success: function(data, status, xhr) {
			displayNewNotifications(0);
		}
	});	
}

function displayNewFollowers(followers) {
	if(followers.length == 0) {
		return;
	}
	var users = [];
	for(var i = 0; i < followers.length; i++) {
		users.push(getUserLinkStyle(followers[i]));
	}
	
	// constructs a message to display with all followers
	// who followed you recently. very messy. also, logic does not check out
	var message = "";
	var namesToDisplay = 1;
	for(var i = 0; i < users.length; i++) {
		if(i > 0) {
			if(i == users.length - 1) {
				message += " and ";
			} else {
				message += ", ";
			}
		}
		message += users[i];
		if(i == namesToDisplay - 1) {
			var remainingFollowers = (users.length - (i + 1));
			message += " and " + createRemainingFollowersElement(remainingFollowers) + " more";
			break;
		}

	}
	message += " followed you.";
	createEventListenersForRemainingFollowersList();
	createRemainingFollowersList(followers.slice(namesToDisplay));
	$("#newFollowersText").html(message);
}

function createRemainingFollowersElement(remainingFollowers) {

	var text = "<span id = 'remainingFollowers' class = 'remainingFollowers'>" + remainingFollowers + "</span>";
	return text;

}

function createEventListenersForRemainingFollowersList() {
	
	$("body").on("mouseenter", "#remainingFollowers", function(event) {
		showRemainingFollowersList(true);
	});

	$("body").on("click", "#remainingFollowers", function(event) {
		$("#remainingFollowersList").fadeToggle('fast');
	});

	$("body").on("mousemove", function(event) {

		var boundingRect = $("#remainingFollowersList")[0].getBoundingClientRect();
		var distance = pointRectDist(event.pageX, event.pageY, boundingRect.left, boundingRect.top, boundingRect.width, boundingRect.height);
		var distanceToFadeOut = 25;
		if(distance > distanceToFadeOut) {
			showRemainingFollowersList(false);
		}

	});
}

// calculates a distance from any of the rectangles edges
function pointRectDist (px, py, rx, ry, rWidth, rHeight) {
    var cx = Math.max(Math.min(px, rx + rWidth ), rx);
    var cy = Math.max(Math.min(py, ry + rHeight), ry);
    return Math.sqrt((px-cx)*(px-cx) + (py-cy)*(py-cy));
}

function showRemainingFollowersList(bool) {
	if(bool) {

		var position = $("#remainingFollowers").position();
		var followersList = $("#remainingFollowersList");
		
		followersList.css({
			"top": position.top + 18,
			"left": position.left + 10
		});
		
		followersList.fadeIn();

	} else {

		var followersList = $("#remainingFollowersList");
		followersList.fadeOut();

	}
}

function createRemainingFollowersList(remainingFollowers) {

	var remainingFollowersListExists = $("#remainingFollowersList").length !== 0;

	if(!remainingFollowersListExists) {
		var element = $(document.createElement("div"));
		element.toggleClass("remainingFollowersList");
		element.toggleClass("remainingListBackground");
		element.attr("id", "remainingFollowersList");
		for(var i = 0; i < remainingFollowers.length; i++) {
			var name = $(document.createElement("a"));
			name.toggleClass('block');
			name.toggleClass("userLink");
			name.text(remainingFollowers[i]);
			name.attr("href", "/u/" + remainingFollowers[i]);
			element.append(name);
		}
		$("body").append(element);
	}

}