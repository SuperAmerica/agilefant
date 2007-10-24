<%@ include file="./_taglibs.jsp"%>

<ww:form action="doActionOnMultipleBacklogItems">

	<!-- Return to this backlog after submit -->
	<ww:hidden name="backlogId" value="${backlog.id}" />

	<display:table class="listTable" name="backlog.sortedBacklogItems" id="item"
		requestURI="${currentAction}.action" >

		<!-- Checkboxes for bulk-moving backlog items -->
		<display:column sortable="false" title="" class="selectColumn">
			<div><ww:checkbox name="selected" fieldValue="${item.id}" /></div>
		</display:column>

		<display:column sortable="true" sortProperty="name" title="Name"
			class="shortNameColumn">

			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
			</ww:url>
			<div><ww:a
				href="%{editLink}&contextObjectId=${backlog.id}&contextViewName=${currentAction}">
			${aef:html(item.name)}
		</ww:a></div>
		</display:column>

		<display:column title="Status" sortable="false" class="taskColumn">
			<c:set var="divId" value="${divId + 1}" scope="page" />
			<c:choose>
				<c:when test="${!(empty item.tasks || fn:length(item.tasks) == 1)}">
					<a href="javascript:toggleDiv(${divId});" title="Click to expand">
					
					<c:if test="${item.placeHolder != null}">
						${fn:length(item.tasks) - 1} 
					</c:if>		
					<c:if test="${item.placeHolder == null}">
						${fn:length(item.tasks)} 
					</c:if>
					
					tasks, <aef:percentDone
						backlogItemId="${item.id}" />% done<br />
						<aef:taskStatusList backlogItemId="${item.id}" id="tsl" /> 
						<ww:url
							id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="${tsl['notStarted']}" />
								<ww:param name="started" value="${tsl['started']}" />
								<ww:param name="blocked" value="${tsl['blocked']}" />
								<ww:param name="implemented" value="${tsl['implemented']}" />
								<ww:param name="done" value="${tsl['done']}" />
						</ww:url> 
						<img src="${imgUrl}" /> 
					</a>
					
					<aef:tasklist tasks="${item.tasks}"
						contextViewName="${currentAction}" contextObjectId="${backlog.id}"
						divId="${divId}"/>
				</c:when>
				<c:otherwise>
					<c:if test="${!empty item.placeHolder}">
						 <a href="javascript:toggleDiv(${divId});" title="Click to expand">
							<ww:text name="task.status.${item.placeHolder.status}"/><br />
							
							<c:choose>
							<c:when test="${item.placeHolder.status == 'NOT_STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="1" /> </ww:url> 
								<img src="${imgUrl}" /> 
							</c:when>
							<c:when test="${item.placeHolder.status == 'STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="started" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.placeHolder.status == 'BLOCKED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="blocked" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.placeHolder.status == 'IMPLEMENTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="implemented" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.placeHolder.status == 'DONE'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="done" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							</c:choose>
								
						</a>
						<aef:tasklist tasks="${item.tasks}"
							contextViewName="${currentAction}" contextObjectId="${backlog.id}"
							divId="${divId}" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</display:column>

		<display:column sortable="true" sortProperty="assignee.fullName"
			title="Responsible" class="responsibleColumn">
		<div>${aef:html(item.assignee.fullName)}</div>
	</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item.priority}" />
		</display:column>

		<c:choose>
			<c:when test="${currentContext == 'iteration'}">
				<display:column sortable="true" sortProperty="iterationGoal.name"
				title="Iteration Goal" class="iterationGoalColumn">
				<div>${aef:html(item.iterationGoal.name)}</div>
				</display:column>
			</c:when>
			<c:otherwise>
				
			</c:otherwise>
		</c:choose>

		<display:column sortable="true" sortProperty="bliEffEst" defaultorder="descending"
			title="Effort Left<br/><span style='white-space: nowrap'>T + BLI</span>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.taskSumEffEst == null}">&mdash;</c:when>
					<c:otherwise>${item.taskSumEffEst}</c:otherwise>
				</c:choose>
				 + 
				<c:choose>
					<c:when test="${item.bliEffEst == null}">&mdash;</c:when>
					<c:otherwise>${item.bliEffEst}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="bliOrigEst" defaultorder="descending"
				title="Original Estimate<br/><span style='white-space: nowrap'>T | BLI
				</span>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.taskSumOrigEst == null}">&mdash;</c:when>
					<c:otherwise>${item.taskSumOrigEst}</c:otherwise>
				</c:choose>
				 | 
				<c:choose>
					<c:when test="${item.bliOrigEst == null}">&mdash;</c:when>
					<c:otherwise>${item.bliOrigEst}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

<%--		<display:column sortable="true" sortProperty="performedEffort" defaultorder="descending"
			title="Work reported">
			${item.performedEffort}
		</display:column>
--%>
<%--		<display:column sortable="true" title="DebugEst">
			<c:choose>
				<c:when test="${!empty item.effortEstimate}">
					${item.effortEstimate}
				</c:when>
				<c:otherwise>
					${item.allocatedEffort}
				</c:otherwise>
			</c:choose>
		</display:column>
--%>
<%--		<display:column sortable="false" title="Actions">
			<ww:url id="deleteLink" action="deleteBacklogItem"
				includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
				<ww:param name="contextObjectId" value="${backlog.id}" />
			</ww:url>
			<ww:a
					href="%{deleteLink}&contextViewName=${currentAction}"
					onclick="return confirmDeleteBli()">
					Delete
			</ww:a>
		</display:column> --%>

		<display:footer>
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<c:if test="${currentContext == 'iteration'}">
					<td>&nbsp;</td>
				</c:if>
				<%-- Effort left --%>
				<td><c:out value="${backlog.bliEffortLeftSum}" /></td>
				<%-- Original estimate --%>
				<td><c:out value="${backlog.bliOrigEstSum}" /></td>
				<%-- Work reported --%>
			<tr>
		</display:footer>
	</display:table>

	<aef:productList />

	<p>
	<ww:submit type="button" name="itemAction" value="%{'MoveSelected'}" label="Move selected to" />
	<aef:backlogDropdown selectName="targetBacklog"
			preselectedBacklogId="${backlog.id}" backlogs="${productList}" /> 
	</p>
	<ww:submit type="button" name="itemAction" value="%{'DeleteSelected'}" label="Delete Selected" />
</ww:form>
