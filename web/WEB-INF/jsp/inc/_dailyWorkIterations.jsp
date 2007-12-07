<%@ include file="./_taglibs.jsp"%>

<h2>All items assigned to <c:out value="${user.fullName}" /> from ongoing iterations</h2>

<c:if test="${!empty iterations}">

<div id="subItems">

<c:forEach items="${iterations}" var="it">


	<div id="subItemHeader">
		
		<ww:url id="parentActionUrl" action="editProduct" includeParams="none">
			<ww:param name="productId" value="${it.project.product.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.project.product.name}:" /></u>
		</ww:a>

		<ww:url id="parentActionUrl" action="editProject" includeParams="none">
			<ww:param name="projectId" value="${it.project.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.project.name}" /></u>
		</ww:a>
		&nbsp;&ndash;&nbsp;
		<ww:url id="parentActionUrl" action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="${it.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.name}" /></u>
		</ww:a>
		<ww:url id="projectTypeActionUrl" action="editProjectType" includeParams="none">
			<ww:param name="projectTypeId" value="${it.project.projectType.id}" />
		</ww:url>
		<ww:a href="%{projectTypeActionUrl}&contextViewName=dailyWork">
			<u><c:out value="(${it.project.projectType.name})" /></u>
		</ww:a>
	</div>
		

<div id="subItemContent">
<p>

<table>
<tr><td>


<display:table class="dailyWorkIteration" name="${bliMap[it]}"
	id="item1" requestURI="${currentAction}.action" >
	
	<display:column sortable="true" sortProperty="name" title="Name"
		class="shortNameColumn">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${item1.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item1.name)}
		</ww:a></div>
	</display:column>

	<display:column sortable="true" sortProperty="effortLeft" defaultorder="descending"
			title="Effort Left<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item1.effortLeft == null}">&mdash;
					</c:when>
					<c:otherwise>${item1.effortLeft}
					</c:otherwise> 
				</c:choose>
			</span>
	</display:column>

	<display:column title="State" sortable="false" class="taskColumn">
			<c:set var="divId" value="${divId + 1}" scope="page" />
			<c:choose>
			<c:when test="${!(empty item1.tasks)}">
				<a href="javascript:toggleDiv(${divId});" title="Click to expand">
				<c:out value="${fn:length(item1.tasks)}" /> tasks, <aef:percentDone
					backlogItemId="${item1.id}" />% done<br />
				<aef:stateList backlogItemId="${item1.id}" id="tsl" /> <ww:url
					id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="notStarted" value="${tsl['notStarted']}" />
					<ww:param name="started" value="${tsl['started']}" />
					<ww:param name="pending" value="${tsl['pending']}" />
					<ww:param name="blocked" value="${tsl['blocked']}" />
					<ww:param name="implemented" value="${tsl['implemented']}" />
					<ww:param name="done" value="${tsl['done']}" />
				</ww:url> <img src="${imgUrl}" /> </a>

				<aef:tasklist backlogItem="${item1}"
					contextViewName="${currentAction}" contextObjectId="${backlog.id}"
					divId="${divId}" />
			</c:when>
			<c:otherwise>
				<a href="javascript:toggleDiv(${divId});" title="Click to expand">
				<ww:text name="task.state.${item1.state}" /><br />

				<c:choose>
					<c:when test="${item1.state == 'NOT_STARTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="notStarted" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item1.state == 'STARTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="started" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item1.state == 'PENDING'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="pending" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item1.state == 'BLOCKED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="blocked" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item1.state == 'IMPLEMENTED'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="implemented" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
					<c:when test="${item1.state == 'DONE'}">
						<ww:url id="imgUrl" action="drawExtendedBarChart"
							includeParams="none">
							<ww:param name="done" value="1" />
						</ww:url>
						<img src="${imgUrl}" />
					</c:when>
				</c:choose> </a>
				<aef:tasklist backlogItem="${item1}"
					contextViewName="${currentAction}" contextObjectId="${backlog.id}"
					divId="${divId}" />
			</c:otherwise>
		</c:choose>
		</display:column>

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item1.priority}" />
		</display:column>

		<display:footer>
			<tr>
				<td>&nbsp;</td>
				<td><c:out value="${effortSums[it]}" /></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
		</display:footer>

	
</display:table>
</td>

		<td class="smallBurndownColumn">
			<div>
				<img src="drawSmallChart.action?iterationId=${it.id}"/>
			</div>
		</td>
	</tr>
</table>

</div>

</c:forEach>
</div>

</c:if>

