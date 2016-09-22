<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<c:import url="header.jsp"></c:import>
	<div class = "form form-alone-wrapper">
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
<script>
$(registerOnLoad());
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

	ga('create', 'UA-83967823-2', 'auto');
	ga('send', 'pageview');
</script>
</body>
</html>