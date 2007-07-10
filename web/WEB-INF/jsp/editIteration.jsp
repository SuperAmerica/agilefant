<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:choose>
	<c:when test="${iteration.id == 0}">
		<aef:bct deliverableId="${deliverableId}"/>
	</c:when>
	<c:otherwise>
		<aef:bct iterationId="${iterationId}"/>
	</c:otherwise>
</c:choose>

<c:set var="divId" value="1336" scope="page"/>
<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"/> 
<ww:actionerror/>
<ww:actionmessage/>
				
<h2>Iteration</h2>

<c:choose>
		<c:when test="${IterationId == 0}">
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
	
<ww:form action="store${new}Iteration">
	<ww:hidden name="iterationId" value="${iteration.id}"/>
	<ww:hidden name="deliverableId"/> 

	<ww:date name="%{new java.util.Date()}" id="start"/>
	<ww:date name="%{new java.util.Date()}" id="end"/>
		
	<c:if test="${iteration.id > 0}">
		<ww:date name="%{iteration.startDate}" id="start"/>
		<ww:date name="%{iteration.endDate}" id="end"/>
	</c:if>
		
	<table class="formTable">
		<tr>
			<td></td>
			<td></td>
			<td></td>	
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td><ww:textfield size="60" name="iteration.name"/></td>	
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td><ww:textarea cols="70" rows="10" name="iteration.description" /></td>	
		</tr>
		<tr>
			<td>Start date</td>
			<td>*</td>
			<td>
				<ww:datepicker value="%{#start}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="iteration.startDate" /> 
			</td>	
		</tr>
		<tr>
			<td>End date</td>
			<td>*</td>
			<td>
			  <ww:datepicker value="%{#end}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="iteration.endDate" /> 
			</td>	
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td>
			<ww:submit value="Store"/>
    	<ww:submit name="action:popContext" value="Back"/>
		</td>	
		</tr>
	</table>
</ww:form>	

<table><tr><td>
	<div id="subItems">
		<div id="subItemHeader">
			Iteration goals
			<ww:url id="createIterationGoalLink" action="createIterationGoal" includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createIterationGoalLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>		
		</div>
	
	<c:if test="${!empty iteration.iterationGoals}">
	<div id="subItemContent">
		<p>
			<display:table class="listTable" name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
		
				<display:column sortable="true" title="Name" sortProperty="name" class="longNameColumn">
					<ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>							
					<ww:a href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">
						${aef:html(row.name)}
					</ww:a>
				</display:column>
					
				<display:column sortable="true" sortProperty="description" title="Description" >
					${aef:html(row.description)}
				</display:column>
	
				<display:column sortable="false" title="Actions">
					<ww:url id="deleteLink" action="deleteIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
						<ww:param name="iterationId" value="${iteration.id}"/>
					</ww:url>
				<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
				
			</display:table>
		</p>
	</div>
	</c:if>
		
	<div id="subItemHeader">
		Backlog items 
		<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
			<ww:param name="backlogId" value="${iteration.id}"/>
		</ww:url>
		<ww:a href="%{createBacklogItemLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>		
	</div>	

	<c:if test="${!empty iteration.backlogItems}">
		<div id="subItemContent">
		<p>
			<display:table class="listTable" name="iteration.backlogItems" id="row2" requestURI="editIteration.action">

				<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row2.id}"/>
					</ww:url>
					<div>				
					<ww:a href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">
						${aef:html(row2.name)}
					</ww:a>
					</div>
				</display:column>

				<display:column title="Tasks" sortable="false" class="taskColumn">
					<c:if test="${!empty row2.tasks}"> 
						<c:set var="divId" value="${divId + 1}" scope="page"/>
						
						
						<a href="javascript:toggleDiv(${divId});" title="Click to expand">
							${fn:length(row2.tasks)} tasks, <aef:percentDone backlogItemId="${row2.id}"/> % complete<br/>
	   					<aef:taskStatusList backlogItemId="${row2.id}" id="tsl"/>							   
							<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
								<ww:param name="started"     value="${tsl['started']}"/>
								<ww:param name="blocked"     value="${tsl['blocked']}"/>
								<ww:param name="implemented" value="${tsl['implemented']}"/>
								<ww:param name="done"        value="${tsl['done']}"/>
							</ww:url>
							<img src="${imgUrl}"/> 
						</a>	
						<aef:tasklist tasks="${row2.tasks}"   contextViewName="editIteration"  contextObjectId="${iteration.id}" divId="${divId}"/>	
						
					</c:if>
				</display:column>
	
				<display:column sortable="true" sortProperty="assignee.fullName" title="Responsible" >
					${aef:html(row2.assignee.fullName)}
				</display:column>
	
				<display:column sortable="true" defaultorder="descending" title="Priority" >
					<ww:text name="backlogItem.priority.${row2.priority}"/>
				</display:column>
	
				<display:column sortable="true" sortProperty="iterationGoal.name" title="Iteration Goal" class="iterationGoalColumn">
					${aef:html(row2.iterationGoal.name)}
				</display:column>
		
				<display:column sortable="true" sortProperty="performedEffort" title="Effort done">
					${row2.performedEffort}
				</display:column>
	
				<display:column sortable="true" title="Estimate">
					<c:choose>
						<c:when test="${!empty row2.effortEstimate}">
							${row2.effortEstimate}
						</c:when>
						<c:otherwise>
							${row2.allocatedEffort}
						</c:otherwise>
					</c:choose>
				</display:column>
				
				<display:column sortable="false" title="Actions">
					<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
            <ww:param name="backlogItemId" value="${row2.id}"/> 
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
</td></tr></table>

<p>	
		<img src="drawChart.action?iterationId=${iteration.id}"/>
	</p>

<%@ include file="./inc/_footer.jsp" %>
