<%@ include file="./_taglibs.jsp"%>

<ww:form action="moveSelectedItems">

	<!-- Return to this backlog after submit -->
	<ww:hidden name="backlogId" value="${iteration.id}" />

	<display:table class="listTable" name="backlog.backlogItems" id="item"
		requestURI="editIteration.action">

		<!-- Checkboxes for bulk-moving backlog items -->
		<display:column sortable="false" title="">
			<ww:checkbox name="selected" fieldValue="${item.id}" />
		</display:column>

		<display:column sortable="true" sortProperty="name" title="Name"
			class="shortNameColumn">

			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
			</ww:url>
			<div><ww:a
				href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">
			${aef:html(item.name)}
		</ww:a></div>
		</display:column>

		<display:column title="Tasks" sortable="false" class="taskColumn">
			<c:if test="${!empty item.tasks}">
				<c:set var="divId" value="${divId + 1}" scope="page" />


				<a href="javascript:toggleDiv(${divId});" title="Click to expand">
				${fn:length(item.tasks)} tasks, <aef:percentDone
					backlogItemId="${item.id}" /> % complete<br />
				<aef:taskStatusList backlogItemId="${item.id}" id="tsl" /> <ww:url
					id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="notStarted" value="${tsl['notStarted']}" />
					<ww:param name="started" value="${tsl['started']}" />
					<ww:param name="blocked" value="${tsl['blocked']}" />
					<ww:param name="implemented" value="${tsl['implemented']}" />
					<ww:param name="done" value="${tsl['done']}" />
				</ww:url> <img src="${imgUrl}" /> </a>
				<aef:tasklist tasks="${item.tasks}" contextViewName="editIteration"
					contextObjectId="${iteration.id}" divId="${divId}" />

			</c:if>
		</display:column>

		<display:column sortable="true" sortProperty="assignee.fullName"
			title="Responsible">
		${aef:html(item.assignee.fullName)}
	</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item.priority}" />
		</display:column>

		<display:column sortable="true" sortProperty="iterationGoal.name"
			title="Iteration Goal" class="iterationGoalColumn">
		${aef:html(item.iterationGoal.name)}
	</display:column>

		<display:column sortable="true" sortProperty="performedEffort"
			title="Effort done">
		${item.performedEffort}
	</display:column>

		<display:column sortable="true" title="Estimate">
			<c:choose>
				<c:when test="${!empty item.effortEstimate}">
				${item.effortEstimate}
			</c:when>
				<c:otherwise>
				${item.allocatedEffort}
			</c:otherwise>
			</c:choose>
		</display:column>

		<display:column sortable="false" title="Actions">
			<ww:url id="deleteLink" action="deleteBacklogItem"
				includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
			</ww:url>
			<ww:a
				href="%{deleteLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Delete</ww:a>
		</display:column>

		<display:footer>
			<tr>
				<td>Total:</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td><c:out value="${iteration.performedEffort}" /></td>
				<td><c:out value="${iteration.totalEstimate}" /></td>
			<tr>
		</display:footer>
	</display:table>

	<aef:productList />

	<p><aef:backlogDropdown selectName="targetBacklog"
		preselectedBacklogId="${iteration.id}" backlogs="${productList}" /> <ww:submit
		type="button" value="%{'MoveItems'}" label="Move to backlog" /></p>
</ww:form>
