<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="userInfoContainer">
	<table class = "userInfoTable">
		<tr>
			<td class = "verticalAlignTop">
				<div id = "userButtonContainer" class="hidden">
					<table id = "followButton" class = "followButton dottedLightBlueBorder">
						<tr>
							<td>
								<img id = "followImage" class = "followImage">
							</td>
							<td>
								<span id = "followText" class = "followText"></span>
							</td>
						</tr>
					</table>
				</div>
			</td>
			<td>
				<div class="profilePictureContainer dottedLightBlueBorder">
					<img class="profilePicture"
						src="../../resources/images/${user.avatarPath}"></img>
				</div>
				<div class="userInfoText">
					<span class="userInfoText">${user.username}</span>
				</div>
			</td>
		</tr>
	</table>


</div>