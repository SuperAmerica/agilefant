<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>
<c:forEach items="${storyHistory}" var="item">
${aef:dateTimeToFormattedString(item.revisionDate)} ${item.revision.userName} 
<c:choose>
    <c:when test="${item.revisionType == 'ADD'}">
    added story &quot;${item.object.name}&quot;
  </c:when>
    <c:when test="${item.revisionType == 'DEL'}">
    removed story &quot;${item.object.name}&quot;
  </c:when>
    <c:otherwise></c:otherwise>
  </c:choose>

  <hr />

</c:forEach>