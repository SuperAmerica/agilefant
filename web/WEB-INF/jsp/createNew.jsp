<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<aef:menu navi="createNew" pageHierarchy="${pageHierarchy}"/>
<aef:existingObjects/>
<h2>Create a new object</h2>

<p>
<ul>

<li>
	<ww:url id="createLink" action="createProduct" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new product</ww:a>
</li>

<c:choose>
<c:when test="${hasProducts && hasActivityTypes}">
	<li>	
		<ww:url id="createLink" action="createDeliverable" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new project</ww:a>
	</li>
</c:when>
<c:otherwise>
  <li class="inactive">
  	<span title="Create a product and a project type before creating a project">
  		Create a new project 
  	</span>
  </li>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${hasProjects}">
	<li>	
		<ww:url id="createLink" action="createIteration" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new iteration</ww:a>
	</li>
</c:when>
<c:otherwise>
  <li class="inactive">
  	<span title="Create a project before creating an iteration">
  		Create a new iteration
  	</span>
 	</li>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${hasIterations}">
	<li>	
		<ww:url id="createLink" action="createIterationGoal" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new iteration goal</ww:a>
	</li>
</c:when>	
<c:otherwise>
  <li class="inactive">
		<span title="Create an iteration before creating an iteration goal">
	  	Create a new iteration goal
		</span>
 	</li>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${hasProducts}">
<li>	
	<ww:url id="createLink" action="createBacklogItem" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new backlog item </ww:a>
</li>
</c:when>	
<c:otherwise>
  <li class="inactive">
		<span title="Create a product before creating a backlog item">
  		Create a new backlog item
 		</span>
	</li>
</c:otherwise>
</c:choose>

<li>	
	<ww:url id="createLink" action="createUser" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new user &raquo;</ww:a>
</li>

<li>	
	<ww:url id="createLink" action="createActivityType" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new project type &raquo;</ww:a>
</li>

<!-- 
<c:if test="${hasActivityTypes}">
<li>	
	<ww:url id="createLink" action="createWorkType" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new work type &raquo;</ww:a>
</li>
</c:if>	
-->
	
</ul>
</p>

<%@ include file="./inc/_footer.jsp" %>