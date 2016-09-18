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
</script>
</body>
</html>