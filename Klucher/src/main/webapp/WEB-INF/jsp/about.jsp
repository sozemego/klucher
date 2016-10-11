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
	as a way to learn Spring (and get some front-end experience). All of the code is open-sourced
	and if you want to dive right in, here's a link: <a class = "about-link" href = "https://bitbucket.org/sozemego/klucher">https://bitbucket.org/sozemego/klucher</a></p>
	<p class = "about-element">In this project I've used the following languages/technologies/frameworks/libraries/buzzwords: Java, Spring Boot, JPA, Hibernate, MySQL, Mockito, git,	Gradle, Amazon Web Services, Tomcat, JSP, Flyway, HTML, CSS, JavaScript, jQuery. Here's a short explanation of what most of them do:</p>
	<p class = "about-element">Spring Boot - klucher's heart. Provides dependency injection and MVC capabilities. Abstracts a lot of things and is mostly configured out of the box. It still lets you change any aspect of your app, if you desire. </p>
	<p class = "about-element">JPA, Hibernate, MySQL - persistent data storage (MySQL) and ways to generate queries (JPA), map DB results to Java objects (Hibernate). </p>
	<p class = "about-element">Mockito - an excellent library used for testing. Provides an easy way to create mocked objects, methods. Allows for easy verification of
	called methods and their arguments.</p>
	<p class = "about-element">git - version control provided by bitbucket.</p>
	<p class = "about-element">Tomcat, JSP - a web container for the app. Jasper is the engine rendering JavaServer Pages (JSP). </p>
	<p class = "about-element">Gradle - a build tool. Among many things, it manages dependencies for you, builds your app, runs tests and many more. I have not used this tool to maximum of its capabilities. </p>
	<p class = "about-element">Amazon Web Services - This website runs on an Elastic Beanstalk, which is a service provided by AWS. It lets you auto-scale (spin up new instances of this app), manages load-balancing, monitors your instances health etc. Deploying a new app is easy and quite fast. Databases are using amazon's RDS, DNS is managed through their Route 53 service. </p>
	<p class = "about-element">Flyway - database versioning system. Easy to use, makes sure all your databases (production, staging, dev etc) have the same schema.</p>
	<p class = "about-element">HTML, CSS, JavaScript, jQuery - fundamental tools (except for jQuery) for building the front-facing part of your site. </p>

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