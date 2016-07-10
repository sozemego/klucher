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
<div class = "container content">
<div class = "leftSide">
	<div class = "frontItem verticalCenter">
		<span class = "nearImage">Publish your thoughts (up to 250 characters).</span>
	<img src = "../../resources/images/write-post-image-front_color.png" width = "96" height = "96">
	</div>
	<div class = "frontItem verticalCenter">
		<span class = "nearImage">Follow your favourite users and receive a feed of their thoughts.</span>
	<img src = "../../resources/images/followers_color.png" width = "96" height = "96">
	</div>
	<div class = "frontItem verticalCenter">
		<span class = "nearImage">Join the discussion! Most popular #topics will automatically open chat rooms.</span>
	<img src = "../../resources/images/chat_color.png" width = "96" height = "96">
	</div>
</div>
<div class = "rightSide">
	<div class = "form">	
		<form action="login" method="post">
		<table class = "roundedCorners frontForm">
			<tr>
				<td class = "welcome centerText" colspan = "2">Already have an account?</td>
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
	</div>
	<div class = "form">		
		<form action="register" method="POST" modelAttribute="registerForm">
		<table class = "roundedCorners frontForm" id = "registerTable">
			<tr>
				<td class = "welcome centerText" colspan = "2">New to Klucher?</td>				
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
	</div>
</div>
</div>		
<script>
$(document).ready(registerOnLoad());
</script>	
</body>
</html>