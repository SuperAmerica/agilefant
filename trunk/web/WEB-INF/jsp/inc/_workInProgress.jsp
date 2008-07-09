<%@ include file="./_taglibs.jsp"%>

<h2>Started items assigned to <c:out value="${user.fullName}" /></h2>


<c:if test="${!(empty backlogItemsForUserInProgress)}">
<div id="subItems">

<div id="subItemHeader">
<table cellspacing="0" cellpadding="0">
<tr>
<td class="header">
Backlog items
</td>
</tr>
</table>
</div>

<div id="subItemContent"><display:table
	name="backlogItemsForUserInProgress" id="row"
	requestURI="dailyWork.action">
	<!-- Display the backlog row name -->
	<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${row.id}" />
		</ww:url>
		<div>
		<ww:a href="#" id="${row.id}" onclick="openThemeBusinessModal('${row.id}', 'editBacklogItemBusinessThemes.action',${row.id},0); return false;">
			<img src="static/img/add_theme.png" alt="Edit themes" title="Edit themes" />
		</ww:a>
			
		<c:forEach items="${row.businessThemes}" var="businessTheme">
			<span class="businessTheme" title="${businessTheme.description}">
				<ww:a href="#" id="${row.id}" onclick="openThemeBusinessModal('${row.id}', 'editBacklogItemBusinessThemes.action',${row.id}, ${businessTheme.id}); return false;">
					${businessTheme.name}
				</ww:a>
			</span>
		</c:forEach>
		<ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(row.name)}
		</ww:a></div>
	</display:column>

	<!-- Display the iteration goal -->
	<display:column sortable="true" title="Iteration goal"
		class="iterationGoalColumn" sortProperty="iterationGoal.name">
		<ww:url id="editLink" action="editIterationGoal" includeParams="none">
			<ww:param name="iterationGoalId" value="${row.iterationGoal.id}" />
		</ww:url>
		<div>		
		<ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(row.iterationGoal.name)}
		</ww:a></div>
	</display:column>
	
	<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${row.id}"/></div>
	</display:column>

	<!-- Display the priority -->
	<display:column sortable="true" defaultorder="descending"
		title="Priority">
		<ww:text name="backlogItem.priority.${row.priority}" />
	</display:column>

	<!-- Display state -->
	<display:column title="State" sortable="false" class="taskColumn">
		<%@ include file="./_backlogItemStatusBar.jsp"%>
		<aef:tasklist backlogItem="${row}"
			contextViewName="${currentAction}" contextObjectId="${backlog.id}"
			divId="${divId}" hourReport="${hourReport}"/>
	</display:column>

	<!-- Display context -->
	<display:column sortable="false" title="Context" class="contextColumn">
		<div><c:forEach items="${row.parentBacklogs}" var="parent">
			<c:choose>
				<c:when test="${aef:isIteration(parent)}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<ww:url id="parentActionUrl" action="editIteration"
						includeParams="none">
						<ww:param name="iterationId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:when test="${aef:isProject(parent)}">
					<c:choose>
						<c:when test="${!aef:isUserAssignedTo(parent, user)}">
							<img src="static/img/unassigned.png"
								title="The user has not been assigned to this project."
								alt="The user has not been assigned to this project." />
						</c:when>
						<c:otherwise>
							&nbsp;&nbsp;&nbsp;							
						</c:otherwise>
					</c:choose>
					<ww:url id="parentActionUrl" action="editProject"
						includeParams="none">
						<ww:param name="projectId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:otherwise>
					<ww:url id="parentActionUrl" action="editProduct"
						includeParams="none">
						<ww:param name="productId" value="${parent.id}" />
					</ww:url>
				</c:otherwise>
			</c:choose>
			<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
				<c:out value="${parent.name}" />
			</ww:a>
			<c:if test="${aef:isProject(parent)}">

			(<c:out value="${parent.projectType.name}" />)
			</c:if>
			<br />
		</c:forEach></div>
	</display:column>

</display:table></div>
</div>
</c:if>