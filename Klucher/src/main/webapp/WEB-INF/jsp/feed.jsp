<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class = "kluchContainer">
<div class = "kluchFeed" id = "kluchFeed">
<c:forEach items = "${feed.kluchs}" var = "kluch">
<div class = "kluch">
<div class = "author">${kluch.author} ${kluch.timestamp}</div>
<div class = "kluchTextArea opacityAnimation">${kluch.text}</div>
</div>
</c:forEach>
</div>
<div id = "lastPage" class = "lastPage hidden">No more Kluchs to load :(</div>
</div>