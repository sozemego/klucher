<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<div class = "headerLogo headerBackground">
<img class = "headerImage" src = "../../resources/images/logo-darker-small.png">
</div>
Welcome, ${user.username}.
<form method = "POST" action = "kluch">
<div class = "dashboardInputContainer">
	<div class = "boxContainer">
		
		<textarea class = "dashboardTextInput" id = "composeKluch" placeholder = "Don't be shy, tell us what you're thinking."  name = "text"></textarea>
			<div class = "underInput">
				<span id = "charactersLeft">250</span>
				<input type="submit" value="share"/>
			
			</div>
	</div>
	
</div>
<div class = "kluchFeed">

</div>
</form>
		
<script>
$(dashboardOnLoad());
</script>	
</body>
</html>