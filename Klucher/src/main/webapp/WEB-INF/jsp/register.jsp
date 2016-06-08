<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
	<form onsubmit="return validateRegister()" action="register" method="POST" modelAttribute="registerForm">
		<table class = "centered roundedCorners">
			<tr>
				<td class = "welcome" colspan = "2">welcome to Klucher</td>				
			</tr>
			<tr>
				<td>username</td>
				<td><input id = "username" name="username" type="text" size="50" maxlength="64" value = "${username}"></td>				
			</tr>
			<tr>
				<td>password</td>
				<td><input id = "password" name="password" type="text" size="50" maxlength="64 value = "${password}"></td>
			</tr>
			<tr>
				<td>
				<td align="right"><input type="submit" value="become a member"/></td>
			</tr>
			<c:if test="${not empty general}">
				<tr>
					<td colspan = "2" align = "center">${general}</td>
				</tr>
			</c:if>
			<c:if test="${not empty username_error}">
				<tr>
					<td colspan = "2" align = "center">${username_error}</td>
				</tr>
			</c:if>
			<c:if test="${not empty password_error}">
				<tr>
					<td colspan = "2" align = "center">${password_error}</td>
				</tr>
			</c:if>
		</table>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
</body>
</html>