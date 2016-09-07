<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="userInfoContainer">
	<table>
		<tr>
			<td class = "verticalAlignTop">
				<div id = "userButtonContainer" class="hidden">
					<table id = "followButton" class = "followButton">
						<tr>
							<td>
								<img id = "followImage" class = followImage>
							</td>
							<td>
								<span id = "followText" class = "followText"></span>
							</td>
						</tr>
					</table>
				</div>
			</td>
			<td>
				<div class="profilePictureContainer">
					<img class="profilePicture"
						src="../../resources/images/blue_profile.png"></img>
				</div>
				<div class="userInfoText">
					<span class="userInfoText">${username}</span>
				</div>
			</td>
		</tr>
	</table>


</div>