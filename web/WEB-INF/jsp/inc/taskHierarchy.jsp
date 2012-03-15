<%@include file="./_taglibs.jsp" %>

<style>
.backlogHierarchy li, .backlogHierarchy li a {
  font-size: 100% !important;
}
</style>

<div class="hierarchyContainer">

<c:if test="${parentStory != null}">
<h2>Story hierarchy</h2>
<div class="storyTreeContainer bubbleHierarchyContainer">
  <div class="tree">
    <ul>
      <aef:storyTreeNode node="${parentStory}" forceOpen="true"/>
    </ul>
  </div>
</div>
</c:if>

<h2>Backlog hierarchy</h2>

<c:choose>
<c:when test="${task.iteration != null}">
  <c:set var="backlog" value="${task.iteration}"/>
</c:when>
<c:otherwise>
  <c:set var="backlog" value="${task.story.iteration}"/>
</c:otherwise>
</c:choose>

<ul class="backlogHierarchy">
<c:choose>
  <c:when test="${aef:isIteration(backlog)}">
  <c:if test="${backlog.parent.parent.name != null}">
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.parent.id}">
      <c:out value="${backlog.parent.parent.name}" />
    </a>
    </li>
  </c:if>
  <c:if test="${backlog.parent.name != null}">
    <li style="margin-left: 1em; list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="${backlog.parent.name}" />
    </a>
    </li> 
  </c:if>
    <li style="margin-left: 2em; list-style-image: url('static/img/hierarchy_arrow.png');">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="${backlog.name}" />
      </a>
    </li>
  </c:when>
  
  <c:when test="${aef:isProject(backlog)}">   
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="${backlog.parent.name}" />
    </a>
    </li> 
    
    <li style="margin-left: 1em; list-style-image: url('static/img/hierarchy_arrow.png');">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="${backlog.name}" />
      </a>
    </li>
  </c:when>
  
  <c:when test="${aef:isProduct(backlog)}">
    <li style="list-style-image: url('static/img/hierarchy_arrow.png');">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="${backlog.name}" />
      </a>
    </li>
  </c:when>
</c:choose>
</ul>

</div>