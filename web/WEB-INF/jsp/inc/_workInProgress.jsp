<%@ include file="./_taglibs.jsp"%>

<h2>Started items assigned to <c:out value="${user.fullName}" /></h2>


<c:if test="${!(empty backlogItemsForUserInProgress)}">
<div id="subItems">

<div id="subItemHeader">Backlog items</div>
<div id="subItemContent"><display:table
	name="backlogItemsForUserInProgress" id="item"
	requestURI="dailyWork.action">
	<!-- Display the backlog item name -->
	<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${item.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item.name)}
		</ww:a></div>
	</display:column>

	<!-- Display the iteration goal -->
	<display:column sortable="true" title="Iteration goal"
		class="iterationGoalColumn" sortProperty="iterationGoal.name">
		<ww:url id="editLink" action="editIterationGoal" includeParams="none">
			<ww:param name="iterationGoalId" value="${item.iterationGoal.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item.iterationGoal.name)}
		</ww:a></div>
	</display:column>
	
	<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${item.id}"/></div>
	</display:column>

	<!-- Display the priority -->
	<display:column sortable="true" defaultorder="descending"
		title="Priority">
		<ww:text name="backlogItem.priority.${item.priority}" />
	</display:column>

	<!-- Display state -->
	<display:column title="State" sortable="false" class="taskColumn">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<c:choose>
			<c:when test="${!(empty item.tasks || fn:length(item.tasks) == 1)}">
				<a href="javascript:toggleDiv(${divId});" title="Click to expand">
				<c:out value="${fn:length(item.tasks)}" /> tasks, <aef:percentDone
					backlogItemId="${item.id}" />% done<br />
				<aef:stateList backlogItemId="${item.id}" id="tsl" /> <ww:url
					id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="notStarted" value="${tsl['notStarted']}" />
					<ww:param name="started" value="${tsl['started']}" />
					<ww:param name="pending" value="${tsl['pending']}" />
					<ww:param name="blocked" value="${tsl['blocked']}" />
					<ww:param name="implemented" value="${tsl['implemented']}" />
					<ww:param name="done" value="${tsl['done']}" />
				</ww:url> <img src="${imgUrl}" /> </a>

				<aef:tasklist backlogItem="${item}"
					contextViewName="${currentAction}" contextObjectId="${backlog.id}"
					divId="${divId}" />
			</c:when>
			<c:otherwise>
				<a href="javascript:toggleDiv(${divId});" title="Click to expand">
				<ww:text name="task.state.${item.state}" /><br />

				<c:choose>
					<c:when test="${item.state == 'NOT_STARTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="notStarted" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item.state == 'STARTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="started" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item.state == 'PENDING'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="pending" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item.state == 'BLOCKED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="blocked" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item.state == 'IMPLEMENTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="implemented" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item.state == 'DONE'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="done" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
				</c:choose> </a>
				<aef:tasklist backlogItem="${item}"
					contextViewName="${currentAction}" contextObjectId="${backlog.id}"
					divId="${divId}" />
			</c:otherwise>
		</c:choose>
	</display:column>

	<!-- Display context -->
	<display:column sortable="false" title="Context" class="contextColumn">
		<div><c:forEach items="${item.parentBacklogs}" var="parent">
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