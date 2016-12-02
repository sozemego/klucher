<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/main.css"></link>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<c:import url="../header.jsp"></c:import>
<div class="page">
	<div class="error-page-container">
		<div class = "error-status-code">${statusCode}</div>
		<div>${message}</div>
	</div>
</div>
<c:import url="../footer.jsp"></c:import>
</body>
</html>