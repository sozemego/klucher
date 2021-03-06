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

	if(!isVisible()) {
		setTimeout(checkAvailability, 10000, currentUsername);
		return;
	}

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
	displayUserSocialStats();
	processProfileDescription();
	getOpenChatRooms(displayOpenChatRooms);
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
		"likes": 0,
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
	if(isGettingFeed()) {
		return;
	}
	let next = null;
	let previous = null;
	if(direction === "before") {
		if(isFeedFinished()) {
			return;
		}
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
	// if there are no more elements to append, we are at the end (oldest part) of feed
	if(feedElements.length === 0 && append) {
		setFeedFinished(true);
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
	kluchAvatar.attr("title", "Kenney http://kenney.nl/")
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
			const username = getLoggedUsername();
			if(username === user.username) {
				const deleteButton = $(document.createElement("div"));
				deleteButton.addClass("kluch-footer-icon-container kluch-footer-icon-delete kluch-footer-icon-delete-inactive");
				const deleteImg = $(document.createElement("img"));
				deleteImg.addClass("kluch-footer-icon");
				deleteImg.attr("src", "../../resources/images/delete_2.png");
				deleteImg.attr("title", "Freepik http://www.freepik.com");

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
				kluchContainer.hover(function() {
					$(this).find(".kluch-footer-icon-delete").removeClass("kluch-footer-icon-delete-inactive");
				}, function() {
					$(this).find(".kluch-footer-icon-delete").addClass("kluch-footer-icon-delete-inactive");
				});
		}
	}

	const likeButton = $(document.createElement("div"));
	likeButton.addClass("kluch-footer-icon-container");
	const likeImg = $(document.createElement("img"));
	likeImg.attr("title", "Kenney http://kenney.nl/");
	likeImg.addClass("kluch-footer-icon");
	likeButton.append(likeImg);

	const likes = kluchElement.likes;
	const likesNumber = $(document.createElement("span"));
	likesNumber.addClass("kluch-footer-text");
	likesNumber.text(likes === 0 ? "" : likes);
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
	link = link.trim();
	const containsHttp = doesLinkContainHttps(link);
	if(!containsHttp) {
		link = "http://" + link;
	}
	return "<a class = 'kluch-text-link-link' href = '" + link + "'>" + link + "</a>";
}

function doesLinkContainHttps(link) {
	const regex = /^(http|https)/;
	return regex.test(link);
}

function getUsername() {
	const username = $("#data").attr("data-username");
	if(username === null || username === undefined || username === "") {
		return getLoggedUsername();
	}
	return username;
}

function getLoggedUsername() {
	return $("#data").attr("data-logged-username");
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
	let currentLikesText = parseInt(likesElement.text());
	if(isNaN(currentLikesText)) {
		currentLikesText = 0;
	}
	let finalText = currentLikesText + change;
	if(finalText === 0) {
		finalText = "";
	}
	likesElement.text(finalText);
}

// sets values for feed pagination. next/previous id. next id is the earliest kluch id ()
function setFeedIds(feed) {
		setNextId(feed.next);
		setPreviousId(feed.previous);
}

function setNextId(next) {
	$("#kluch-feed").attr("data-next", next);
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
	$("#kluch-feed").attr("data-feed-finished", bool);
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
	if(isGettingFeed() || !isVisible()) {
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
	attachNewKluchElementListener();
	pollFeed();
	pollNotifications();
	setUpUserButtons();
	setUpLoginForm();
	configureSubheaderButtons();
	displayTimeCreated();
	displayUserSocialStats();
	processProfileDescription();
	getOpenChatRooms(displayOpenChatRooms);
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
	attachUserLikeButtonListener();
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
	userButtonFollowImg.attr("title", "Freepik http://www.freepik.com");
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
	userButtonFollowImg.attr("title", "Freepik http://www.freepik.com");
	const userButtonFollowText = $("#user-button-follow-text");
	userButtonFollowText.text("unfollow");
	$("#user-button-follow").unbind("click");
	$("#user-button-follow").click(function() {
		unfollowUserAjax();
	});
}

function attachUserLikeButtonListener() {
	const likeButton = $("#user-button-like");
	const likeButtonText = likeButton.find("#user-button-like-text");
	const likes = doesLikeUser();
	const likeButtonImg = likeButton.find("#user-button-like-image");
	likeButtonImg.attr("src", likes ? "../../resources/images/like_2_active.png" : "../../resources/images/like_2.png");
	likeButtonText.text(likes ? "unlike" : "like");
	const username = getUsername();
	likeButton.off();
	likeButton.click(function() {
		ajaxLikeUnlikeUser(username, likes);
	});
}

function ajaxLikeUnlikeUser(username, likes) {
	const path = likes ? "unlike" : "like";
	$.ajax({
		type: "POST",
		url: "/u/" + path,
		data: {
			"username" : username
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			setLikesUser(!likes);
			attachUserLikeButtonListener();
		}
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

function doesLikeUser() {
	return $("#data").attr("data-likes") === "true";
}

function setLikesUser(likes) {
	$("#data").attr("data-likes", likes);
}

function getUserNumberOfLikes() {
	return parseInt($("#data").attr("data-likes-number"));
}

function getUserNumberOfFollowers() {
	return parseInt($("#data").attr("data-followers-number"));
}

function getUserNumberOfKluchs() {
	return parseInt($("#data").attr("data-kluchs-number"));
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

function displayUserVarious() {
	displayUserSocialStats();
}

function displayUserSocialStats() {
	const userLikes = getUserNumberOfLikes();
	const userFollowers = getUserNumberOfFollowers();
	const userKluchs = getUserNumberOfKluchs();
	let text = "";
	if(userLikes > 0) {
		text += "liked by: " + userLikes;
	}
	if(userFollowers > 0) {
		text += " followers: " + userFollowers;
	}
	if(userKluchs > 0) {
		text += " kluchs: " + userKluchs;
	}
	$(".user-info-text-social-stats").text(text);
}

function processProfileDescription() {
	const profileDescriptionElement = $("#user-info-profile-description");
	const profileDescriptionText = profileDescriptionElement.text();
	const processedDescriptionText = processKluchText(profileDescriptionText);
	profileDescriptionElement.html(processedDescriptionText);
}


function hashtagOnLoad() {
	attachInifiteScrollingListenerHashtag();
	configureSubheaderButtons();
	setUpLoginForm();
	getHashtagFeed(parseInt($("#kluch-feed").attr("data-next")), true);
	pollNotifications();
	getOpenChatRooms(displayOpenChatRooms);
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
	getNewLikes();
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
	if(!isVisible()) {
		setTimeout(pollNotifications, 60 * 1000);
		return;
	}
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

function handleNewLikes(feed) {
	displayNewLikes(feed.elements, feed.totalElements);
}

function getNewLikes(id) {
	const username = getUsername();
	$.ajax({
		type: "GET",
		url: "/u/likes/" + username,
		data: {
			"id": id
		},
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			handleNewLikes(data);
		}
	});
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

function displayNewLikes(likes, total) {
	if(likes.length === 0) {
		return;
	}
	const users = [];
	for(let i = 0; i < likes.length; i++) {
		//TODO fix this, i dont have to get a style for all of them?
		users.push(getUserLinkStyle(likes[i].username));
	}
	
	// constructs a message to display with all likes
	// who liked you recently.
	let message = "";
	const namesToDisplay = 3;
	if(users.length === 1) {
		message += users[0];
	}
	if(users.length === 2) {
		message += users[0] + " and " + users[1];
	}
	if(users.length > 2) {
		const remainingLikes = total - namesToDisplay;
		if(remainingLikes === 0) {
			message += users[0] + ", " + users[1] + " and " + users[2];
		}
		
		if(remainingLikes > 0) {
			message += users[0] + ", " + users[1] + ", " + users[2];
			message += " and " + createRemainingLikesElement(remainingLikes) + " more";
		}
	}
	message += " liked you.";
	createEventListenersForRemainingLikesList();
	populateRemainingLikes(likes.slice(namesToDisplay), total, namesToDisplay);
	$("#likes-new-text").html(message);
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

function createRemainingLikesElement(likes) {
	return "<span id = 'likes-new-remaining-text' class = 'likes-new-remaining-text'>" + likes + "</span>";
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

function createEventListenersForRemainingLikesList() {
	$("body").on("mouseenter", "#likes-new-remaining-text", function(event) {
		showRemainingLikesList(true);
	});

	$("body").on("click", "#likes-new-remaining-text", function(event) {
		$("#likes-new-free").fadeToggle('fast');
	});

	$("body").on("mousemove", function(event) {
		const boundingRect = $("#likes-new-free")[0].getBoundingClientRect();
		const distance = pointRectDist(event.pageX, event.pageY, boundingRect.left, boundingRect.top, boundingRect.width, boundingRect.height);
		const distanceToFadeOut = 25;
		if(distance > distanceToFadeOut) {
			showRemainingLikesList(false);
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

function showRemainingLikesList(bool) {

	const likesList = $("#likes-new-free");

	if(bool) {
		const position = $("#likes-new-remaining-text").position();
		likesList.css({
			"top": position.top + 18,
			"left": position.left + 10
		});
		likesList.fadeIn();
	} else {
		likesList.fadeOut();
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
		avatar.attr("title", "Kenney http://kenney.nl/");
		avatarContainer.append(avatar);
		element.append(avatarContainer);

		const text = $(document.createElement("span"));
		text.addClass("followers-new-free-element-text");
		text.text(follower.username);
		element.append(text);

		remainingFollowersList.append(element);
	}

}

function populateRemainingLikes(remainingLikes, total, listedAlready) {

	const remainingLikesList = $("#likes-new-free");
	remainingLikesList.empty();
	const maxTableLength = 10;
	for(var i = 0; i < remainingLikes.length; i++) {

		if(i === maxTableLength) {

			const element = $(document.createElement("a"));
			element.addClass("likes-new-free-element likes-new-free-element-remaining-text");
			element.attr("href", "/#");

			const remainingText = $(document.createElement("span"));
			remainingText.text("and " + (total - listedAlready - maxTableLength) + " more...");
			
			element.append(remainingText);
			remainingLikesList.append(element);
			break;

		}

		const like = remainingLikes[i];

		const element = $(document.createElement("a"));
		element.addClass("likes-new-free-element");
		element.attr("href", "/u/profile/" + like.username);

		const avatarContainer = $(document.createElement("div"));
		avatarContainer.addClass("likes-new-free-element-image-container");

		const avatar = $(document.createElement("img"));
		avatar.addClass("likes-new-free-element-image");
		avatar.attr("src", "../../resources/images/" + like.avatarPath);
		avatar.attr("title", "Kenney http://kenney.nl/");
		avatarContainer.append(avatar);
		element.append(avatarContainer);

		const text = $(document.createElement("span"));
		text.addClass("likes-new-free-element-text");
		text.text(like.username);
		element.append(text);

		remainingLikesList.append(element);
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

function settingsOnLoad() {
	pollNotifications();
	attachSaveSettingsListener();
	configureDeleteAccountButton();
}

function attachSaveSettingsListener() {
	const saveSettingsButton = $("#settings-save-button");
	saveSettingsButton.click(function() {
		const valid = validateSettings();
		if(valid) {
			saveSettings();
		}
	});
}

function validateSettings() {
	const kluchsPerRequestInputElement = $("#settings-input-kluchs-per-request");
	const kluchsPerRequestValue = parseInt(kluchsPerRequestInputElement.val());
	if(isNaN(kluchsPerRequestValue) || kluchsPerRequestValue < 10 || kluchsPerRequestValue > 120) {
		$("#settings-kluchs-per-request-text").addClass("settings-error");
		return false;
	}

	const profileDescriptionElement = $("#settings-input-profile-description");
	const profileDescriptionValue = profileDescriptionElement.val();
	if(profileDescriptionValue.length > 140) {
		$("#settings-profile-description-text").adClass("settings-error");
		return false;
	}

	return true;
}

function saveSettings() {
	const userSettings = gatherSettings();
	const stringifiedSettings = JSON.stringify(userSettings);
	$.ajax({
		type: "POST",
		data: stringifiedSettings,
		dataType: "json",
		headers: {
      "Content-Type": "application/json"
		},
		url: "/settings",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			
		}
	});
}

function gatherSettings() {
	const profileDescription = $("#settings-input-profile-description").val();
	const kluchsPerRequest = parseInt($("#settings-input-kluchs-per-request").val());
	return {
		"avatarPath": "",
		"kluchsPerRequest": kluchsPerRequest,
		"profileDescription": profileDescription
	};
}

function configureDeleteAccountButton() {
	const deleteButtonElement = $("#settings-delete-button");
	deleteButtonElement.click(function() {
		const deleteButtonConfirmElement = $("#settings-delete-button-yes");
		deleteButtonConfirmElement.fadeToggle();
	});
	const deleteButtonConfirmElement = $("#settings-delete-button-yes");
	deleteButtonConfirmElement.click(function() {
		$(this).fadeOut(2500, function() {
			deleteAccount();
		});
	});
}

function deleteAccount() {
	$.ajax({
		url: "/settings/delete",
		type: "DELETE",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			window.location = "/";
		}
	});
}

var stompClient = null;

function chatStart() {
	const hashtag = getHashtag();
	if(hashtag === null || hashtag === undefined || hashtag === "") {
		getOpenChatRooms(sendOpenRoomsAsChatMessage);
		return;
	}
	var socket = new SockJS("/chat-socket");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log("Connected: " + frame);
		stompClient.subscribe("/chat/back/" + getHashtag(), function(message) {
			handleMessage(message);
		});
		requestUserList();
	});
	attachKeyListeners();
}

function handleMessage(message) {
	const parsedMessage = JSON.parse(message.body);

	if(parsedMessage.type === "CHAT_MESSAGE") {
		displayMessage(parsedMessage);
	}

	if(parsedMessage.type === "USER_COUNT") {
		updateUserCount(parsedMessage.userCount);
	}

	if(parsedMessage.type === "ADD_USER") {
		addUserToChatList(parsedMessage.username);
	}

	if(parsedMessage.type === "REMOVE_USER") {
		removeUserFromChatList(parsedMessage.username);
	}

	if(parsedMessage.type === "USER_LIST") {
		populateChatUserList(parsedMessage.users);
	}
}

function displayMessage(parsedMessage) {

	const time = getTimeOfMessage(parsedMessage.timestamp);
	const username = parsedMessage.username;
	const messageText = processKluchText(parsedMessage.message);

	const chatMessageContainer = $(document.createElement("div"));
	chatMessageContainer.addClass("chat-message-container");

	const timestampElement = $(document.createElement("span"));
	timestampElement.addClass("chat-message-timestamp");
	timestampElement.text(time);

	const usernameElement = $(document.createElement("span"));
	usernameElement.addClass("chat-message-username");
	usernameElement.text(username + ":");

	const messageTextElement = $(document.createElement("span"));
	messageTextElement.addClass("chat-message-text");
	messageTextElement.html(messageText);

	chatMessageContainer.append([timestampElement, usernameElement, messageTextElement]);

	const chatMessagesContainer = $("#chat-messages");
	chatMessagesContainer.append(chatMessageContainer);
	chatMessagesContainer[0].scrollTop = chatMessagesContainer[0].scrollHeight;
}

function getHashtag() {
	return $("#data").attr("data-hashtag");
}

function hideChat() {
	$("#chat-container").addClass("chat-inactive");
}

function getTimeOfMessage(timestamp) {
	const date = new Date(timestamp);
	const mm = date.getMonth() + 1 + "";
	const dd = "" + date.getDate();
	const ddLength = dd.length;
	const day = ("00" + dd).substring(dd.length);
	const month = ("00" + mm).substring(mm.length);
	const dateString = [day, month, date.getFullYear()].join(".");
	const hours = "" + date.getHours();
	const hoursString = ("00" + hours).substring(hours.length);
	const minutes = "" + date.getMinutes();
	const minutesString = ("00" + minutes).substring(minutes.length);
	const seconds = "" + date.getSeconds();
	const secondsString = ("00" + seconds).substring(seconds.length);
	const timeString = [hoursString, minutesString, secondsString].join(":");
	const returnString = [dateString, timeString].join(" ");
	return returnString;
}

function attachKeyListeners() {
	const textbox = $("#chat-input-textbox");
	textbox.keydown(function(event) {
		if(event.which === 13) {
			event.preventDefault();
			validateAndSendMessage();
			textbox.val('');
			textbox.focus();
		}
	});

	const sendButton = $("#chat-input-send-button");
	sendButton.click(function() {
		validateAndSendMessage();
		textbox.val('');
		textbox.focus();
	});
}

function validateAndSendMessage() {
	const messageValue = $("#chat-input-textbox").val();
	if(messageValue === undefined || messageValue === null || messageValue === "") {
		return;
	}
	sendMessage(messageValue.trim().substring(0, 140));
}

function sendMessage(text) {
	stompClient.send("/chat/in/" + getHashtag(), {}, JSON.stringify({"type": "CHAT_MESSAGE", "content": text}));
}

function updateUserCount(userCount) {
	const hashtagNameElement = $("#hashtag-name-title");
	hashtagNameElement.text("#" + getHashtag() + " (" + userCount + " user(s))");
}

function requestUserList() {
	stompClient.send("/chat/in/" + getHashtag(), {}, JSON.stringify({"type" : "USER_LIST_REQUEST", "content": ""}));
}

function addUserToChatList(username) {
	const chatList = $("#chat-user-list");

	removeUserFromChatList(username);
	const userElement = $(document.createElement("div"));
	userElement.addClass("chat-user-element");
	userElement.text(username);
	userElement.attr("data-username", username);

	chatList.append(userElement);

}

function removeUserFromChatList(username) {
	const chatList = $("#chat-user-list");
	const userElements = chatList.children();
	for(var i = 0; i < userElements.length; i++) {
		const name = $(userElements[i]).attr("data-username");
		if(name === username) {
			userElements[i].remove();
		}
	}
}

function populateChatUserList(users) {
	$("#chat-user-list").empty();
	users.sort(function(a, b) {
		if(a < b) return -1;
		if(a > b) return 1;
		return 0;
	});
	for(var i = 0; i < users.length; i++) {
		addUserToChatList(users[i]);
	}
}

function getChatMessageFromSystem(message) {
	return {
		"timestamp" : new Date().getTime(),
		"username" : "System",
		"message": message
	};
}

function getOpenChatRooms(callback) {
	$.ajax({
		url: "/chats/trending",
		type: "GET",
		error: function(xhr, status, error) {
			displayAlert(xhr.responseJSON.message);
		},
		success: function(data, status, xhr) {
			console.log(data);
			callback(data.chatRoomCounts);
		}
	});
}

function displayOpenChatRooms(chatRoomCounts) {

	const chatListElement = $("#chat-trending-list");

	if(chatRoomCounts.length > 0) {
		const welcomeElement = $(document.createElement("div"));
		const welcomeElementText = $(document.createElement("span"));
		welcomeElementText.text("Chat rooms:");
		welcomeElement.append(welcomeElementText);
		chatListElement.append(welcomeElement);
	}

	for(var i = 0; i < chatRoomCounts.length; i++) {
		const roomName = chatRoomCounts[i].roomName;
		const userCount = chatRoomCounts[i].userCount;

		const chatRoomElement = $(document.createElement("div"));

		const roomNameElement = $(document.createElement("a"));
		roomNameElement.text("#" + roomName);
		roomNameElement.addClass("chat-trending-list-roomname");
		roomNameElement.attr("href", "/chat/" + roomName);
		const userCountElement = $(document.createElement("span"));
		userCountElement.text(" (" + userCount + " users online)");
		chatRoomElement.append(roomNameElement);
		chatRoomElement.append(userCountElement);

		chatListElement.append(chatRoomElement);

	}

}

function sendOpenRoomsAsChatMessage(chatRoomCounts) {
	displayMessage(getChatMessageFromSystem("This room is not open."));
	displayMessage(getChatMessageFromSystem("You can follow these hashtags instead:"));
	for(var i = 0; i < chatRoomCounts.length; i++) {
		const roomName = chatRoomCounts[i].roomName;
		displayMessage(getChatMessageFromSystem("#" + roomName));
	}
}

function getChatRoomLink(roomName) {
	return '<a href = "/chat/' + roomName + '">'+roomName+'</a>';
}

function isVisible() {
	if(document.visibilityState === "visible") {
		return true;
	}
	return false;
}