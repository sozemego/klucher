<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script
	src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
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
<input type="hidden" id="data" data-username="${username}" data-logged-username="${loggedUsername}" data-logged-in="${loggedIn}" 	data-follows = "${follows}"
	data-created-date="${createdAt}" data-likes="${likes}"
	data-likes-number="${numberOfLikes}" data-followers-number="${numberOfFollowers}" data-kluchs-number="${kluchs}">
<c:import url="header_with_buttons.jsp"></c:import>
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
						<img id = "user-button-like-image" class="user-button-image" src = "../../resources/images/like_2.png"></img>
					</div>
					<span id = "user-button-like-text" class="user-button-text">like</span>
				</div>
			</div>
			<div class="user-info-container">
				<div class="user-avatar-container">
					<img class="user-avatar" src="../../resources/images/${avatarPath}"></img>
				</div>
				<div class="user-info-text" id = "user-info-text">
					<p class="user-info-text-name">${username}</p>
					<p class="user-info-text-social-stats"></p>
					<p class="user-date-created" id="user-date-created"></p>
				</div>
			</div>
		</div>
		
		<div class="dashboard-section dashboard-section-feed">
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
<c:import url="form_login.jsp"></c:import>
<c:import url="footer.jsp"></c:import>
<script>
$(userOnLoad());
</script>
</body>
</html>