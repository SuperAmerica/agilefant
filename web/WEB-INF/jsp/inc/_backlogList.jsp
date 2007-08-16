<%@ include file="./_taglibs.jsp"%>

<ww:form action="moveSelectedItems">

	<!-- Return to this backlog after submit -->
	<ww:hidden name="backlogId" value="${backlog.id}" />

	<display:table class="listTable" name="backlog.backlogItems" id="item"
		requestURI="${currentAction}.action" >

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
						backlogItemId="${item.id}" /> % complete<br />
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
							<ww:text name="task.status.${item.placeHolder.status}"/>	
						</a>
						<aef:tasklist tasks="${item.tasks}"
							contextViewName="${currentAction}" contextObjectId="${backlog.id}"
							divId="${divId}" placeholder="true" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</display:column>

		<display:column sortable="true" sortProperty="assignee.fullName"
			title="Responsible">
		${aef:html(item.assignee.fullName)}
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
			title="Effort Left<br/><span style='white-space: nowrap'>T / BLI</span>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.taskSumEffEst == null}">-</c:when>
					<c:otherwise>${item.taskSumEffEst}</c:otherwise>
				</c:choose>
				 / 
				<c:choose>
					<c:when test="${item.bliEffEst == null}">-</c:when>
					<c:otherwise>${item.bliEffEst}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="bliOrigEst" defaultorder="descending"
				title="Original Estimate<br/><span style='white-space: nowrap'>T / BLI
				</span>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.taskSumOrigEst == null}">-</c:when>
					<c:otherwise>${item.taskSumOrigEst}</c:otherwise>
				</c:choose>
				 / 
				<c:choose>
					<c:when test="${item.bliOrigEst == null}">-</c:when>
					<c:otherwise>${item.bliOrigEst}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="performedEffort" defaultorder="descending"
			title="Work reported">
			${item.performedEffort}
		</display:column>

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
		<display:column sortable="false" title="Actions">
			<ww:url id="deleteLink" action="deleteBacklogItem"
				includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}" />
				<ww:param name="contextObjectId" value="${backlog.id}" />
			</ww:url>
			<ww:a
					href="%{deleteLink}&contextViewName=${currentAction}">
					Delete
			</ww:a>
		</display:column>

		<display:footer>
			<tr>
				<td>Total:</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<%-- Effort left --%>
				<td><c:out value="${backlog.bliEffortLeftSum}" /></td>
				<%-- Original estimate --%>
				<td><c:out value="${backlog.bliOrigEstSum}" /></td>
				<%-- Work reported --%>
				<td><c:out value="${backlog.performedEffort}" /></td>
				<td></td>
			<tr>
		</display:footer>
	</display:table>

	<aef:productList />

	<p>
	<aef:backlogDropdown selectName="targetBacklog"
			preselectedBacklogId="${backlog.id}" backlogs="${productList}" /> 
		<ww:submit type="button" value="%{'MoveItems'}" label="Move to backlog" />
	</p>
</ww:form>
