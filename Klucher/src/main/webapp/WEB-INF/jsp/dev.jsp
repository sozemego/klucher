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

<form action = "dev/post" id = "post" method = "POST">
<table>
	<tr>
		<td>Username</td>
		<td><input type = "text" name = "username"></td>
	</tr>
	<tr>
		<td>Number</td>
		<td><input type = "text" name = "number"></td>
	</tr>
	<tr>
		<td>Millis</td>
		<td><input type = "text" name = "millis"></td>
	</tr>
	<tr>
		<td>FastMode</td>
		<td><input type = "checkbox" name = "fastMode"></td>
	</tr>
	<tr>
		<td>Mode</td>
		<td><select name = "mode">
			<option value = "id">id</option>
			<option value = "timestamp">timestamp</option>
			<option value = "random text">random text</option>
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

</script>	
</body>
</html>