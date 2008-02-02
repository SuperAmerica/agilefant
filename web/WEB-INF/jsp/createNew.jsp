<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<aef:menu navi="createNew" pageHierarchy="${pageHierarchy}" />
<aef:existingObjects />
<h2>Create a new object</h2>

<p>
<ul>

	<li><ww:url id="createLink" action="createProduct"
		includeParams="none" /> <ww:a href="%{createLink}">Create a new product &raquo;</ww:a>
	</li>

	<c:choose>
		<c:when test="${hasProducts && hasProjectTypes}">
			<li><ww:url id="createLink" action="createProject"
				includeParams="none" /> <ww:a href="%{createLink}">Create a new project &raquo;</ww:a>
			</li>
		</c:when>
		<c:otherwise>
			<li class="inactive"><span
				title="Create a product and a project type before creating a project">
			Create a new project &raquo;</span></li>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${hasProjects}">
			<li><ww:url id="createLink" action="createIteration"
				includeParams="none" /> <ww:a href="%{createLink}">Create a new iteration &raquo;</ww:a>
			</li>
		</c:when>
		<c:otherwise>
			<li class="inactive"><span
				title="Create a project before creating an iteration"> Create
			a new iteration &raquo;</span></li>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${hasIterations}">
			<li><ww:url id="createLink" action="createIterationGoal"
				includeParams="none" /> <ww:a href="%{createLink}?contextViewName=createNew&resetContextView=true">Create a new iteration goal &raquo;</ww:a>
			</li>
		</c:when>
		<c:otherwise>
			<li class="inactive"><span
				title="Create an iteration before creating an iteration goal">
			Create a new iteration goal &raquo;</span></li>
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${hasProducts}">
			<li><ww:url id="createLink" action="createBacklogItem"
				includeParams="none" /> <ww:a href="%{createLink}?contextViewName=createNew&resetContextView=true">Create a new backlog item &raquo;</ww:a>
			</li>
		</c:when>
		<c:otherwise>
			<li class="inactive"><span
				title="Create a product before creating a backlog item">
			Create a new backlog item &raquo;</span></li>
		</c:otherwise>
	</c:choose>

	<li><ww:url id="createLink" action="createUser"
		includeParams="none" /> <ww:a href="%{createLink}">Create a new user &raquo;</ww:a>
	</li>
	
	<li><ww:url id="createLink" action="createTeam"
		includeParams="none" /> <ww:a href="%{createLink}">Create a new team &raquo;</ww:a>
	</li>

	<li><ww:url id="createLink" action="createProjectType"
		includeParams="none" /> <ww:a href="%{createLink}">Create a new project type &raquo;</ww:a>
	</li>
</ul>
</p>

<%@ include file="./inc/_footer.jsp"%>