<c:choose>
	<c:when test="${row.status == 'OK'}">
		<img src="static/img/status-green.png" alt="OK" title="OK"/>
	</c:when>
	<c:when test="${row.status == 'CHALLENGED'}">
		<img src="static/img/status-yellow.png" alt="Challenged" title="Challenged"/>
	</c:when>
	<c:when test="${row.status == 'CRITICAL'}">
		<img src="static/img/status-red.png" alt="Critical" title="Critical"/>
	</c:when>
</c:choose>
