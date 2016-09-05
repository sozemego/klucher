<div class = "subheaderContainer">
<table class = "subheaderTable">
	<tr>
		<td width = "25%" align = "right">
			<a href = "/dashboard" class = "verticalCenter subheaderButton" id = "dashboardButton">
				<img class = "logoutButtonImg" src = "../../resources/images/dashboard_2.png">dashboard
			</a>
		</td>
		<td width = "25%">
			<a href = "/notifications" class = "verticalCenter subheaderButton" id = "notificationsButton">
				<img class = "logoutButtonImg" src = "../../resources/images/messages_5.png">
				<span id = "notificationsText">notifications</span>
			</a>
		</td>
		<td width = "25%" align = "right">
			<div class = "verticalCenter subheaderButton" id = "settingsButton">
				<img class = "logoutButtonImg" src = "../../resources/images/settings.png">settings
			</div>
		</td>
		<td width = "25%">
			<a href="/logout" class = "verticalCenter subheaderButton" id = "logoutButton">
				<img class = "logoutButtonImg" src = "../../resources/images/logout.png"><span class = "buttonText">logout</span>
			</a>				
		</td>
	</tr>
</table>
</div>	
<script>
$(subheaderOnLoad());
</script>
