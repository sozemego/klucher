<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
</head>
<body>
	<form action="login" method="post">
		<table class = "centered roundedCorners">
			<tr>
				<td class = "welcome" colspan = "2">welcome back!</td>
			</tr>
			<tr>
				<td>username</td>
				<td><input name="username" class = "myInput" type="text"
					size="50"></td>
			</tr>
			<tr>
				<td>password</td>
				<td><input name="password" class = "myInput" type="password" size="50"></td>
			</tr>
			<tr>
				<td colspan= "2" align="right">
				<input type="checkbox" id="rememberme"
					name="remember-me"> Remember me
				<input type="submit" value="log in"/></td>
			</tr>
			<c:if test="${not empty error}">
			<tr>
				<td class = "error" colspan = "2" align="center">${error}</td>
			</tr>
			</c:if>
		</table>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
</body>
</html>