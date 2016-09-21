/*jshint esversion: 6 */

function registerOnLoad() {
	checkAvailability(null);
	attachValidationListeners();
	attachSubmitRegisterListener();
	validateRegisterForm();
}

//sends an Ajax request to check if given username is available
function checkAvailability(lastUsername) {

	const currentUsername = $("#register-form-username").val();
	if(hasWhiteSpace(currentUsername)) {
		setTimeout(checkAvailability, 2500, currentUsername);
		return;
	}

	if(lastUsername === currentUsername) {
		setTimeout(checkAvailability, 2500, currentUsername);
		return;
	}

	if(currentUsername === undefined || currentUsername === null || currentUsername === "") {
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
			displayUsernameAvailable(data);
			setTimeout(checkAvailability, 1000, currentUsername);
		}
	});
}

function attachValidationListeners() {
	$("#register-form-username").on("keyup click blur", function() {
		validateRegisterForm();
	});
	$("#register-form-password").on("keyup click blur", function() {
		validateRegisterForm();
	});
}

function validateRegisterForm() {
	clearErrors();
	validateUsername();
	validatePassword();
	isUsernameAvailable();
}

function clearErrors() {
	$("#register-errors").empty();
}

function validateUsername() {

	const usernameElement = $("#register-form-username");
	const username = usernameElement.val();

	const isTooShort = isUsernameTooShort(username);
	const isTooLong = isUsernameTooLong(username);
	const whiteSpace = hasWhiteSpace(username);

	if(isTooShort) {
		$("#register-errors").append(createDivWithText("Username should be at least 1 character long."));
	}
	if(isTooLong) {
		$("#register-errors").append(createDivWithText("Username should not be longer than 32 characters."));
	}
	if(whiteSpace) {
		$("#register-errors").append(createDivWithText("Username cannot contain white space."));
	}

	usernameElement.removeClass("form-input-field-invalid");
	if(isTooShort || isTooLong || whiteSpace) {
		usernameElement.addClass("form-input-field-invalid");
	} 
}

function validatePassword() {

	const passwordElement = $("#register-form-password");
	const password = passwordElement.val();

	const isTooShort = isPasswordTooShort(password);
	const isTooLong = isPasswordTooLong(password);
	const whiteSpace = hasWhiteSpace(password);

	if(isTooShort) {
		$("#register-errors").append(createDivWithText("Password should be at least 6 characters long."));
	}
	if(isTooLong) {
		$("#register-errors").append(createDivWithText("Password should not be longer than 64 characters."));
	}
	if(whiteSpace) {
		$("#register-errors").append(createDivWithText("Password cannot contain white space."));
	}

	passwordElement.removeClass("form-input-field-invalid");
	if(isTooShort || isTooLong || whiteSpace) {
		passwordElement.addClass("form-input-field-invalid");
	} 
}

function attachSubmitRegisterListener() {
	$("#register-form").submit(function(event) {
		validateRegisterForm();
		const errors = $("#register-errors").children().length + $("#register-availability-errors").children().length;
		if(errors > 0) {
			event.preventDefault();
			return false;
		}
	});
}

function displayUsernameAvailable(available) {
	const currentUsernameElement = $("#register-form-username");
	$("#register-availability-errors").empty();
	if(!available) {
		currentUsernameElement.addClass("form-input-field-invalid");
		$("#register-availability-errors").prepend(createDivWithText("Username exists already."));
	} 
}

function createDivWithText(text) {
	return "<div>"+text+"</div>";
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

function isUsernameAvailable() {
	const errors = $("#register-availability-errors").children().length > 0;
	const usernameElement = $("#register-form-username");
	if(errors) {
		usernameElement.addClass("form-input-field-invalid");
	} 
}

function dashboardOnLoad() {
	// call getFeed with maximum (latest) allowable timestamp
	attachCharacterCountListener();
	getFeed("before", Number.MAX_SAFE_INTEGER, true);
	attachShareButtonListener();
	attachInfiniteScrollingListener();
	attachNewKluchElementListener();
	pollFeed();
	pollNotifications();
}

function attachCharacterCountListener() {
	$("#kluch-text").keyup(function() {
		checkCharacterCount();
	});
}

function checkCharacterCount() {
	const textArea = $("#kluch-text");
	const maxCharacters = 250;
	const charactersLeft = charactersRemaining(textArea.val(), maxCharacters);
	const charactersLeftElement = $("#kluch-characters-remaining");
	if(charactersLeft < 0) {
		let text = textArea.val();
		text = text.slice(0, 250);
		textArea.val(text);
	}
	charactersLeftElement.empty();
	charactersLeftElement.append(charactersRemaining(textArea.val(), maxCharacters));
}

function charactersRemaining(text, length) {
	return length - text.length;
}

function attachShareButtonListener() {
	$("#kluch-share-button").on("click", function() {
		ajaxPostKluch();
	});
}

function ajaxPostKluch() {
	const kluchText = $("#kluch-text").val();
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
		success: function(kluch, status, xhr) {
			clearTextArea();
			addKluchToFeed(kluch, getUserData(), false);
			checkCharacterCount();
			setGettingFeed(0);
		}
	});
	focusInputArea();
}

function getUserData() {
	const username = getUsername();
	const avatarPath = getAvatarPath();
	return  {
		"username" : username,
		"avatarPath" : avatarPath
	};
}

function getAvatarPath() {
	return $("#data").attr("data-avatar-path");
}

function focusInputArea() {
	$("#kluch-text").focus();
}

function clearTextArea() {
	$("#kluch-text").val("");
}

function getKluch(username, timestamp, text) {
	return {
		author : username,
		timestamp : timestamp,
		text : text
	};
}

function getFeed(direction, timestamp, append) {
	const isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	const username = getUsername();
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
			addKluchUserFeed(data, append);
			setGettingFeed(0);
		}
	});
}

function addKluchUserFeed(feed, append) {
	const feedElements = feed.elements;
	for(let i = 0; i < feedElements.length; i++) {
		addKluchToFeed(feedElements[i].kluch, feedElements[i].user, append);
	}
}

function addKluchToFeed(kluch, user, append) {

	const kluchContainer = $(document.createElement("div"));
	kluchContainer.attr("data-id", kluch.id);
	kluchContainer.addClass("kluch-container");

	const kluchAvatar = $(document.createElement("img"));
	kluchAvatar.addClass("kluch-avatar");
	kluchAvatar.attr("src", "../../resources/images/" + user.avatarPath);
	kluchContainer.append(kluchAvatar);

	const kluchBody = $(document.createElement("div"));
	kluchBody.addClass("kluch-body");

	const kluchHeader = $(document.createElement("div"));
	kluchHeader.addClass("kluch-header");

	const kluchAuthor = $(document.createElement("a"));
	kluchAuthor.addClass("kluch-header-author");
	kluchAuthor.attr("href", "/u/" + kluch.author);
	kluchAuthor.text(kluch.author);
	

	const kluchTime = $(document.createElement("span"));
	kluchTime.addClass("kluch-header-time");
	kluchTime.text(millisToText(kluch.timestamp));

	kluchHeader.append(kluchAuthor);
	kluchHeader.append(kluchTime);

	kluchBody.append(kluchHeader);

	const kluchContent = $(document.createElement("div"));
	kluchContent.addClass("kluch-content");

	const kluchText = $(document.createElement("span"));
	kluchText.addClass("kluch-text");
	const processedKluchText = processKluchText(kluch.text);
	kluchText.html(processedKluchText);

	kluchContent.append(kluchText);

	const kluchFooter = $(document.createElement("div"));
	kluchFooter.addClass("kluch-footer");

	const likeButton = $(document.createElement("div"));
	likeButton.addClass("kluch-footer-icon-container");
	const likeImg = $(document.createElement("img"));
	likeImg.addClass("kluch-footer-icon");
	likeImg.attr("src", "../../resources/images/like_2.png");

	likeImg.hover(function() {
		$(this).attr("src", "../../resources/images/like_2_hover.png");
	}, function() {
		$(this).attr("src", "../../resources/images/like_2.png");
	});

	const loggedIn = isLoggedIn();

	likeButton.append(likeImg);
	kluchFooter.append(likeButton);

	if(loggedIn) {
			const username = getUsername();
			if(username === kluch.author) {
			const deleteButton = $(document.createElement("div"));
			deleteButton.addClass("kluch-footer-icon-container");
			const deleteImg = $(document.createElement("img"));
			deleteImg.addClass("kluch-footer-icon");
			deleteImg.attr("src", "../../resources/images/delete_2.png");

			deleteImg.hover(function() {
				$(this).attr("src", "../../resources/images/delete_2_hover.png");
			}, function() {
				$(this).attr("src", "../../resources/images/delete_2.png");
			});

			deleteButton.click({container: kluchContainer}, function(event) {
				const kluchId = event.data.container.attr("data-id");
				$.ajax({
					type: "DELETE",
					url: "/kluch?kluchId=" + kluchId,
					error: function(xhr, status, error) {
						displayAlert(xhr.responseJSON.message);
					},
					success: function(data, status, xhr) {
						displayAlert("Kluch deleted!");
						event.data.container.remove();
					}
				});
			});

			deleteButton.append(deleteImg);
			kluchFooter.append(deleteButton);
		}
	}

	
	

	//configure event listeners

	//check if own kluch ../../resources/images/delete_1.png kluch-footer-icon-delete

	kluchBody.append(kluchContent);
	kluchBody.append(kluchFooter);

	kluchContainer.append(kluchBody);

	if(append) {
		$("#kluch-feed").append(kluchContainer);
	} else {
		$("#kluch-feed").prepend(kluchContainer);
	}

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
	let result = regex.exec(text);

	while(result !== null) {
		/**
		* since exec method returns a matched group and captured group
		* but the resulting index field points to the matched group
		* which here resulted in index field pointing to the space preceding the hashtag
		*/
		if(result.length > 1 && result[0] != result[1]) {
			result.index += 1;
		}

		const replaced = styleCallback(text.substring(result.index, regex.lastIndex));
		text = text.substring(0, result.index) + replaced + text.substring(regex.lastIndex, text.length);

		//start the search after the inserted element, which invariably makes the text longer
		regex.lastIndex += replaced.length - result[result.length - 1].length;
		result = regex.exec(text);
	}
	return text;
}

function getHashtagStyle(hashtag) {
	return "<a class = 'kluch-text-hashtag-link' href = '/hashtag/" + hashtagWithoutPound(hashtag) + "'>" + hashtag + "</a>";
}

function hashtagWithoutPound(hashtag) {
	return hashtag.replace("#", "");
}

function getUserLinkStyle(user) {
	return "<a class = 'kluch-text-user-link' href = '/u/" + userWithoutAt(user) + "'>" + user + "</a>";
}

function userWithoutAt(user) {
	return user.replace("@", "");
}

function getLinkStyle(link) {
	return "<a class = 'kluch-text-link-link' href = 'http://" + link + "'>" + link + "</a>";
}

function getUsername() {
	return $("#data").attr("data-username");
}

// sets values for last (latest) and first (earliest) timestamps
function assignTimestamps(kluch) {
	const timestamp = kluch.timestamp;
	setLastTimestamp(timestamp);
	setFirstTimestamp(timestamp);
}

function setLastTimestamp(millis) {
	const currentLastTimestamp = parseInt($("#data").attr("data-last-timestamp"));
	if(millis > currentLastTimestamp) {
		$("#data").attr("data-last-timestamp", millis);
	}
}

function setFirstTimestamp(millis) {
	const currentFirstTimestamp = parseInt($("#data").attr("data-first-timestamp"));
	if(millis < currentFirstTimestamp) {
		$("#data").attr("data-first-timestamp", millis);
	}
}

function attachInfiniteScrollingListener() {
	$(window).scroll(function(ev) {
		const windowInnerHeight = window.innerHeight;
		const scrollY = window.scrollY;
		const bodyHeight = document.body.offsetHeight;
		if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
			getFeed("before", parseInt($("#data").attr("data-first-timestamp")), true);
		}
		displayLastPageMessage();
	});
}

function displayLastPageMessage() {
	const lastPage = $("#data").attr("data-page");
	if(lastPage == -1) {
		$(".kluch-no-more").addClass("kluch-no-more-visible");
	}
}

function setGettingFeed(data) {
	$("#data").attr("data-getting-feed", data);
}

function pollFeed() {
	const isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		setTimeout(pollFeed, 10000);
		return;
	}

	const timestamp = $("#data").attr("data-last-timestamp");
	const username = getUsername();
	if(username === undefined || username === null || username === "") {
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
			setTimeout(pollFeed, 10000);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			setTimeout(pollFeed, 10000);
			if(data) {
				displayNewKluchElement();
			}
		}
	});
}

function attachNewKluchElementListener() {
	$("#new-kluch-alert").click(clickNewKluchs);
}

function displayNewKluchElement() {
	$("#new-kluch-alert").addClass("dashboard-new-kluch-alert-visible");
}

function clickNewKluchs() {
	hideNewKluchElement();
	getFeed("after", parseInt($("#data").attr("data-last-timestamp")), false);
}

function hideNewKluchElement() {
	$("#new-kluch-alert").removeClass("dashboard-new-kluch-alert-visible");
}

// converts millisecond (unix time) difference between now and given parameter to readable text
function millisToText(millis) {
	const now = new Date();
	const millisNow = now.getTime();
	const millisDifference = millisNow - millis;
	const minutesPassed = Math.floor(getMinutesPassed(millisDifference));
	if(minutesPassed < 60) {
		if(minutesPassed <= 0) {
			return "less than a minute ago";
		}
		if(minutesPassed === 1) {
			return "a minute ago";
		}
		return minutesPassed + " minutes ago";
	}
	const hoursPassed = Math.floor(getHoursPassed(minutesPassed));
    if(hoursPassed < 24) {
    	if(hoursPassed === 1) {
    		return "an hour ago";
    	}
    	return hoursPassed + " hours ago";
    }
    const daysPassed = Math.floor(getDaysPassed(hoursPassed));
    if(daysPassed === 1) {
    	return "a day ago";
    }
    return daysPassed + " days ago";
}

function getMinutesPassed(millis) {
	const seconds = millis / 1000;
	return seconds / 60;
}

function getHoursPassed(minutes) {
	return minutes / 60;
}

function getDaysPassed(hours) {
	return hours / 24;
}

function userOnLoad() {
	getFeed("before", Number.MAX_SAFE_INTEGER, true);
	attachInfiniteScrollingListener();
	pollFeed();
	pollNotifications();
	setUpUserButtons();
	setUpLoginForm();
	configureSubheaderButtons();
}

function setUpUserButtons() {
	const loggedIn = isLoggedIn();
	if(loggedIn) {
		setUpUserButtonsLoggedIn();
	} else {
		setUpUserButtonsLoggedOut();
	}
}

function setUpUserButtonsLoggedIn() {
	setUpFollowButtonLoggedIn();
}

function setUpUserButtonsLoggedOut() {
	attachLoginOnClick($("#user-button-follow"));
	$("#user-button-like").addClass("user-button-inactive");
	$("#user-button-poke").addClass("user-button-inactive");
}

function setUpFollowButtonLoggedIn() {
	const follows = doesFollow();
	if(follows) {
		setUpUnfollowButton();
	} else {
		setUpFollowButton();
	}
}

function setUpFollowButton() {
	const userButtonFollowImg = $("#user-button-follow-image");
	userButtonFollowImg.attr("src", "../../resources/images/follow_1.png");
	const userButtonFollowText = $("#user-button-follow-text");
	userButtonFollowText.text("follow");
	$("#user-button-follow").unbind("click");
	$("#user-button-follow").click(function() {
		followUserAjax();
	});
}

function setUpUnfollowButton() {
	const userButtonFollowImg = $("#user-button-follow-image");
	userButtonFollowImg.attr("src", "../../resources/images/unfollow_1.png");
	const userButtonFollowText = $("#user-button-follow-text");
	userButtonFollowText.text("unfollow");
	$("#user-button-follow").unbind("click");
	$("#user-button-follow").click(function() {
		unfollowUserAjax();
	});
}

function attachLoginOnClick(element) {
	element.click(function() {
		showLoginForm();
	});
}

function showLoginForm() {
	$("#page-overlay").fadeIn(150);
	$("#form-login").fadeIn(150);
	setTimeout(attachClickOutsideLoginElementListener, 50);
}

function hideLoginForm() {
	$("#page-overlay").fadeOut(150);
	$("#form-login").fadeOut(150);
}

function setUpLoginForm() {
	$("#form-login").hover(function () {
		$("#form-login").addClass("hover");
	}, function() {
		$("#form-login").removeClass("hover");
	});
}

function attachClickOutsideLoginElementListener() {
	$("body").click(toggleLoginFormIfMouseDoesNotHover);
}

function toggleLoginFormIfMouseDoesNotHover() {
	const mouseHovers = $("#form-login").hasClass("hover");
		if(!mouseHovers) {
			hideLoginForm();
			$("body").off("click", toggleLoginFormIfMouseDoesNotHover);
		}
}

function isLoggedIn() {
	// users can get to those mappings only if they are authorised
	if(window.location.pathname === "/dashboard") {
		return true;
	}
	if(window.location.pathname === "/notifications") {
		return true;
	}
	return $("#data").attr("data-logged-in") === "true";
}

// data-follows should store whether or not you follow the current user (at /u/* mappings)
function doesFollow() {
	return $("#data").attr("data-follows") === "true";
}

function followUserAjax() {
	const loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	const follows = doesFollow();
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
			setFollow(true);
			setUpUserButtons();
		}
	});
}

function unfollowUserAjax() {
	const loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	const follows = doesFollow();
	if(!follows) {
		return;
	}
	const username = $("#data").attr("data-username");
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
			setFollow(false);
			setUpUserButtons();
		}
	});
}

function setFollow(bool) {
	$("#data").attr("data-follows", bool);
}

function hashtagOnLoad() {
	attachInifiteScrollingListenerHashtag();
	configureSubheaderButtons();
	setUpLoginForm();
	getHashtagFeed(parseInt($("#data").attr("data-first-timestamp")), true);
	pollNotifications();
}

function attachInifiteScrollingListenerHashtag() {
	$(window).scroll(function(ev) {
		const windowInnerHeight = window.innerHeight;
		const scrollY = window.scrollY;
		const bodyHeight = document.body.offsetHeight;
			if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
				getHashtagFeed(parseInt($("#data").attr("data-first-timestamp")), true);
			}
	});
}

function getHashtagFeed(timestamp, append) {
	const isGettingFeed = $("#data").attr("data-getting-feed");
	if(isGettingFeed == 1) {
		return;
	}
	setGettingFeed(1);
	const hashtag = $("#data").attr("data-hashtag");
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/feed/hashtag/" + hashtag,
		data: {
			"timestamp" : timestamp
		},
		error: function(xhr, status, error) {
			setGettingFeed(0);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			addKluchUserFeed(data, append);
			setGettingFeed(0);
		}
	});
}

// configures dashboard/messages/settings buttons. basically, hides them if the user is not logged in
function configureSubheaderButtons() {
	configureLogoutButton();
	const loggedIn = isLoggedIn();
	if(!loggedIn) {
		$("#header-button-dashboard").toggleClass("header-button-inactive");
		$("#header-button-notifications").toggleClass("header-button-inactive");
		$("#header-button-settings").toggleClass("header-button-inactive");
	}
}

function configureLogoutButton() {
	const loggedIn = isLoggedIn();
	if(loggedIn) {
		return;
	}
	const loginButton = $("#header-logout-button");

	const loginImage = loginButton.find(".header-button-image");
	loginImage.attr("src", "../../resources/images/login.png");

	const loginText = loginButton.find(".header-button-text");
	loginText.text("login");

	const loginLink = loginButton.find(".header-button-link");
	loginLink.attr("href", "#");
	loginButton.click(function() {
		showLoginForm();
	});
}

function displayAlert(text) {
	$("#alert-text").text(text);
	$("#alert-container").animate(
		{
			height: "135px"
		},
		150, attachHideAlert);
}

function attachHideAlert() {
	$("body").click(animateAlertUp);
}

function animateAlertUp() {
	removeAnimateAlertUp();
	$("#alert-container").animate(
		{
			height: "-135px"
		},
		150);
}

function removeAnimateAlertUp() {
	$("body").off("click", animateAlertUp);
}

function notificationsOnLoad() {
	pollNotifications();
	getNotifications();
}

function pollNotifications() {
	const loggedIn = isLoggedIn();
	if(!loggedIn) {
		return;
	}
	$.ajax({
		type: "GET",
		url: "/notification/poll/",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
			setTimeout(pollNotifications, 60 * 1000);
		},
		success: function(data, status, xhr) {
			setTimeout(pollNotifications, 60 * 1000);
			displayNewNotifications(data);
		}
	});
}

function displayNewNotifications(number) {
	const notificationsText = $("#header-button-notifications").find(".header-button-text");
	if(number <= 0) {
		notificationsText.text("notifications");
	} else {
		if(number > 999) {
			number = 999;
		}
		notificationsText.text("notifications (" + number + ")");
	}
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
	const kluchIds = [];
	const newFollowers = [];
	for(var i = 0; i < notifications.length; i++) {
		const notification = notifications[i];
		if(notification.kluchId !== undefined) {
			kluchIds.push(notification.kluchId);
		}
		if(notification.username !== undefined) {
			newFollowers.push({ username: notification.username, avatarPath: notification.avatarPath });
		}
	}
	getKluchs(kluchIds);
	displayNewFollowers(newFollowers);
	setTimeout(sendMarkNotificationsAsRead, 750);
}

function getKluchs(kluchIds) {
	if(kluchIds.length === 0) {
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
			addKluchUserFeed(data);			
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
	if(followers.length === 0) {
		return;
	}
	const users = [];
	for(let i = 0; i < followers.length; i++) {
		users.push(getUserLinkStyle(followers[i].username));
	}
	
	// constructs a message to display with all followers
	// who followed you recently. very messy. also, logic does not check out
	let message = "";
	const namesToDisplay = 3;
	if(users.length === 1) {
		message += users[0];
	}
	if(users.length === 2) {
		message += users[0] + " and " + users[1];
	}
	if(users.length > 2) {
		const remainingFollowers = users.length - namesToDisplay;
		if(remainingFollowers === 0) {
			message += users[0] + ", " + users[1] + " and " + users[2];
		}
		
		if(remainingFollowers > 0) {
			message += users[0] + ", " + users[1] + ", " + users[2];
			message += " and " + createRemainingFollowersElement(remainingFollowers) + " more";
		}
	}
	message += " followed you.";
	createEventListenersForRemainingFollowersList();
	populateRemainingFollowers(followers.slice(namesToDisplay));
	$("#followers-new-text").html(message);
}

function createRemainingFollowersElement(remainingFollowers) {
	return "<span id = 'followers-new-remaining-text' class = 'followers-new-remaining-text'>" + remainingFollowers + "</span>";
}

function createEventListenersForRemainingFollowersList() {
	$("body").on("mouseenter", "#followers-new-remaining-text", function(event) {
		showRemainingFollowersList(true);
	});

	$("body").on("click", "#followers-new-remaining-text", function(event) {
		$("#followers-new-free").fadeToggle('fast');
	});

	$("body").on("mousemove", function(event) {
		const boundingRect = $("#followers-new-free")[0].getBoundingClientRect();
		const distance = pointRectDist(event.pageX, event.pageY, boundingRect.left, boundingRect.top, boundingRect.width, boundingRect.height);
		const distanceToFadeOut = 25;
		if(distance > distanceToFadeOut) {
			showRemainingFollowersList(false);
		}
	});
}

// calculates a distance from any of the rectangles edges
function pointRectDist (px, py, rx, ry, rWidth, rHeight) {
    const cx = Math.max(Math.min(px, rx + rWidth ), rx);
    const cy = Math.max(Math.min(py, ry + rHeight), ry);
    return Math.sqrt((px-cx)*(px-cx) + (py-cy)*(py-cy));
}

function showRemainingFollowersList(bool) {

	const followersList = $("#followers-new-free");

	if(bool) {
		const position = $("#followers-new-remaining-text").position();
		followersList.css({
			"top": position.top + 18,
			"left": position.left + 10
		});
		followersList.fadeIn();
	} else {
		followersList.fadeOut();
	}
}

function populateRemainingFollowers(remainingFollowers) {

	const remainingFollowersList = $("#followers-new-free");
	remainingFollowersList.empty();

	for(var i = 0; i < remainingFollowers.length; i++) {

		const follower = remainingFollowers[i];

		const element = $(document.createElement("a"));
		element.addClass("followers-new-free-element");
		element.attr("href", "/u/" + follower.username);

		const avatarContainer = $(document.createElement("div"));
		avatarContainer.addClass("followers-new-free-element-image-container");

		const avatar = $(document.createElement("img"));
		avatar.addClass("followers-new-free-element-image");
		avatar.attr("src", "../../resources/images/" + follower.avatarPath);
		avatarContainer.append(avatar);
		element.append(avatarContainer);

		const text = $(document.createElement("span"));
		text.addClass("followers-new-free-element-text");
		text.text(follower.username);
		element.append(text);
		remainingFollowersList.append(element);
	}

}