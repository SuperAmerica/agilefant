<%@include file="./_taglibs.jsp" %>
<div class="hierarchyContainer">

<h2>Story hierarchy</h2>

<%--
Link to backlog:

<a href="editBacklog.action?backlogId=${story.backlog.id}">
</a>
 --%>

<ul>
<c:set var="indent" value="0"/>
<c:set var="itemNumber" value="${fn:length(hierarchy)}" />
<c:set var="count" value="0" />

<c:forEach items="${hierarchy}" var="item">
  <c:set var="count" value="${count + 1}" />
  <c:choose>
  <c:when test="${story == item}">
    <c:set var="cssClass" value="currentItem" />
  </c:when>
  <c:otherwise>
    <c:set var="cssClass" value="" />  
  </c:otherwise>
  </c:choose>
    
  <li style="margin-left: ${indent}px;" class="${cssClass}"><c:out value="${item.name}" /></li>
  
  
  <c:set var="indent" value="${indent + 10}"/>
  
  <c:if test="${count == itemNumber}">
    <c:forEach items="${item.children}" var="child">
      <li style="margin-left: ${indent}px;"><c:out value="${item.name}" /></li>        
    </c:forEach>
  </c:if>
  
</c:forEach>
</ul>


</div>