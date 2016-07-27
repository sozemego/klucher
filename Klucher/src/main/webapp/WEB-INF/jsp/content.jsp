<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class = "content">
<form method = "POST" action = "kluch" id = "kluchForm">
<div class = "dashboardInputContainer">
	<div class = "boxContainer">
		
		<textarea class = "dashboardTextInput" id = "kluchTextArea" placeholder = "Don't be shy, tell us what you're thinking."  name = "kluch"></textarea>
			<div class = "underInput">
				<span class = "charactersLeft centerText" id = "charactersLeft">250</span>
				<span class = "submitButton centerText" id = "submitButton">share</span>
			</div>
	</div>
	
</div>
</form>
<div class = "newKluch centerText" id = "newKluch"></div>
<c:import url="feed.jsp"></c:import>
</div>