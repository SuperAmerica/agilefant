<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:if test="${iterationGoal.id > 0}">
	<aef:bct iterationGoalId="${iterationGoal.id}" />
</c:if>
<c:if test="${iterationGoal.id == 0}">
	<aef:bct iterationId="${iterationId}" />
</c:if>

<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<aef:productList />
<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${iterationGoalId == 0}">
		<h2>Create iteration goal</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit iteration goal</h2>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${iterationGoalId == 0}">
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<ww:form action="store${new}IterationGoal">
	<ww:hidden name="iterationGoalId" value="${iterationGoal.id}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60"
				name="iterationGoal.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				name="iterationGoal.description" /></td>
		</tr>
		<tr>
			<td>Iteration</td>
			<td></td>
			<td colspan="2"><select name="iterationId">
				<option value="" class="inactive">(select iteration)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive">${product.name}</option>
					<c:forEach items="${product.projects}" var="project">
						<option value="" class="inactive">&nbsp;&nbsp;&nbsp;&nbsp;${project.name}</option>
						<c:forEach items="${project.iterations}" var="iter">
							<c:choose>
								<c:when test="${iter.id == currentIterationId}">
									<option selected="selected" value="${iter.id}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iter.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${iter.id}">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${iter.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</c:forEach>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<td><ww:submit value="Create" /> <ww:submit
						action="storeCloseIterationGoal" value="Create & Close" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /> <ww:submit
						action="storeCloseIterationGoal" value="Save & Close" /></td>
					<td class="deleteButton"><ww:submit
						onclick="return confirmDelete()" action="deleteIterationGoal"
						value="Delete" /></td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>

<table>
	<tr>
		<td><c:if test="${iterationGoalId != 0}">
			<div id="subItems">
			<div id="subItemHeader">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}" />
				<ww:param name="iterationGoalId" value="${iterationGoal.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">Create new &raquo;</ww:a>
			</div>
			<c:if test="${!empty iterationGoal.backlogItems}">
				<div id="subItemContent">
				<p><display:table class="listTable"
					name="iterationGoal.backlogItems" id="row"
					requestURI="editIterationGoal.action" defaultsort="3"
					defaultorder="descending">

					<display:column sortable="true" sortProperty="name" title="Name"
						class="shortNameColumn">
						<ww:url id="editLink" action="editBacklogItem"
							includeParams="none">
							<ww:param name="backlogItemId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{editLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">
						${aef:html(row.name)}
					</ww:a>
					</display:column>

					<display:column sortable="true" title="Responsibles"
						class="responsibleColumn">
						<div><aef:responsibleColumn backlogItemId="${row.id}" /></div>
					</display:column>

					<display:column sortable="true" defaultorder="descending"
						title="Priority">
						<ww:text name="backlogItem.priority.${row.priority}" />
					</display:column>

					<display:column title="State" sortable="false" class="taskColumn">
						<c:set var="divId" value="${divId + 1}" scope="page" />
						<c:choose>
							<c:when test="${!(empty row.tasks || fn:length(row.tasks) == 1)}">
								<a href="javascript:toggleDiv(${divId});"
									title="Click to expand"> <c:out
									value="${fn:length(row.tasks)}" /> tasks, <aef:percentDone
									backlogItemId="${row.id}" /> % complete<br />
								<aef:stateList backlogItemId="${row.id}" id="tsl" /> <ww:url
									id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted" value="${tsl['notStarted']}" />
									<ww:param name="started" value="${tsl['started']}" />
									<ww:param name="blocked" value="${tsl['blocked']}" />
									<ww:param name="implemented" value="${tsl['implemented']}" />
									<ww:param name="done" value="${tsl['done']}" />
								</ww:url> <img src="${imgUrl}" /> </a>
								<aef:tasklist backlogItem="${row}"
									contextViewName="${currentAction}"
									contextObjectId="${backlog.id}" divId="${divId}" />
							</c:when>
							<c:otherwise>
								<a href="javascript:toggleDiv(${divId});"
									title="Click to expand"> <ww:text
									name="task.state.${row.state}" /> </a>
								<aef:tasklist backlogItem="${row}"
									contextViewName="${currentAction}"
									contextObjectId="${backlog.id}" divId="${divId}" />
							</c:otherwise>
						</c:choose>
					</display:column>

					<display:column sortable="true" sortProperty="effortLeft"
						defaultorder="descending" title="Effort Left<br/>">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${row.effortLeft == null}">&mdash;</c:when>
							<c:otherwise>${row.effortLeft}</c:otherwise>
						</c:choose> </span>
					</display:column>

					<display:column sortable="true" sortProperty="originalEstimate"
						defaultorder="descending" title="Original Estimate<br/>">
						<span style="white-space: nowrap"> <c:choose>
							<c:when test="${row.originalEstimate == null}">&mdash;</c:when>
							<c:otherwise>${row.originalEstimate}</c:otherwise>
						</c:choose> </span>
					</display:column>
					
					
					<display:footer>
					<tr>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						
						<%-- Effort left --%>
						<td><c:out value="${effortLeftSum}" /></td>
						<%-- Original estimate --%>
						<td><c:out value="${originalEstimateSum}" /></td>
					</tr>
					</display:footer>
				</display:table></p>
				</div>
			</c:if></div>
		</c:if> <%@ include file="./inc/_footer.jsp"%>