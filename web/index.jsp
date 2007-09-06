<%@ include file="./WEB-INF/jsp/inc/_taglibs.jsp" %>

<c:choose>
	<c:when test="${!empty currentContext}">
		<% response.sendRedirect("./contextView.action?contextName=" +
				session.getAttribute("currentContext") + "&contextObjectId=" + 
				session.getAttribute("currentPageId") + "&resetContextView=true"); %>
	</c:when>
	<c:otherwise>
	<% response.sendRedirect("./selectBacklog.action"); %>
	</c:otherwise>
</c:choose>