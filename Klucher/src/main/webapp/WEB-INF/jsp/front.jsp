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
	<c:import url = "registerElement.jsp"></c:import>
</div>
</div>			
</body>
</html>