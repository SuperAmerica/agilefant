<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct productId="${productId}"/>
<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}" /> 

	<ww:actionerror/>
	<ww:actionmessage/>

<%--  TODO: fiksumpi virheekäsittely --%>
<c:choose>
	<c:when test="${empty activityTypes}">
				<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
				
				No activity types avalable. <ww:a href="%{workTypeLink}">Add those first.</ww:a>			
	</c:when>
	<c:otherwise>


	<h2>Edit Deliverable</h2>
	<ww:form action="storeDeliverable">
		<ww:hidden name="deliverableId" value="${deliverable.id}"/>
		<ww:hidden name="productId"/>
			
		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
			
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield name="deliverable.name"/></td>	
		</tr>
			
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="deliverable.description" /></td>	
		</tr>
			
		<tr>
		<td>Activity type</td>
		<td></td>
		<td><ww:select name="activityTypeId" list="#attr.activityTypes" listKey="id" listValue="name" value="${deliverable.activityType.id}"/></td>	
		</tr>
			
		<tr>
		<td>Start date</td>
		<td>*</td>
		<td><ww:textfield name="deliverable.startDate" /> (Use date pattern: <ww:text name="webwork.date.format"/>)</td>	
		</tr>
			
		<tr>
		<td>End date</td>
		<td>*</td>
		<td><ww:textfield name="deliverable.endDate"/> (Use date pattern: <ww:text name="webwork.date.format"/>)</td>	
		</tr>
			
		<tr>
		<td></td>
		<td></td>
		<td>
			<ww:submit value="Store"/>
    		<ww:submit name="action:editProduct" value="Cancel"/>
			
			</td>	
		</tr>
	</table>
			
			
			
	</ww:form>		
	
		<c:if test="${deliverable.id > 0}">

<table><tr><td>

			<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">
	</c:if>

		<c:if test="${deliverable.id > 0}">
			
			<p>Iterations 
 				<ww:url id="createLink" action="createIteration" includeParams="none">
					<ww:param name="deliverableId" value="${deliverable.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new &raquo;</ww:a>
			</p>

			<display:table class="listTable" name="deliverable.iterations" id="row" requestURI="editDeliverable.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of backlog items">
					${fn:length(row.backlogItems)}
				</display:column>
				<display:column sortable="true"	title="Effort left" sortProperty="effortEstimate.time">
					${row.effortEstimate}
				</display:column>
				<display:column sortable="true" title="Performed effort" sortProperty="performedEffort.time">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Start date" >
					${row.startDate}
				</display:column>
				<display:column sortable="true" title="End date" >
					${row.endDate}
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editIteration" includeParams="none">
						<ww:param name="iterationId" value="${row.id}"/>
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="iterationId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>
	
		<p>Backlog items 
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${deliverable.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}">Create new &raquo;</ww:a>		
		</p>
		</c:if>
	<c:if test="${!empty deliverable.backlogItems}">
		<display:table class="listTable" name="deliverable.backlogItems" id="row" requestURI="editDeliverable.action">
			<display:column sortable="true" title="Id" property="id"/>
			<display:column sortable="true" title="Name" property="name"/>
			<display:column sortable="true" title="# of tasks">
				${fn:length(row.tasks)}
			</display:column>
			<display:column sortable="true" title="Priority" sortProperty="priority.ordinal">
				<ww:text name="task.priority.${row.priority}"/>
			</display:column>
			<display:column sortable="true" title="Effort estimate" sortProperty="allocatedEffort.time">
				${row.allocatedEffort}
			</display:column>
			<display:column sortable="true"	title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Performed effort" sortProperty="performedEffort.time">
				${row.performedEffort}
			</display:column>
			<display:column sortable="false" title="Actions">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
	</c:if>

		<c:if test="${deliverable.id > 0}">

</div>
</div>
</td></tr></table>

	</c:if>
	</c:otherwise>
</c:choose>


<%@ include file="./inc/_footer.jsp" %>