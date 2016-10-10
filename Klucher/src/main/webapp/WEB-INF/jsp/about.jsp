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
	and if you want to dive right in, here's a link:<a class = "about-link" href = "https://bitbucket.org/sozemego/klucher">https://bitbucket.org/sozemego/klucher</a></p>
	<p class = "about-element">In this project I've used the following languages/technologies/frameworks/libraries: Java, Spring Boot, JPA, Hibernate, MySQL, Mockito, git,	Gradle, Amazon Web Services, Tomcat, Flyway, HTML, CSS, JavaScript, jQuery, JSP. Here's a short explanation of what most of them do:</p>
	<p class = "about-element">Spring Boot - klucher's heart. Provides dependency injection and MVC capabilities. Allows access to JPA and Hibernate technologies, has support for Flyway and renders JSP content (through either embedded or provided Tomcat). </p>
	<p class = "about-element">JPA, Hibernate, MySQL - persistent data storage (MySQL) and ways to generate queries (JPA), map DB results to Java objects (Hibernate). </p>
	<p class = "about-element">Mockito - excellent library used for testing. Provides an easy way to create mocked objects, methods. Allows for easy verification of
	called methods and their arguments.</p>
	<p class = "about-element">git - version control. Bitbucket is the service used to provide git.</p>
	<p class = "about-element">Tomcat, Jasper, JSP - a web container for the app. Jasper is the engine rendering JavaServer Pages (JSP). </p>
	<p class = "about-element">Gradle - a build and dependency tool. Among many things, it manages dependencies for you, builds your app, runs tests and many more.</p>
	<p class = "about-element">Amazon Web Services - This website runs on an Elastic Beanstalk, which is a service provided by AWS. It provides an Elastic Load Balancer, monitors your app's health and provides auto-scaling. A database is provided by another AWS service, RDS. DNS is provided by Route 53.</p>
	<p class = "about-element">Flyway - database versioning system. Easy to use, makes sure all your databases (production, staging, dev etc) follow the same schema.</p>
	<p class = "about-element">HTML, CSS, JavaScript, jQuery - fundamental tools (except for jQuery) for building a front-facing part of your site. </p>

</div>
<c:import url="footer.jsp"></c:import>
<script>
$(configureSubheaderButtons());
</script>
</body>
</html>