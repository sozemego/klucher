<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css" />"></link>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
<script src="/resources/js/dev.js"></script>
</head>
<body>
Welcome to dev console!

<form action = "post" id = "post" method = "POST">
<table>
	<tr>
		<td>Username</td>
		<td><input type = "text" id = "username" name = "username"></td>
	</tr>
	<tr>
		<td>Number</td>
		<td><input type = "text" id = "number" name = "number"></td>
	</tr>
	<tr>
		<td>Millis</td>
		<td><input type = "text" id = "millis" name = "millis"></td>
	</tr>
	<tr>
		<td>FastMode</td>
		<td><input type = "checkbox" id = "fastMode" name = "fastMode"></td>
	</tr>
	<tr>
		<td>Mode</td>
		<td><select name = "mode" id = "mode">
			<option>id</option>
			<option>timestamp</option>
			<option>random</option>
		</select></td>
	</tr>
	<tr>
		<td colspan = "2">
			<input type = "submit">
		</td>
	</tr>
	
</table>
</form>

${error}	
<script>
$(onDevLoad());
</script>	
</body>
</html>