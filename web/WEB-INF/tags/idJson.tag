<%@ include file="../jsp/inc/_taglibs.jsp"%>
<%@tag description="Creates JSON suitable for listing IDs of entities"%>
<%@attribute name="items" required="true" type="java.util.List"%>
<c:forEach items="${items}" var="item" varStatus="rowCounter">${item.id}<c:if test="${rowCounter.count < fn:length(items)}">,</c:if></c:forEach>