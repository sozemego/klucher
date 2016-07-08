<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<input type = "hidden" id = "data" data-username = "${user.username}" data-page = "0" data-last-timestamp="9007199254740991" data-first-timestamp = "0">
<div class = "headerLogo paddedHeader headerBackground">
<img class = "headerImage" src = "../../resources/images/logo_2.png">
</div>
Welcome, ${user.username}.
<form method = "POST" action = "kluch" id = "kluchForm">
<div class = "dashboardInputContainer">
	<div class = "boxContainer">
		
		<textarea class = "dashboardTextInput" id = "kluchTextArea" placeholder = "Don't be shy, tell us what you're thinking."  name = "kluch"></textarea>
			<div class = "underInput">
				<span id = "charactersLeft">250</span>
				<input type="submit" value="share"/>
			
			</div>
	</div>
	
</div>
</form>
<div class = "newKluch centerText" id = "newKluch"></div>
<div class = "kluchFeed" id = "kluchFeed">
<c:forEach items = "${feed.kluchs}" var = "kluch">
<div class = "kluch">
<div class = "author">${kluch.author} ${kluch.timestamp}</div>
<div class = "kluchTextArea opacityAnimation">${kluch.text}</div>
</div>
</c:forEach>
</div>

		
<script>
$(dashboardOnLoad());
</script>	
</body>
</html>