<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<input type = "hidden" id = "data" data-username = "${username}" data-page = "0" data-last-timestamp="0" data-first-timestamp = "9007199254740991">
<c:import url="header_with_buttons.jsp"></c:import>
<div class="page">
	<div class="dashboard">
		<div class="dashboard-section dashboard-section-user">
			<div class="user-info-container">
				
			</div>
		</div>
		
		<div class="dashboard-section dashboard-section-feed">
			<div class="followers-new-container" id="followers-new-container">
				<span class="followers-new-text" id="followers-new-text">

				</span>
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
<div class="followers-new-free" id="followers-new-free">
	
</div>
<script>
$(notificationsOnLoad());
</script>	
</body>
</html>