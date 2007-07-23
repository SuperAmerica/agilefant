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
				<%@ include file="./inc/_backlogList.jsp" %>
			</p>
		</div>		
	</c:if>
	
</div>
</td></tr></table>

<p>	
		<img src="drawChart.action?iterationId=${iteration.id}"/>
	</p>

<%@ include file="./inc/_footer.jsp" %>
