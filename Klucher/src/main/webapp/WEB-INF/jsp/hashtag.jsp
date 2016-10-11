<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
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
<input type="hidden" id="data" data-username="${username}" data-logged-in="${loggedIn}" data-hashtag="${hashtag}">
<c:import url="header_with_buttons.jsp"></c:import>
<div class="page">
	<div class="hashtag-name-title-container">
		<span class="hashtag-name-title">
			#${hashtag}
		</span>
	</div>
	<div class="dashboard">
		<div class="dashboard-section dashboard-section-user">

		</div>
		
		<div class="dashboard-section dashboard-section-feed">
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
<c:import url="form_login.jsp"></c:import>
<c:import url="footer.jsp"></c:import>
<script>
$(hashtagOnLoad());
</script>
</body>
</html>