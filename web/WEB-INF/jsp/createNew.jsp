<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<aef:menu navi="createNew" pageHierarchy="${pageHierarchy}"/>
<aef:existingObjects/>
<h2>Create a new object</h2>

<p>
<ul>

<li>
	<ww:url id="createLink" action="createProduct" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new product  &raquo;</ww:a>
</li>

<c:if test="${hasProducts && hasActivityTypes}">
	<li>	
		<ww:url id="createLink" action="createDeliverable" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new project  &raquo;</ww:a>
	</li>
</c:if>

<c:if test="${hasProjects}">
	<li>	
		<ww:url id="createLink" action="createIteration" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new iteration  &raquo;</ww:a>
	</li>
</c:if>

<c:if test="${hasIterations}">
	<li>	
		<ww:url id="createLink" action="createIterationGoal" includeParams="none"/>
		<ww:a href="%{createLink}">Create a new iteration goal  &raquo;</ww:a>
	</li>
</c:if>	

<c:if test="${hasProducts}">
<li>	
	<ww:url id="createLink" action="createBacklogItem" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new backlog item  &raquo;</ww:a>
</li>
</c:if>	

<li>	
	<ww:url id="createLink" action="createUser" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new user &raquo;</ww:a>
</li>

<li>	
	<ww:url id="createLink" action="createActivityType" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new activity type &raquo;</ww:a>
</li>

<c:if test="${hasActivityTypes}">
<li>	
	<ww:url id="createLink" action="createWorkType" includeParams="none"/>
	<ww:a href="%{createLink}">Create a new work type &raquo;</ww:a>
</li>
</c:if>	
	
</ul>
</p>

<%@ include file="./inc/_footer.jsp" %>