<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class = "form">		
		<form action="register" method="POST" modelAttribute="registerForm" id = "registerForm">
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
			</table>		
				</td>
			</tr>		
		</table>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}" />
	</form>
	</div>
	<script>
		$(registerOnLoad());
	</script>