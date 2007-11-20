<%@ include file="./_taglibs.jsp"%>

<h2>Started backlog items</h2>

<div id="subItems">

<!-- <div id="subItemHeader">
	Backlog items		
</div>-->

<div id="subItemContent">

<display:table name="backlogItemsForUserInProgress" id="item" requestURI="dailyWork.action" >

	<!-- Display the backlog item name -->
	<display:column sortable="true" title="Backlog item">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${item.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item.name)}
		</ww:a></div>
	</display:column>

	<!-- Display the iteration goal -->
	<display:column sortable="true" title="Iteration goal" class="iterationGoalColumn">
		<ww:url id="editLink" action="editIterationGoal" includeParams="none">
			<ww:param name="iterationGoalId" value="${item.iterationGoal.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item.iterationGoal.name)}
		</ww:a></div>
	</display:column>

	<!-- Display the priority -->
	<display:column sortable="true" defaultorder="descending"
		title="Priority">
		<ww:text name="backlogItem.priority.${item.priority}" />
	</display:column>

	<!-- Display status -->
	<display:column title="Status" sortable="false" class="taskColumn">
		<c:set var="divId" value="${divId + 1}" scope="page" />
		<c:choose>
			<c:when test="${!(empty item.tasks || fn:length(item.tasks) == 1)}">
				<a href="javascript:toggleDiv(${divId});" title="Click to expand">

				<c:if test="${item.placeHolder != null}">
						${fn:length(item.tasks) - 1} 
					</c:if> <c:if test="${item.placeHolder == null}">
						${fn:length(item.tasks)} 
					</c:if> tasks, <aef:percentDone backlogItemId="${item.id}" />% done<br />
				<aef:taskStatusList backlogItemId="${item.id}" id="tsl" /> <ww:url
					id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="notStarted" value="${tsl['notStarted']}" />
					<ww:param name="started" value="${tsl['started']}" />
					<ww:param name="blocked" value="${tsl['blocked']}" />
					<ww:param name="implemented" value="${tsl['implemented']}" />
					<ww:param name="done" value="${tsl['done']}" />
				</ww:url> <img src="${imgUrl}" /> </a>

				<aef:tasklist tasks="${item.tasks}"
					contextViewName="${currentAction}" contextObjectId="${backlog.id}"
					divId="${divId}" />
			</c:when>
			<c:otherwise>
				<c:if test="${!empty item.placeHolder}">
					<a href="javascript:toggleDiv(${divId});" title="Click to expand">
					<ww:text name="task.status.${item.placeHolder.status}" /><br />

					<c:choose>
						<c:when test="${item.placeHolder.status == 'NOT_STARTED'}">
							<ww:url id="imgUrl" action="drawExtendedBarChart"
								includeParams="none">
								<ww:param name="notStarted" value="1" />
							</ww:url>
							<img src="${imgUrl}" />
						</c:when>
						<c:when test="${item.placeHolder.status == 'STARTED'}">
							<ww:url id="imgUrl" action="drawExtendedBarChart"
								includeParams="none">
								<ww:param name="started" value="1" />
							</ww:url>
							<img src="${imgUrl}" />
						</c:when>
						<c:when test="${item.placeHolder.status == 'BLOCKED'}">
							<ww:url id="imgUrl" action="drawExtendedBarChart"
								includeParams="none">
								<ww:param name="blocked" value="1" />
							</ww:url>
							<img src="${imgUrl}" />
						</c:when>
						<c:when test="${item.placeHolder.status == 'IMPLEMENTED'}">
							<ww:url id="imgUrl" action="drawExtendedBarChart"
								includeParams="none">
								<ww:param name="implemented" value="1" />
							</ww:url>
							<img src="${imgUrl}" />
						</c:when>
						<c:when test="${item.placeHolder.status == 'DONE'}">
							<ww:url id="imgUrl" action="drawExtendedBarChart"
								includeParams="none">
								<ww:param name="done" value="1" />
							</ww:url>
							<img src="${imgUrl}" />
						</c:when>
					</c:choose> </a>
					<aef:tasklist tasks="${item.tasks}"
						contextViewName="${currentAction}" contextObjectId="${backlog.id}"
						divId="${divId}" />
				</c:if>
			</c:otherwise>
		</c:choose>
	</display:column>

	<!-- Display context -->
	<display:column sortable="false" title="Context" class="contextColumn">
		<div>
		<c:forEach items="${item.parentBacklogs}" var="parent">
			<c:choose>
				<c:when test="${aef:isIteration(parent)}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<ww:url id="parentActionUrl" action="editIteration"
						includeParams="none">
							<ww:param name="iterationId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:when test="${aef:isDeliverable(parent)}">
					&nbsp;&nbsp;&nbsp;
					<ww:url id="parentActionUrl" action="editDeliverable"
						includeParams="none">
							<ww:param name="deliverableId" value="${parent.id}" />
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
			<c:if test="${aef:isDeliverable(parent)}">
			<ww:url id="activityTypeActionUrl" action="editActivityType" includeParams="none">
				<ww:param name="activityTypeId" value="${it.deliverable.activityType.id}" />
			</ww:url>
			(<ww:a href="%{activityTypeActionUrl}&contextViewName=dailyWork">
				<c:out value="${parent.activityType.name}" />
			</ww:a>)
			</c:if>
			<br/>
		</c:forEach>
		</div>
	</display:column>
	
</display:table>

</div>
</div>
