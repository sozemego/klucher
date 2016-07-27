<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<form action="${pageContext.servletContext.contextPath}/login" method="post">
	<table class="centerViewport roundedCorners aboveOverlay" id="loginTable">
		<tr>
			<td class="welcome centerText" colspan="2">welcome back!</td>
		</tr>
		<tr>
			<td>username</td>
			<td><input name="username" class="myInput" type="text" size="50"></td>
		</tr>
		<tr>
			<td>password</td>
			<td><input name="password" class="myInput" type="password"
				size="50"></td>
		</tr>
		<tr>
			<td colspan="2" align="right"><input type="checkbox"
				id="rememberme" name="remember-me"> Remember me <input
				type="submit" value="log in" /></td>
		</tr>
		<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
			<tr>
				<td class="error" colspan="2" align="center">Wrong username or
					password!</td>
			</tr>
		</c:if>
		<tr>
			<td class="registerText" colspan="2" align="right"><span>Don't
					have an account yet? <a href="/register">Register for free</a>
			</span></td>
		</tr>
	</table>
	<input type="hidden" name="${_csrf.parameterName}"
		value="${_csrf.token}" />
</form>
