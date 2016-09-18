<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
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
		<c:if test = "${not empty param.error}">
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