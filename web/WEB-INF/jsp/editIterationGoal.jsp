<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:if test="${iterationGoal.id > 0}">
	<aef:bct iterationGoalId="${iterationGoal.id}"/>
</c:if>
<c:if test="${iterationGoal.id == 0}">
	<aef:bct iterationId="${iterationId}"/>
</c:if>

<aef:menu navi="${contextName }"  pageHierarchy="${pageHierarchy}"/> 
<aef:productList/>
<ww:actionerror/>
<ww:actionmessage/>

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
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
	
	<ww:form action="store${new}IterationGoal">
		<ww:hidden name="iterationGoalId" value="${iterationGoal.id}"/>
		<table class="formTable">
			<tr>
				<td>Name</td>
				<td>*</td>
				<td><ww:textfield size="60" name="iterationGoal.name"/></td>	
			</tr>
			<tr>
				<td>Description</td>
				<td></td>
				<td><ww:textarea cols="70" rows="10" name="iterationGoal.description" /></td>	
			</tr>
			<tr>
				<td>Iteration</td>
				<td></td>
				<td>
					<select name="iterationId">
						<option value="" class="inactive">(select iteration)</option>
						<c:forEach items="${productList}" var="product">
							<option value="" class="inactive">${product.name}</option>
							<c:forEach items="${product.deliverables}" var="deliverable">
								<option value="" class="inactive">&nbsp;&nbsp;&nbsp;&nbsp;${deliverable.name}</option>
								<c:forEach items="${deliverable.iterations}" var="iter">
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
					</select>
				</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>
				<c:choose>
					<c:when test="${iterationGoalId == 0}">
						<ww:submit value="Create"/>
					</c:when>
					<c:otherwise>
					  <ww:submit value="Save"/>
 						<span class="deleteButton">
 							<ww:submit action="deleteIterationGoal" value="Delete"/>
 						</span>
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
		</table>


	</ww:form>
		
	<table><tr><td>
		<c:if test="${iterationGoalId != 0}">
		<div id="subItems">
			<div id="subItemHeader">
				Backlog items 
				<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
					<ww:param name="backlogId" value="${iteration.id}"/>
					<ww:param  name="iterationGoalId" value="${iterationGoal.id}"/>
				</ww:url>
				<ww:a href="%{createBacklogItemLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">Create new &raquo;</ww:a>		
			</div>
		<c:if test="${!empty iterationGoal.backlogItems}">
		<div id="subItemContent">
		<p>
			<display:table class="listTable" name="iterationGoal.backlogItems" id="row" requestURI="editIterationGoal.action">
		
				<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>				
					<ww:a href="%{editLink}&contextViewName=editIterationGoal&contextObjectId=${iterationGoal.id}">
						${aef:html(row.name)}
					</ww:a>
				</display:column>
		
				<display:column title="Status" sortable="false" class="taskColumn">
			<c:set var="divId" value="${divId + 1}" scope="page" />
			<c:choose>
				<c:when test="${!(empty row.tasks || fn:length(row.tasks) == 1)}">
					<a href="javascript:toggleDiv(${divId});" title="Click to expand">
					
					<c:if test="${row.placeHolder != null}">
						${fn:length(row.tasks) - 1} 
					</c:if>		
					<c:if test="${row.placeHolder == null}">
						${fn:length(row.tasks)} 
					</c:if>
					
					tasks, <aef:percentDone
						backlogItemId="${row.id}" /> % complete<br />
						<aef:taskStatusList backlogItemId="${row.id}" id="tsl" /> 
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
					<aef:tasklist tasks="${row.tasks}"
						contextViewName="${currentAction}" contextObjectId="${backlog.id}"
						divId="${divId}"/>
				</c:when>
				<c:otherwise>
					<c:if test="${!empty row.placeHolder}">
						 <a href="javascript:toggleDiv(${divId});" title="Click to expand">
							<ww:text name="task.status.${row.placeHolder.status}"/>	
						</a>
						<aef:tasklist tasks="${row.tasks}"
							contextViewName="${currentAction}" contextObjectId="${backlog.id}"
							divId="${divId}" placeholder="true" />
					</c:if>
				</c:otherwise>
			</c:choose>
		</display:column>
		
		
			</display:table>
		</p>
		</div>
	</c:if>	
	</div>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
