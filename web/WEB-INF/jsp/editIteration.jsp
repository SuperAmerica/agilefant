<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:choose>
	<c:when test="${iteration.id == 0}">
		<aef:bct projectId="${projectId}" />
	</c:when>
	<c:otherwise>
		<aef:bct iterationId="${iterationId}" />
	</c:otherwise>
</c:choose>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<aef:productList />

<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${iterationId == 0}">
		<h2>Create iteration</h2>
	</c:when>
	<c:otherwise>
		<h2><c:out value="${iteration.name}" />
		<span class="timeframe">
		[<c:out value="${iteration.startDate.date}.${iteration.startDate.month + 1}.${iteration.startDate.year + 1900}" />
		- <c:out value="${iteration.endDate.date}.${iteration.endDate.month + 1}.${iteration.endDate.year + 1900}" />]
		</span>
		<a href="" onclick="toggleDiv('editIterationForm'); return false;" class="editLink">(edit)</a>
		</h2>
		<p class="description">${iteration.description}</p>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${IterationId == 0}">
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<div id="editIterationForm" style="display: none;">

<ww:form action="store${new}Iteration">
	<ww:hidden name="iterationId" value="${iteration.id}" />
	<ww:date name="%{iteration.getTimeOfDayDate(6)}" id="start"
		format="%{getText('webwork.shortDateTime.format')}" />
	<ww:date name="%{iteration.getTimeOfDayDate(18)}" id="end"
		format="%{getText('webwork.shortDateTime.format')}" />
	<c:if test="${iteration.id > 0}">
		<ww:date name="%{iteration.startDate}" id="start"
			format="%{getText('webwork.shortDateTime.format')}" />
		<ww:date name="%{iteration.endDate}" id="end"
			format="%{getText('webwork.shortDateTime.format')}" />
	</c:if>

	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="iteration.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				name="iteration.description" /></td>
		</tr>
		<tr>
			<td>Project</td>
			<td>*</td>
			<td colspan="2"><select name="projectId">
				<option class="inactive" value="">(select project)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive">${aef:out(product.name)}</option>
					<c:forEach items="${product.projects}" var="project">
						<c:choose>
							<c:when test="${project.id == currentProjectId}">
								<option selected="selected" value="${project.id}"
									title="${project.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
							</c:when>
							<c:otherwise>
								<option value="${project.id}" title="${project.name}">&nbsp;&nbsp;&nbsp;&nbsp;${aef:out(project.name)}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td>Start date</td>
			<td>*</td>
			<td colspan="2"><ww:datepicker value="%{#start}" size="15" showstime="true"
				format="%{getText('webwork.datepicker.format')}" name="startDate" />
			</td>
		</tr>
		<tr>
			<td>End date</td>
			<td>*</td>
			<td colspan="2"><ww:datepicker value="%{#end}" size="15" showstime="true"
				format="%{getText('webwork.datepicker.format')}" name="endDate" />
			</td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${iterationId == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /></td>
					<td class="deleteButton"><ww:submit onclick="return confirmDelete()"
						action="deleteIteration" value="Delete" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>

</div>

<table>
	<tr>
		<td><c:if test="${iterationId != 0}">
			<div id="subItems">
			<div id="subItemHeader">Iteration goals <ww:url
				id="createIterationGoalLink" action="createIterationGoal"
				includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}" />
			</ww:url> <ww:a
				href="%{createIterationGoalLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>
			</div>
			<c:if test="${!empty iteration.iterationGoals}">
				<div id="subItemContent">
				<p><display:table class="listTable"
					name="iteration.iterationGoals" id="row"
					requestURI="editIteration.action" defaultsort="1">

					<display:column sortable="true" title="Name" sortProperty="name"
						class="longNameColumn">
						<ww:url id="editLink" action="editIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>

					<display:column sortable="true" sortProperty="description"
						title="Description">
					${aef:html(row.description)}
				</display:column>
					<display:column sortable="false" title="# of backlog items">
				  ${aef:html(fn:length(row.backlogItems))}
				</display:column>
				
				<display:column sortable="true" title="Effort left sum">
					<c:out value="${iterationGoalEffLeftSums[row.id]}" />
				</display:column>
				
				<display:column sortable="true" title="Original estimate sum">
					<c:out value="${iterationGoalOrigEstSums[row.id]}" />
				</display:column>

					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteIterationGoal"
							includeParams="none">
							<ww:param name="iterationGoalId" value="${row.id}" />
							<ww:param name="iterationId" value="${iteration.id}" />
						</ww:url>
						<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
					</display:column>

				</display:table></p>
				</div>
			</c:if>

			<div id="subItemHeader">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>
			</div>

			<c:if test="${!empty iteration.backlogItems}">
				<div id="subItemContent">
				<p><%@ include file="./inc/_backlogList.jsp"%>
				</p>
				</div>
			</c:if></div>
			<c:if test="${!empty iteration.backlogItems}">
				<p><img src="drawChart.action?iterationId=${iteration.id}" /></p>
			</c:if>
		</c:if></td>
	</tr>
</table>

<%@ include file="./inc/_footer.jsp"%>