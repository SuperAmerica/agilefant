<%@include file="./_taglibs.jsp" %>
<div class="hierarchyContainer">

<h2>Story hierarchy</h2>

<aef:storyTreeNode node="${story}" />

<h2>Backlog hierarchy</h2>

<c:set var="backlog" value="${story.backlog}"/>

<c:choose>
  <c:when test="${aef:isIteration(backlog)}">
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.parent.id}">
      <c:out value="${backlog.parent.parent.name}" />
    </a>
    </li>
    
    <li style="margin-left: 1em; list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="${backlog.parent.name}" />
    </a>
    </li> 
    
    <li style="margin-left: 2em; list-style-image: url('static/img/hierarchy_arrow.png');">
      <c:out value="${backlog.name}" />
    </li>
  </c:when>
  
  <c:when test="${aef:isProject(backlog)}">   
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="${backlog.parent.name}" />
    </a>
    </li> 
    
    <li style="margin-left: 1em; list-style-image: url('static/img/hierarchy_arrow.png');">
      <c:out value="${backlog.name}" />
    </li>
  </c:when>
  
  <c:when test="${aef:isProduct(backlog)}">
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
      <c:out value="${backlog.name}" />
    </li>
  </c:when>
</c:choose>