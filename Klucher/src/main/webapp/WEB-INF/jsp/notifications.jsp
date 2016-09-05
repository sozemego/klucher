<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.js"></script>
<script src="/resources/js/klucher.js"></script>
</head>
<body>
<input type = "hidden" id = "data" data-username = "${username}" data-page = "0" data-last-timestamp="0" data-first-timestamp = "9007199254740991">

<c:import url="header.jsp"></c:import>
<c:import url="subheader.jsp"></c:import>
<c:import url="alertElement.jsp"></c:import>
<div class="content">
		<table width = "100%">
			<tr>
				<td class = "verticalAlignTop" width = "33%"></td>
				<td width = "33%">
					<div class = "newFollowers" id = "newFollowers">
					<span class = "newFollowersText"
						 id = 'newFollowersText'></span>
					
					</div>					
					<c:import url="feed.jsp"></c:import>
				</td>
				<td width = "33%"></td>
			</tr>
		</table>

</div>
<script>
$(notificationsOnLoad());
</script>	
</body>
</html>