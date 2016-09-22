<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<input type = "hidden" id = "data" data-username = "${username}" data-avatar-path = "${avatarPath}" data-page = "0" data-last-timestamp="0" data-first-timestamp = "9007199254740991">
<c:import url="header_with_buttons.jsp"></c:import>
<div class="page">
	<div class="dashboard">
		<div class="dashboard-section dashboard-section-user">
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
			<div class="kluch-input">
				<textarea class="kluch-input-text-content" id="kluch-text" 
					placeholder="Don't be shy, tell us what you're thinking" name="kluch"></textarea>
				<div class="kluch-input-controls">
					<span class="kluch-input-controls-text" id="kluch-characters-remaining">250</span>
					<span class="kluch-input-controls-text kluch-input-controls-share" id="kluch-share-button">share</span>
				</div>
			</div>
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
<div class="alert-container" id="alert-container">
	<div class="alert-text-wrapper">
		<span class="alert-text" id="alert-text"></span>
	</div>
</div>
<script>
$(dashboardOnLoad());
</script>	
</body>
</html>