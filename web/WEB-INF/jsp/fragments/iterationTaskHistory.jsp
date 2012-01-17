<%@ include file="/WEB-INF/jsp/inc/_taglibs.jsp"%>
<c:forEach items="${taskHistory}" var="item">
${aef:dateTimeToFormattedString(item.revisionDate)} ${item.revision.userName} 
<c:choose>
    <c:when test="${item.revisionType == 'ADD'}">
    added task &quot;${item.object.name}&quot;
  </c:when>
    <c:when test="${item.revisionType == 'DEL'}">
    removed task &quot;${item.object.name}&quot;
  </c:when>
    <c:otherwise>
    	modified task &quot;${item.object.name}&quot;
    </c:otherwise>
  
</c:choose>
  
<c:choose>
  <c:when test="${empty item.object.story}">
    (task without story)
  </c:when>
  <c:otherwise>
  	in story &quot;${item.object.story.name}&quot;
  </c:otherwise>
</c:choose>
 
  <hr />

</c:forEach>