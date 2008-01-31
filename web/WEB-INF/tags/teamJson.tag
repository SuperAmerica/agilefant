<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Creates JSON suitable for listing teams"%>
<%@attribute name="items" required="true" type="java.util.List"%>
<c:forEach items="${items}" var="item" varStatus="rowCounter">
	{name: "${item.name}", users: [<c:forEach items="${item.users}" var="user" varStatus="userCounter">${user.id}<c:if test="${userCounter.count < fn:length(item.users)}">,</c:if></c:forEach>]}
<c:if test="${rowCounter.count < fn:length(items)}">,</c:if></c:forEach>