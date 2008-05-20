<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<script type="text/javascript" src="static/js/generic.js"></script>

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
			<td colspan="2">
			<c:choose>
				<c:when test="${iterationGoalId == 0}">
					<select name="iterationId" onchange="disableIfEmpty(this.value, ['createButton', 'createAndCloseButton']);">
				</c:when>
				<c:otherwise>
					<select name="iterationId" onchange="disableIfEmpty(this.value, ['saveButton', 'saveAndCloseButton']);">
				</c:otherwise>
			</c:choose>
				<option value="" class="inactive">(select iteration)</option>
				<c:forEach items="${productList}" var="product">
					<option value="" class="inactive productOption">${product.name}</option>
					<c:forEach items="${product.projects}" var="project">
						<option value="" class="inactive projectOption">${project.name}</option>
						<c:forEach items="${project.iterations}" var="iter">
							<c:choose>
								<c:when test="${iter.id == currentIterationId}">
									<option selected="selected" value="${iter.id}" class="iterationOption">${iter.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${iter.id}" class="iterationOption">${iter.name}</option>
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
					<td><ww:submit value="Create" id="createButton" /> <ww:submit
						action="storeCloseIterationGoal" value="Create & Close" id="createAndCloseButton"/></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" id="saveButton"/> <ww:submit
						action="storeCloseIterationGoal" value="Save & Close" id="saveAndCloseButton"/></td>
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
			<div id="subItemHeader">
			<table cellspacing="0" cellpadding="0">
                <tr>
                <td class="header">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}" />
				<ww:param name="iterationGoalId" value="${iterationGoal.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
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
							<c:when test="${!(empty row.tasks)}">
								<a href="javascript:toggleDiv(${divId});"
									title="Click to expand"> <c:out
									value="${fn:length(row.tasks)}" /> tasks, <aef:percentDone
									backlogItemId="${row.id}" /> % complete<br />
								<aef:stateList backlogItemId="${row.id}" id="tsl" /> <ww:url
									id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted" value="${tsl['notStarted']}" />
									<ww:param name="started" value="${tsl['started']}" />
									<ww:param name="pending" value="${tsl['pending']}" />
									<ww:param name="blocked" value="${tsl['blocked']}" />
									<ww:param name="implemented" value="${tsl['implemented']}" />
									<ww:param name="done" value="${tsl['done']}" />
								</ww:url> <img src="${imgUrl}" /> </a>
								<aef:tasklist backlogItem="${row}"
									contextViewName="editIterationGoal"
									contextObjectId="${iterationGoal.id}" divId="${divId}" />
							</c:when>
							<c:otherwise>
								<a href="javascript:toggleDiv(${divId});" title="Click to expand">
                            <ww:text name="backlogItem.state.${row.state}"/><br />
                            
                            <c:choose>
                            <c:when test="${row.state == 'NOT_STARTED'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="notStarted" value="1" /> </ww:url> 
                                <img src="${imgUrl}" /> 
                            </c:when>
                            <c:when test="${row.state == 'STARTED'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="started" value="1" /> </ww:url> 
                                <img src="${imgUrl}" />
                            </c:when>
                            <c:when test="${row.state == 'PENDING'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="pending" value="1" /> </ww:url> 
                                <img src="${imgUrl}" />
                            </c:when>
                            <c:when test="${row.state == 'BLOCKED'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="blocked" value="1" /> </ww:url> 
                                <img src="${imgUrl}" />
                            </c:when>
                            <c:when test="${row.state == 'IMPLEMENTED'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="implemented" value="1" /> </ww:url> 
                                <img src="${imgUrl}" />
                            </c:when>
                            <c:when test="${row.state == 'DONE'}" >
                                <ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
                                <ww:param name="done" value="1" /> </ww:url> 
                                <img src="${imgUrl}" />
                            </c:when>
                            </c:choose>
                                
                        </a>
								<aef:tasklist backlogItem="${row}"
									contextViewName="editIterationGoal"
									contextObjectId="${iterationGoal.id}" divId="${divId}" />
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
		</c:if>
</td>
</tr>
</table>		
<%@ include file="./inc/_footer.jsp"%>