<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<c:import url = "loginElement.jsp"></c:import>
	<input type="hidden" id="data" data-username="${user.username}"
		data-page="0" data-last-timestamp="0" data-first-timestamp = "9007199254740991" data-logged-in="${loggedIn}" data-follows = "${follows}">
		<c:import url="header.jsp"></c:import>
		<c:import url="subheader.jsp"></c:import>
	<div class="content">
		<table width = "100%">
			<tr>
				<td class = "verticalAlignTop" width = "33%"><c:import url = "userinfo.jsp"></c:import></td>
				<td width = "33%">
					<div class="newKluch centerText" id="newKluch"></div>
					<c:import url="feed.jsp"></c:import>
				</td>
				<td width = "33%"></td>
			</tr>
		</table>

	</div>
<script>
$(userOnLoad());
</script>
</body>
</html>