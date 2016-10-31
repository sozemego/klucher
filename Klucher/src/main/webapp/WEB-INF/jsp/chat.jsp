<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <link rel="stylesheet" href="/resources/css/main.css"></link>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
    <script src="/resources/js/klucher.js"></script>
    <script src="//cdn.jsdelivr.net/sockjs/1/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
</head>
<body>
<input type = "hidden" id = "data" data-hashtag = "${hashtag}"></input>
<c:import url="header.jsp"></c:import>
<div class = "page">
	<div class="hashtag-name-title-divider">
		<div class="hashtag-name-title-container">
			<span class="hashtag-name-title" id = "hashtag-name-title">
				#${hashtag}
			</span>
		</div>
	</div>
	<div class = "chat-container" id = "chat-container">
		<div class = "chat-messages-wrapper">
			<div class = "chat-messages" id = "chat-messages">
					
			</div>
			<div class = "chat-input-container">
				<textarea class = "chat-input-textbox" id = "chat-input-textbox"
					placeholder="type your message here, 140 characters maximum"
					maxlength="140" cols="140"></textarea>
				<span class="chat-send-button" id = "chat-input-send-button">send</span>
			</div>
		</div>
		<div class = "chat-user-list-container">
			<div class = "chat-user-list" id = "chat-user-list">
				
			</div>
		</div>
	</div>
</div>
<script>
$(chatStart());
</script>
</body>
</html>