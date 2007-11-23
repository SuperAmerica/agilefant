<%@ include file="./_taglibs.jsp"%>

<h2>All items assigned to <c:out value="${user.fullName}" /> from ongoing iterations</h2>

<c:if test="${!empty iterations}">

<div id="subItems">

<c:forEach items="${iterations}" var="it">


	<div id="subItemHeader">
		
		<ww:url id="parentActionUrl" action="editProduct" includeParams="none">
			<ww:param name="productId" value="${it.deliverable.product.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.deliverable.product.name}:" /></u>
		</ww:a>

		<ww:url id="parentActionUrl" action="editDeliverable" includeParams="none">
			<ww:param name="deliverableId" value="${it.deliverable.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.deliverable.name}" /></u>
		</ww:a>
		&nbsp;&ndash;&nbsp;
		<ww:url id="parentActionUrl" action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="${it.id}" />
		</ww:url>
		<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
			<u><c:out value="${it.name}" /></u>
		</ww:a>
		<ww:url id="activityTypeActionUrl" action="editActivityType" includeParams="none">
			<ww:param name="activityTypeId" value="${it.deliverable.activityType.id}" />
		</ww:url>
		<ww:a href="%{activityTypeActionUrl}&contextViewName=dailyWork">
			<u><c:out value="(${it.deliverable.activityType.name})" /></u>
		</ww:a>
	</div>
		

<div id="subItemContent">
<p>

<table>
<tr><td>

<display:table class="dailyWorkIteration" name="bliList"
	id="item" requestURI="${currentAction}.action" >
	
	<display:column sortable="true" title="Name" class="shortNameColumn">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${item.id}" />
		</ww:url>
		<div><ww:a href="%{editLink}&contextViewName=dailyWork">
			${aef:html(item.name)}
		</ww:a></div>
	</display:column>
	<%--
	<c:set var="divId" value="${divId + 1}" scope="page" />
	
	<display:column sortable="true" title="Name" class="shortNameColumn">
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${item.id}" />
		</ww:url>
		<div>
	<a href="javascript:toggleDiv(${divId})">${aef:html(item.name)}</a>
	</div>
	
	
	<div style="display:none;" id="${divId}">
	
	
	<aef:backlogItemForm backlogItem="${item}" />
	
	</div>	
	</display:column> --%>

	<display:column sortable="true" sortProperty="bliEffEst" defaultorder="descending"
			title="Effort Left<br/>">
			<span style="white-space: nowrap">
				<c:choose>
					<c:when test="${item.totalEffortLeft == null}">&mdash;
						<%--<ww:textfield size="10" name="updatedEffortLeft" value="&mdash;"/>--%>
					</c:when>
					<c:otherwise>${item.totalEffortLeft}
					<%--
						<ww:textfield size="10" name="updatedEffortLeft" value="${item.totalEffortLeft}"/>
						--%>
					</c:otherwise> 
				</c:choose>
			</span>
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

		<display:column sortable="true" defaultorder="descending"
			title="Priority">
			<ww:text name="backlogItem.priority.${item.priority}" />
			<%--<ww:select name="updatedItemPriority" value="#attr.item.priority.name" list="#{'UNDEFINED':'undefined', 'BLOCKER':'+++++', 'CRITICAL':'++++', 'MAJOR':'+++', 'MINOR':'++', 'TRIVIAL':'+'}"/>--%>
		</display:column>

		<display:footer>
			<tr>
				<td>&nbsp;</td>
				<td><c:out value="${userEffortLeft}" /></td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
		</display:footer>
		
		
</display:table>
</td>
<%--</ww:form>--%>

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

