<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-83967823-2', 'auto');
  ga('send', 'pageview');

</script>
</head>
<body>
<input type = "hidden" id = "data" data-username = "${username}" data-avatar-path = "${avatarPath}" data-created-date="${createdAt}">
<c:import url="header_with_buttons.jsp"></c:import>
<div class="page">
	<div class="dashboard">
		<div class="dashboard-section dashboard-section-user">
			<div class="user-info-container">
				<div class="user-avatar-container">
					<img class="user-avatar" src="../../resources/images/${avatarPath}"></img>
				</div>
				<div class="user-info-text">
					<p class="user-info-text-name">${username}</p>
					<p class="user-date-created" id="user-date-created"></p>
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
			<div class="kluch-feed" id="kluch-feed" data-next = "9007199254740991"
			 data-previous = "0" data-getting-feed="false" data-feed-finished="false">
				
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
<c:import url="footer.jsp"></c:import>
<script>
$(dashboardOnLoad());	
</script>
</body>
</html>