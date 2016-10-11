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
<c:import url="header.jsp"></c:import>
<div class="page">
	<div class="front-side front-side-left">
		<div class="front-side-introduction">
			<div class="front-side-introduction-element">
				<img class="front-introduction-image" src="../../resources/images/write-post-image-front_color.png">
				<div class="front-introduction-text-wrapper">
					<span class="front-introduction-text">Publish your thoughts (up to 250 characters).
				</div>		
			</div>
			<div class="front-side-introduction-element front-side-introduction-element-middle">
				<img class="front-introduction-image" src="../../resources/images/followers_color.png">
				<div class="front-introduction-text-wrapper">
					<span class="front-introduction-text">Follow your favourite users and receive a feed of their thoughts.
				</div>
			</div>
			<div class="front-side-introduction-element">
				<img class="front-introduction-image" src="../../resources/images/chat_color.png">
				<div class="front-introduction-text-wrapper">
					<span class="front-introduction-text">Join the discussion! Most popular #topics will automatically open chat rooms.
				</div>
			</div>
		</div>
	</div>
	<div class="front-side front-side-right">
		<div class="form front-form">
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
				<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">
			</form>
		</div>
		<div class = "form front-form">
			<form action="${pageContext.servletContext.contextPath}/register" id="register-form" method="post" data-last-username-checked="" data-last-username-available="">
				<span class="form-welcome-text">New? Register here</span>
				<div class="form-input-field-container">
					<span class="form-input-field-name">username</span>
					<input name="username" class="form-input-field form-input-field-valid" id="register-form-username" type="text">
				</div>
				<div class="form-input-field-container">
					<span class="form-input-field-name">password</span>
					<input name="password" class="form-input-field form-input-field-valid" id="register-form-password" type="password">
				</div>
				<div class="form-submit-container">
					<input type="submit" value="sign up"></input>
				</div>
				<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}">
			</form>
			<div class="form-errors" id="register-availability-errors">
				
			</div>
			<div class="form-errors" id="register-errors">
				
			</div>
		</div>
	</div>
</div>
<c:import url="footer.jsp"></c:import>
<script>
	$(registerOnLoad());
</script>
</body>
</html>