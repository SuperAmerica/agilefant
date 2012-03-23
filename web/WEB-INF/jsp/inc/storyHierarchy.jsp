<%@include file="./_taglibs.jsp" %>
<link rel="stylesheet" href="static/css/dailywork.css" type="text/css"/>

<style>
.backlogHierarchy li, .backlogHierarchy li a {
  font-size: 100% !important;
}
</style>


<div class="hierarchyContainer">

<h2>Story hierarchy</h2>

<div class="storyTreeContainer bubbleHierarchyContainer">
  <div class="tree">
    <ul>
      <aef:storyTreeNode node="${topmostStory}" displayLinksToStories="true" forceOpen="true"/>
    </ul>
  </div>
</div>

<h2>Backlog hierarchy</h2>

<c:choose>
<c:when test="${story.iteration != null}">
  <c:set var="backlog" value="${story.iteration}"/>
  <c:set var="storybacklog" value="${story.backlog}"/>
</c:when>
<c:otherwise>
  <c:set var="backlog" value="${story.backlog}"/>
</c:otherwise>
</c:choose>

<ul class="backlogHierarchy">
<c:choose>
    <c:when test="${aef:isStandaloneIteration(story.iteration)}">
    <c:choose>
    	<%-- Show Product-> Project -> Standalone Iteration --%>
	    <c:when test="${aef:isProject(storybacklog)}">
		    <li class = "backlogHierarchy-product">
		    <a href="editBacklog.action?backlogId=${storybacklog.parent.id}">
		      <c:out value="Product: ${storybacklog.parent.name}" />
		    </a>
		    </li>
		    
		    <li class = "backlogHierarchy-project">
		    <a href="editBacklog.action?backlogId=${storybacklog.id}">
		      <c:out value="Project: ${storybacklog.name}" />
		    </a>
		    </li> 
		    
		    <li class = "backlogHierarchy-iteration">
		      <a href="editBacklog.action?backlogId=${backlog.id}">
		        <c:out value="Iteration: ${backlog.name}" />
		      </a>
		    </li>
	    </c:when>
	    <%-- Show Product->Standalone Iteration --%>
	    <c:when test="${aef:isProduct(storybacklog)}">
	    <li class = "backlogHierarchy-product">
	    <a href="editBacklog.action?backlogId=${storybacklog.id}">
	      <c:out value="Product: ${storybacklog.name}" />
	    </a>
	    </li>
	    <li class = "backlogHierarchy-iteration">
	    <a href="editBacklog.action?backlogId=${backlog.id}">
	      <c:out value="Iteration: ${backlog.name}" />
	    </a>
	    </li> 
	    </c:when>
	    <%-- Show Only Standalone Iteration --%>
        <c:otherwise>
			<li class = "backlogHierarchy-iteration">
		    <a href="editBacklog.action?backlogId=${backlog.id}">
		      <c:out value="Iteration: ${backlog.name}"/>
		    </a>
		    </li>
		</c:otherwise>   
	 </c:choose>
   </c:when>
  
  <c:when test="${aef:isIteration(backlog)}">
    <li class = "backlogHierarchy-product">
    <a href="editBacklog.action?backlogId=${backlog.parent.parent.id}">
      <c:out value="Product: ${backlog.parent.parent.name}" />
    </a>
    </li>
    
    <li class = "backlogHierarchy-project">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="Project: ${backlog.parent.name}" />
    </a>
    </li> 
    
    <li class = "backlogHierarchy-iteration">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="Iteration: ${backlog.name}" />
      </a>
    </li>
  </c:when>
  
  <c:when test="${aef:isProject(backlog)}">   
    <li class = "backlogHierarchy-product">
    <a href="editBacklog.action?backlogId=${backlog.parent.id}">
      <c:out value="Product: ${backlog.parent.name}" />
    </a>
    </li> 
    
    <li class = "backlogHierarchy-project">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="Project: ${backlog.name}" />
      </a>
    </li>
  </c:when>
  
  <c:when test="${aef:isProduct(backlog)}">
    <li class = "backlogHierarchy-product">
      <a href="editBacklog.action?backlogId=${backlog.id}">
        <c:out value="Product: ${backlog.name}" />
      </a>
    </li>
  </c:when>
</c:choose>
</ul>

</div>