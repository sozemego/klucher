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
<input type = "hidden" id = "data" data-username = "${username}"
 data-avatar-path = "${avatarPath}" data-created-date="${createdAt}" data-logged-username="${loggedUsername}">
<c:import url="header_with_buttons.jsp"></c:import>
<div class = "page">
	<div class = "settings-container">
		<p class = "settings-header">Settings</p>
		<div class = "settings-item">
			<span class = "settings-text">Kluchs per request (between 10 and 120):</span>
			<input type="number" class="settings-input-number" max="120" min="10" id = "settings-input-kluchs-per-request">
		</div>
		<div class = "settings-item settings-item-profile-description">
			<span class = "settings-text">Profile description (maximum 140 characters):</span>
			<textarea maxlength="140" class="settings-item-profile-description-element"></textarea>
		</div>
	</div>
</div>
<c:import url="footer.jsp"></c:import>
<script>
$(settingsOnLoad());
</script>
</body>
</html>