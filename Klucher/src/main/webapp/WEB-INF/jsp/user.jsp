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
<div class = "bottomBorder">
<c:import url="header.jsp"></c:import>
</div>
<div class = "content">
<div class = "newKluch centerText" id = "newKluch"></div>
<div class = "kluchContainer">
<div class = "kluchFeed" id = "kluchFeed">
<c:forEach items = "${feed.kluchs}" var = "kluch">
<div class = "kluch">
<div class = "author">${kluch.author} ${kluch.timestamp}</div>
<div class = "kluchTextArea opacityAnimation">${kluch.text}</div>
</div>
</c:forEach>
</div>
<div id = "lastPage" class = "lastPage hidden">No more Kluchs to load :(</div>
</div>
</div>
<script>
$(userOnLoad());
</script>	
</body>
</html>