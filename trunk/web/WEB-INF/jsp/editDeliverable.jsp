<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

		<c:choose>
			<c:when test="${!empty deliverable.id}">
				<c:set var="currentDeliverableId" value="${deliverable.id}" scope="page"/>
				<c:if test="${deliverable.id != previousDeliverableId}">
					<c:set var="previousDeliverableId" value="${deliverable.id}" scope="session"/>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="currentDeliverableId" value="${previousDeliverableId}" scope="page"/>
			</c:otherwise>
		</c:choose>			

<c:choose>
	<c:when test="${deliverable.id == 0}">
		<aef:bct productId="${productId}"/>
	</c:when>
	<c:otherwise>
		<aef:bct deliverableId="${deliverableId}" />
	</c:otherwise>
</c:choose>

<aef:menu navi="${contextName}" pageHierarchy="${pageHierarchy}" /> 

	<ww:actionerror/>
	<ww:actionmessage/>


<ww:date name="%{new java.util.Date()}" id="start"/>
<ww:date name="%{new java.util.Date()}" id="end"/>
		
<c:if test="${deliverable.id > 0}">
<ww:date name="%{deliverable.startDate}" id="start"/>
<ww:date name="%{deliverable.endDate}" id="end"/>

</c:if>



<%--  TODO: fiksumpi virheekäsittely --%>
<c:choose>
	<c:when test="${empty activityTypes}">
				<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
				
				No activity types avalable. <ww:a href="%{workTypeLink}">Add activity types</ww:a>			
	</c:when>
	<c:otherwise>

					<ww:url id="viewLink" action="viewDeliverable" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>												
					</ww:url>
						<ww:a href="%{viewLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">View</ww:a> | Edit

					<aef:productList/>
<aef:deliverableMenu deliverableId="${deliverable.id}"/>

<h2>Project</h2>

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
		<td><ww:textfield size="53" name="deliverable.name"/></td>	
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
		<td>
			    <ww:datepicker value="%{#start}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="deliverable.startDate" /> 
		
		</td>	
		</tr>
			
		<tr>
		<td>End date</td>
		<td>*</td>
		<td>
			    <ww:datepicker value="%{#end}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="deliverable.endDate" /> 		
		</td>	
		</tr>
			
		<tr>
		<td></td>
		<td></td>
		<td>
			<ww:submit value="Store"/>
    		<ww:submit name="action:popContext" value="Cancel"/>
			
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
				<ww:a href="%{createLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Create new &raquo;</ww:a>
			</p>

			<display:table class="listTable" name="deliverable.iterations" id="row" requestURI="editDeliverable.action">
				<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editIteration" includeParams="none">
					<ww:param name="iterationId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">
						${aef:html(row.name)}
				</ww:a>			
				</display:column>
				<display:column sortable="true" title="# of backlog items">
					${fn:length(row.backlogItems)}
				</display:column>
				<display:column sortable="true"	title="Effort left" sortProperty="effortEstimate.time">
					${row.effortEstimate}
				</display:column>
				<display:column sortable="true" title="Effort done" sortProperty="performedEffort.time">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Start date" >
				<ww:date name="#attr.row.startDate"/>
				</display:column>
				<display:column sortable="true" title="End date" >
				<ww:date name="#attr.row.endDate"/>
				</display:column>
				<display:column sortable="false" title="Actions">
					<!-- <ww:url id="editLink" action="editIteration" includeParams="none">
						<ww:param name="iterationId" value="${row.id}"/>
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
					</ww:url>
					-->
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="iterationId" value="${row.id}"/>
					</ww:url>
					<!-- <ww:a href="%{editLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Edit</ww:a>|-->
					<ww:a href="%{deleteLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Delete</ww:a>
				</display:column>
			</display:table>
	
		<p>Backlog items 
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${deliverable.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Create new &raquo;</ww:a>		
		</p>
		</c:if>
	<c:if test="${!empty deliverable.backlogItems}">
		<display:table class="listTable" name="deliverable.backlogItems" id="row" requestURI="editDeliverable.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">
					${aef:html(row.name)}			
				</ww:a>
			</display:column>
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
			<display:column sortable="true" title="Effort done" sortProperty="performedEffort.time">
				${row.performedEffort}
			</display:column>
			<display:column sortable="false" title="Actions">
				<!-- ><ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url> -->
				<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>
				<!-- ><ww:a href="%{editLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Edit</ww:a>|-->
				<ww:a href="%{deleteLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Delete</ww:a>
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