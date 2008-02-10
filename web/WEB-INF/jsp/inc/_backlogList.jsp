<%@ include file="./_taglibs.jsp"%>

<script language="javascript" type="text/javascript">
function validateDeletion() {
	var conf = confirm("The selected backlog items will be gone forever. Are you sure?");
	if (conf)
		return true;
	else
		return false;
}
</script>

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

		<display:column sortable="true" title="Responsibles" class="responsibleColumn">
		<div><aef:responsibleColumn backlogItemId="${item.id}"/></div>
		</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item.priority}" />
		</display:column>

		<display:column title="State" sortable="false" class="taskColumn">
			<c:set var="divId" value="${divId + 1}" scope="page" />
			<c:choose>
				<c:when test="${!(empty item.tasks)}">
					<a href="javascript:toggleDiv(${divId});" title="Click to expand">
					
						
					${fn:length(item.tasks)} 
					
					
					tasks, <aef:percentDone
						backlogItemId="${item.id}" />% done<br />
						<aef:stateList backlogItemId="${item.id}" id="tsl" /> 
						<ww:url
							id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="${tsl['notStarted']}" />
								<ww:param name="started" value="${tsl['started']}" />
								<ww:param name="pending" value="${tsl['pending']}" />
								<ww:param name="blocked" value="${tsl['blocked']}" />
								<ww:param name="implemented" value="${tsl['implemented']}" />
								<ww:param name="done" value="${tsl['done']}" />
						</ww:url> 
						<img src="${imgUrl}" /> 
					</a>
					
					<aef:tasklist backlogItem="${item}"
						contextViewName="${currentAction}" contextObjectId="${backlog.id}"
						divId="${divId}"/>
				</c:when>
				<c:otherwise>
					
						 <a href="javascript:toggleDiv(${divId});" title="Click to expand">
							<ww:text name="backlogItem.state.${item.state}"/><br />
							
							<c:choose>
							<c:when test="${item.state == 'NOT_STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="1" /> </ww:url> 
								<img src="${imgUrl}" /> 
							</c:when>
							<c:when test="${item.state == 'STARTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="started" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.state == 'PENDING'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="pending" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.state == 'BLOCKED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="blocked" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.state == 'IMPLEMENTED'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="implemented" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							<c:when test="${item.state == 'DONE'}" >
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="done" value="1" /> </ww:url> 
								<img src="${imgUrl}" />
							</c:when>
							</c:choose>
								
						</a>
						<aef:tasklist backlogItem="${item}"
							contextViewName="${currentAction}" contextObjectId="${backlog.id}"
							divId="${divId}" />
				</c:otherwise>
			</c:choose>
		</display:column>

		

		

		<display:column sortable="true" sortProperty="effortLeft" defaultorder="descending"
			title="Effort Left<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.effortLeft == null}">&mdash;</c:when>
					<c:otherwise>${item.effortLeft}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

		<display:column sortable="true" sortProperty="originalEstimate" defaultorder="descending"
				title="Original Estimate<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.originalEstimate == null}">&mdash;</c:when>
					<c:otherwise>${item.originalEstimate}</c:otherwise> 
				</c:choose>
			</span>
		</display:column>

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
				<%--
				<td><c:out value="${backlog.bliEffortLeftSum}" /></td>
				--%>
				<td><c:out value="${effortLeftSum}" /></td>
				<%-- Original estimate --%>
				<td><c:out value="${originalEstimateSum}" /></td>
			<tr>
		</display:footer>
	</display:table>

	<aef:productList />

	<table>
	<tr>
		<td>
			<ww:submit type="button" name="itemAction" value="%{'MoveSelected'}" label="Move selected to" />
		</td>
		<td class="backlogDropdownColumn">
			<aef:backlogDropdown selectName="targetBacklog"
				preselectedBacklogId="${backlog.id}" backlogs="${productList}" />
		</td>
		<td>
			<ww:submit type="button" name="itemAction" value="%{'DeleteSelected'}"
				onclick="return validateDeletion()" label="Delete selected" />
		</td>
	</tr>
	<tr>
		<td>
			<ww:submit type="button" name="itemAction" value="%{'PrioritizeSelected'}"
				label="Change priority to" />
		</td>
		<td>
			<ww:select name="targetPriority"
				list="#{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}" />
		</td>
	</tr>
	</table>
</ww:form>
