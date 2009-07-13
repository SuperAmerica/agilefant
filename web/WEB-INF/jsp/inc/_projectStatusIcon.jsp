<c:choose>
	<c:when test="${row.status == 'GREEN'}">
		<img src="static/img/status-green.png" alt="Green" title="Green"/>
	</c:when>
	<c:when test="${row.status == 'YELLOW'}">
		<img src="static/img/status-yellow.png" alt="Yellow" title="Yellow"/>
	</c:when>
	<c:when test="${row.status == 'RED'}">
		<img src="static/img/status-red.png" alt="Red" title="Red"/>
	</c:when>
	<c:when test="${row.status == 'GREY'}">
		<img src="static/img/status-grey.png" alt="Grey" title="Grey"/>
	</c:when>
	<c:when test="${row.status == 'BLACK'}">
		<img src="static/img/status-black.png" alt="Black" title="Black"/>
	</c:when>
</c:choose>