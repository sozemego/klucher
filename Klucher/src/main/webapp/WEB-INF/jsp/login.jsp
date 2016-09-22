<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
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
<div class="form form-alone-wrapper">
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
		<div class="form-register-redirect">
			<a class="form-register-redirect-link" href="/register">Don't have an account yet? Register for free!</a>
		</div>
		<c:if test = "${param.error != null}">
			<div class="form-errors">
				Invalid username or password!
			</div>
		</c:if>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}">
	</form>
</div>
</body>
</html>