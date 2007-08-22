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
					<ww:submit value="Store"/>
				</td>
			</tr>
		</table>


	</ww:form>
		
	<table><tr><td>
		<c:if test="${iterationId != 0}">
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

				<display:column title="Tasks" sortable="false" class="taskColumn">
					<c:if test="${!empty row.tasks}"> 
						<c:set var="divId" value="${divId + 1}" scope="page"/>
						<a href="javascript:toggleDiv(${divId});" title="Click to expand">
							${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % complete<br/>
	   					<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
							<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
								<ww:param name="started"     value="${tsl['started']}"/>
								<ww:param name="blocked"     value="${tsl['blocked']}"/>
								<ww:param name="implemented" value="${tsl['implemented']}"/>
								<ww:param name="done"        value="${tsl['done']}"/>
							</ww:url>
				 			<img src="${imgUrl}"/> 
						</a>					
						<aef:tasklist tasks="${row.tasks}"   contextViewName="editIteration"  contextObjectId="${iteration.id}" divId="${divId}"/>									
					</c:if>
				</display:column>
			
				<display:column sortable="true" sortProperty="assignee.fullName" title="Responsible" >
					${aef:html(row.assignee.fullName)}
				</display:column>
		
				<display:column sortable="true" defaultorder="descending" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>
				</display:column>
		
				<display:column sortable="true" title="Effort done">
					${row.performedEffort}
				</display:column>
		
				<display:column sortable="true" title="Estimate">
					<c:choose>
						<c:when test="${!empty row.effortEstimate}">
							${row.effortEstimate}
						</c:when>
						<c:otherwise>
							${row.allocatedEffort}
						</c:otherwise>
					</c:choose>
				</display:column>
				
			<display:column sortable="false" title="Actions">
				<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
        	<ww:param name="backlogItemId" value="${row.id}"/> 
        </ww:url> 
         <ww:a href="%{deleteLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Delete</ww:a> 					
			</display:column>
			
			<display:footer>
			 	<tr>
			 		<td>Total:</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td>&nbsp;</td>
			 		<td><c:out value="${iteration.performedEffort}" /></td>
			 		<td><c:out value="${iteration.totalEstimate}" /></td>
			  	<tr>
			 </display:footer>				
		
			</display:table>
		</p>
		</div>
	</c:if>	
	</div>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
