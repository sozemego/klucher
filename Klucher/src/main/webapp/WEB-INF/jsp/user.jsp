<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script
	src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<input type="hidden" id="data" data-username="${user.username}"
	data-page="0" data-last-timestamp="0"
	data-first-timestamp = "9007199254740991" data-logged-in="${loggedIn}" data-follows = "${follows}">
<div class="header">
	<div class="header-buttons-wrapper">
		<div class="header-buttons-left">
			<span class="header-button" id="header-button-dashboard">
				<a href="/dashboard" class="header-button-link">
					<div class="header-button-content">
						<img class="header-button-image" src="../../resources/images/dashboard_2.png"></img>
						<span class="header-button-text">dashboard</span>
					</div>
				</a>
			</span>
			<span class="header-button"  id="header-button-notifications">
				<a href="/notifications" class="header-button-link">
					<div class="header-button-content">
						<img class="header-button-image" src="../../resources/images/messages_5.png"></img>
						<span class="header-button-text">notifications</span>
					</div>
				</a>
			</span>
		</div>
		<div class="header-buttons-right">
			<span class="header-button"  id="header-button-settings">
				<a href="/" class="header-button-link">
					<div class="header-button-content">
						<img class="header-button-image" src="../../resources/images/settings.png"></img>
						<span class="header-button-text">settings</span>
					</div>
				</a>
			</span>
			<span class="header-button" id="header-logout-button">
				<a href="/logout" class="header-button-link">
					<div class="header-button-content">
						<img class="header-button-image" src="../../resources/images/logout.png"></img>
						<span class="header-button-text">logout</span>
					</div>
				</a>
			</span>
		</div>
	</div>
	<img class="logo logo-header" src="../../resources/images/logo_2.png" alt = "logo">
	<div class="header-bottom-underline"></div>
</div>
<div class="page">
	<div class="dashboard">
		<div class="dashboard-section dashboard-section-user">
			<div class="user-buttons-container">
				<div class="user-button" id="user-button-follow">
					<div class="user-button-image-container">
						<img id = "user-button-follow-image" class="user-button-image" src = "../../resources/images/follow_1.png"></img>
					</div>
					<span id = "user-button-follow-text" class="user-button-text">follow</span>
				</div>
				<div class="user-button" id="user-button-like">
					<div class="user-button-image-container">
						<img id = "user-button-like-image" class="user-button-image" src = "../../resources/images/follow_1.png"></img>
					</div>
					<span id = "user-button-like-text" class="user-button-text">like</span>
				</div>
				<div class="user-button" id="user-button-poke">
					<div class="user-button-image-container">
						<img id = "user-button-poke-image" class="user-button-image" src = "../../resources/images/follow_1.png"></img>				
					</div>
					<span id = "user-button-poke-text" class="user-button-text">poke</span>
				</div>
			</div>
			<div class="user-info-container">
				<div class="user-avatar-container">
					<img class="user-avatar" src="../../resources/images/${user.avatarPath}"></img>
				</div>
				<div class="user-info-text">
					<span class="user-info-text-name">${user.username}</span>
				</div>
			</div>
		</div>
		
		<div class="dashboard-section dashboard-section-feed">			
			<div class="dashboard-new-kluch-alert" id="new-kluch-alert">
				<span class="dashboard-new-kluch-alert-text">new kluchs available, click here to view!</span>
			</div>
			<div class="kluch-feed" id="kluch-feed">
				
			</div>
			<div class = "kluch-no-more">
				no more kluchs :(
			</div>
		</div>
		<div class = "dashboard-section">
			
		</div>
	</div>
</div>
<div id="page-overlay" class="page-overlay page-overlay-form page-overlay-inactive">
</div>
<div class="form form-overlay form-overlay-inactive" id="form-login">
	<form action="${pageContext.servletContext.contextPath}/login" method="post">
		<span class="form-welcome-text">Welcome back!</span>
		<div class="form-input-field-container">
			<span class="form-input-field-name">username</span>
			<input name="username" class="form-input-field" type="text">
		</div>
		<div class="form-input-field-container">
			<span class="form-input-field-name">password</span>
			<input name="password" class="form-input-field" type="password">
		</div>
		<div class="form-submit-container">
			<input type="checkbox" id="rememberme" name="remember-me">Remember me</input>
			<input type="submit" value="log in"></input>
		</div>
		<c:if test = "${not empty param.error}">
			<div class="form-errors">
				Invalid username or password!
			</div>
		</c:if>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}">
	</form>
</div>
<script>
$(userOnLoad());
</script>
</body>
</html>