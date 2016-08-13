<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<c:import url="header.jsp"></c:import>
	<form id = "registerForm" action="register" method="POST" modelAttribute="registerForm">
		<table class = "centerViewport roundedCorners" id = "registerTable">
			<tr>
				<td class = "welcome centerText" colspan = "2">welcome to Klucher</td>				
			</tr>
			<tr>
				<td>username</td>
				<td><input id = "username" class = "available myInput" name="username" type="text" size="50" maxlength="64" value = "${username}"></td>				
			</tr>
			<tr>
				<td>password</td>
				<td><input id = "password" class = "myInput" name="password" type="password" size="50" maxlength="64 value = "${password}"></td>
			</tr>
			<tr>
				<td>
				<td align="right"><input type="submit" value="become a member"/></td>
			</tr>
			<tr>
				<td colspan = "2" align = "center">
				<table id = "errorTable">	
			<c:if test="${not empty general}">
				<tr>
					<td align = "center">${general}</td>
				</tr>
			</c:if>
			<c:if test="${not empty username_error}">
				<tr>
					<td align = "center">${username_error}</td>
				</tr>
			</c:if>
			<c:if test="${not empty password_error}">
				<tr>
					<td align = "center">${password_error}</td>
				</tr>
			</c:if>			
			</table>		
				</td>
			</tr>		
		</table>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
<script>
$(document).ready(registerOnLoad());
</script>
</body>
</html>