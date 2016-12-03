<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
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
<input type = "hidden" id = "data" data-logged-in="${loggedIn}">
<c:import url="header_with_buttons.jsp"></c:import>
<div class = "about">
	<p class = "about-element">Hi! My name is Kamil Jurek and I'm a self-thought developer. I built this Twitter-like service
	as a way to learn Spring (and get some basic front-end experience). All of the code is open-sourced
	and if you want to dive right in, here's a link: <a class = "about-link" href = "https://bitbucket.org/sozemego/klucher">https://bitbucket.org/sozemego/klucher</a></p>
	<p class = "about-element">In this project I've used the following languages/technologies/frameworks/libraries/buzzwords: Java, Spring Boot, JPA, Hibernate, MySQL, Mockito, git,	Gradle, Amazon Web Services, Tomcat, JSP, Flyway, HTML, CSS, JavaScript, jQuery.
	<p class = "about-element">Most graphical assets are not made by me, however I only selected those assets which are royalty free. Where possible, attribution is displayed when an image is hovered over. Below is a list of all attributions:</p>
	<p class = "about-element">Freepik <a href = "http://www.freepik.com">http://www.freepik.com</a></p>
	<p class = "about-element">Dario Ferrando <a href = "http://dario.io/">http://dario.io/</a></p>
	<p class = "about-element">Kenney <a href = "http://kenney.nl/">http://kenney.nl/</a></p>
	<p class = "about-element">Madebyoliver <a href = "http://www.flaticon.com/authors/madebyoliver">http://www.flaticon.com/authors/madebyoliver</a></p>
</div>
<div id="page-overlay" class="page-overlay page-overlay-form page-overlay-inactive">
</div>
<div class="form form-overlay form-overlay-inactive" id="form-login">
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
		<c:if test = "${not empty param.error}">
			<div class="form-errors">
				Invalid username or password!
			</div>
		</c:if>
		<input type="hidden" name="${_csrf.parameterName}"
			value="${_csrf.token}">
	</form>
</div>
<c:import url="footer.jsp"></c:import>
<script>
$(configureSubheaderButtons());
</script>
</body>
</html>