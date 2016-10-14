/*jshint esversion: 6 */

function registerOnLoad() {
	checkAvailability(null);
	validateRegisterForm();
	attachValidationListeners();
	attachSubmitRegisterListener();
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
	displayTimeCreated();
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
	$.ajax({
		type: "POST",
		url: "/kluch",
		data: {"kluchText" : kluchText },
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(kluch, status, xhr) {
			clearTextArea();
			addKluchToFeed(getKluchElement(kluch), false);
			checkCharacterCount();
			setPreviousId(kluch.id);
		}
	});
	focusInputArea();
}

function getKluchElement(kluch) {
	return {
		"kluch": kluch,
		"liked": false,
		"user": getUser()
	};
}

function getUser() {
	const username = getUsername();
	const avatarPath = getAvatarPath();
	return {
		"username": username,
		"avatarPath": avatarPath
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

function getFeed(direction, id, append) {
	if(isGettingFeed() || isFeedFinished()) {
		return;
	}
	let next = null;
	let previous = null;
	if(direction === "before") {
		next = id;
	} else if (direction === "after") {
		previous = id;
	}
	setGettingFeed(true);
	const username = getUsername();

	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/kluch/" + username,
		data: {
			"next" : next,
			"previous" : previous
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
			setGettingFeed(false);
		},
		success: function(feed, status, xhr) {
			addKluchUserFeed(feed, append);
			setGettingFeed(false);
		}
	});
}

function addKluchUserFeed(feed, append) {
	const feedElements = feed.elements;
	for(let i = 0; i < feedElements.length; i++) {
		addKluchToFeed(feedElements[i], append);
	}
	setFeedIds(feed);
}

function addKluchToFeed(kluchElement, append) {

	const kluch = kluchElement.kluch;
	const user = kluchElement.user;

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
	kluchAuthor.attr("href", "/u/profile/" + user.username);
	kluchAuthor.text(user.username);
	

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

	const loggedIn = isLoggedIn();
	if(loggedIn) {
			const username = getUsername();
			if(username === user.username) {
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

	const likeButton = $(document.createElement("div"));
	likeButton.addClass("kluch-footer-icon-container");
	const likeImg = $(document.createElement("img"));
	likeImg.addClass("kluch-footer-icon");
	likeButton.append(likeImg);

	const likes = kluchElement.likes;
	const likesNumber = $(document.createElement("span"));
	likesNumber.addClass("kluch-footer-text");
	likesNumber.text(likes == 0 ? "" : likes);
	likeButton.append(likesNumber);

	const liked = kluchElement.liked;
	configureLikeImg(likeImg, liked);

	if(loggedIn) {
		attachLikeListener(likeButton, liked, kluch.id);
	} else {
		likeButton.click(function() {
			showLoginForm();
		});
	}

	kluchFooter.append(likeButton);

	kluchBody.append(kluchContent);
	kluchBody.append(kluchFooter);

	kluchContainer.append(kluchBody);

	if(append) {
		$("#kluch-feed").append(kluchContainer);
	} else {
		$("#kluch-feed").prepend(kluchContainer);
	}
	
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
	return "<a class = 'kluch-text-user-link' href = '/u/profile/" + userWithoutAt(user) + "'>" + user + "</a>";
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

function attachLikeListener(button, liked, id) {
	button.off();
	if(!liked) {
		button.click(function() {
			ajaxLike(id);
			configureLikeImg(button.find(".kluch-footer-icon"), true);
			attachLikeListener(button, true, id);
			changeLikeCounter(button, 1);
		});
	} else {
		button.click(function() {
			ajaxUnlike(id);
			configureLikeImg(button.find(".kluch-footer-icon"), false);
			attachLikeListener(button, false, id);
			changeLikeCounter(button, -1);
		});
	}
}

function ajaxLike(kluchId) {
	$.ajax({
		type: "POST",
		url: "/kluch/like/",
		data: {
			"kluchId" : kluchId
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			
		}
	});
}

function ajaxUnlike(kluchId) {
	$.ajax({
		type: "POST",
		url: "/kluch/unlike/",
		data: {
			"kluchId" : kluchId
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			
		}
	});
}

function configureLikeImg(imgElement, liked) {
	imgElement.off();
	if(liked) {
			imgElement.attr("src", "../../resources/images/like_2_active.png");
			imgElement.hover(function() {
				$(this).attr("src", "../../resources/images/like_2.png");
			}, function() {
				$(this).attr("src", "../../resources/images/like_2_active.png");
			});
		} else {
			imgElement.attr("src", "../../resources/images/like_2.png");
			imgElement.hover(function() {
				$(this).attr("src", "../../resources/images/like_2_active.png");
			}, function() {
				$(this).attr("src", "../../resources/images/like_2.png");
			});
		}
}

function changeLikeCounter(button, change) {
	const likesElement = button.find(".kluch-footer-text");
	const currentLikesText = parseInt(likesElement.text());
	likesElement.text(currentLikesText + change);
}

// sets values for feed pagination. next/previous id. next id is the earliest kluch id ()
function setFeedIds(feed) {
		setNextId(feed.next);
		setPreviousId(feed.previous);
}

function setNextId(next) {
	$("#kluch-feed").attr("data-next", next);
	if(next === null || next === undefined) {
		setFeedFinished(true);
	}
}

function setPreviousId(previous) {
	$("#kluch-feed").attr("data-previous", previous);
}

function attachInfiniteScrollingListener() {
	$(window).scroll(function(ev) {
		const windowInnerHeight = window.innerHeight;
		const scrollY = window.scrollY;
		const bodyHeight = document.body.offsetHeight;
		if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
			getFeed("before", parseInt($("#kluch-feed").attr("data-next")), true);
		}
		displayLastPageMessage();
	});
}

function setFeedFinished(bool) {
	$("#kluch-feed").attr("data-feed-finished", true);
}

function isFeedFinished() {
	const feedFinished = $("#kluch-feed").attr("data-feed-finished");
	if(feedFinished === "true") {
		return true;
	}
	return false;
}

function displayLastPageMessage() {
	const feedFinished = isFeedFinished();
	if(feedFinished) {
		$(".kluch-no-more").addClass("kluch-no-more-visible");
	}
}

function setGettingFeed(data) {
	$("#kluch-feed").attr("data-getting-feed", data);
}

function isGettingFeed() {
	const gettingFeed = $("#kluch-feed").attr("data-getting-feed");
	if(gettingFeed === "true") {
		return true;
	}
	return false;
}

function pollFeed() {
	if(isGettingFeed()) {
		setTimeout(pollFeed, 30000);
		return;
	}

	const previous = $("#kluch-feed").attr("data-previous");
	const username = getUsername();
	if(username === undefined || username === null || username === "") {
		return;
	}

	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/kluch/poll/" + username,
		data: {
			"previous" : previous
		},
		error: function(xhr, status, error) {
			setTimeout(pollFeed, 30000);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			setTimeout(pollFeed, 30000);
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
	setFeedFinished(false);
	getFeed("after", parseInt($("#kluch-feed").attr("data-previous")), false);
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
	displayTimeCreated();
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

// data-follows should store whether or not you follow the current user
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
	getHashtagFeed(parseInt($("#kluch-feed").attr("data-next")), true);
	pollNotifications();
}

function attachInifiteScrollingListenerHashtag() {
	$(window).scroll(function(ev) {
		const windowInnerHeight = window.innerHeight;
		const scrollY = window.scrollY;
		const bodyHeight = document.body.offsetHeight;
			if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
				getHashtagFeed(parseInt($("#kluch-feed").attr("data-next")), true);
			}
	});
}

function getHashtagFeed(id, append) {
	if(isGettingFeed()) {
		return;
	}
	setGettingFeed(true);
	const hashtag = $("#data").attr("data-hashtag");
	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/kluch/hashtag/" + hashtag,
		data: {
			"next" : id
		},
		error: function(xhr, status, error) {
			setGettingFeed(false);
			displayAlert(xhr.responseJSON.message);
		},
		success: function(feed, status, xhr) {
			addKluchUserFeed(feed, append);
			setGettingFeed(false);
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
	getNewFollowers();
	getKluchsWithMentions(Number.MAX_SAFE_INTEGER);
	attachInifiteScrollingListenerMentions();
	markNotificationsAsRead();
}

function attachInifiteScrollingListenerMentions() {
	$(window).scroll(function(ev) {
		const windowInnerHeight = window.innerHeight;
		const scrollY = window.scrollY;
		const bodyHeight = document.body.offsetHeight;
			if ((windowInnerHeight + scrollY) >= bodyHeight * 0.9) {
				getKluchsWithMentions(parseInt($("#kluch-feed").attr("data-next")), true);
			}
	});
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

function getNewFollowers(id) {
	const username = getUsername();
	$.ajax({
		type: "GET",
		url: "/u/followers/" + username,
		data: {
			"id": id
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			handleNewFollowers(data);
		}
	});
}

function handleNewFollowers(feed) {
	displayNewFollowers(feed.elements, feed.totalElements);
}

function getKluchsWithMentions(id) {
	if(isGettingFeed() || isFeedFinished()) {
		return;
	}
	setGettingFeed(true);

	$.ajax({
		dataType: "json",
		type: "GET",
		url: "/kluch/mentions",
		data: {
			"next" : id
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(feed, status, xhr) {
			setGettingFeed(false);
			addKluchUserFeed(feed, true);
		}
	});
}

function markNotificationsAsRead() {
	const isVisible = isWindowVisible();
	if(isVisible) {
		sendMarkNotificationsAsRead();
	} else {
		setTimeout(2500, markNotificationsAsRead);
	}
}

function isWindowVisible() {
	//TODO actually check if window is visible
	return true;
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

function displayNewFollowers(followers, total) {
	if(followers.length === 0) {
		return;
	}
	const users = [];
	for(let i = 0; i < followers.length; i++) {
		//TODO fix this, i dont have to get a style for all of them?
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
		const remainingFollowers = total - namesToDisplay;
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
	populateRemainingFollowers(followers.slice(namesToDisplay), total, namesToDisplay);
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

function populateRemainingFollowers(remainingFollowers, total, listedAlready) {

	const remainingFollowersList = $("#followers-new-free");
	remainingFollowersList.empty();
	const maxTableLength = 10;
	for(var i = 0; i < remainingFollowers.length; i++) {

		if(i === maxTableLength) {

			const element = $(document.createElement("a"));
			element.addClass("followers-new-free-element followers-new-free-element-remaining-text");
			element.attr("href", "/#");

			const remainingText = $(document.createElement("span"));
			remainingText.text("and " + (total - listedAlready - maxTableLength) + " more...");
			
			element.append(remainingText);
			remainingFollowersList.append(element);
			break;

		}

		const follower = remainingFollowers[i];

		const element = $(document.createElement("a"));
		element.addClass("followers-new-free-element");
		element.attr("href", "/u/profile/" + follower.username);

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

function displayTimeCreated() {
	const timestamp = $("#data").attr("data-created-date");
	const convertedTimestamp = convertTimestampToMonthYear(timestamp);
	const userDateCreated = $("#user-date-created");
	userDateCreated.text("joined " + convertedTimestamp);
}

function convertTimestampToMonthYear(timestamp) {
	const date = new Date(timestamp);
	const month = date.getMonth() + 1;
	const year = date.getFullYear();
	return month + "." + year;
}