<%@ include file="./_taglibs.jsp"%>
<c:choose>
	<c:when test="${badArgumentFound == false}">
		<b><c:out value="${aef:minutesToString(hourSum)}" /></b>
	</c:when>
	<c:otherwise>
		<b style="color: red;">Error</b>
	</c:otherwise>
</c:choose>
