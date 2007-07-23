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

<c:set var="divId" value="1336" scope="page"/>
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

<aef:productList/>

<h2>Project</h2>

	<c:choose>
		<c:when test="${deliverableId == 0}">
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
	
	<ww:form action="store${new}Deliverable">
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
		<td><ww:textfield size="60" name="deliverable.name"/></td>	
		</tr>
			
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="70" rows="10" name="deliverable.description" /></td>	
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
    		<ww:submit name="action:popContext" value="Back"/>
			
			</td>	
		</tr>
	</table>
			
			
			
	</ww:form>		
	
<table><tr><td>
	<div id="subItems">
		<div id="subItemHeader">
			Iterations 
 			<ww:url id="createLink" action="createIteration" includeParams="none">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<ww:a href="%{createLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Create new &raquo;</ww:a>
		</div>
		<c:if test="${deliverable.id > 0}">
		<div id="subItemContent">
		<p>
			<display:table class="listTable" name="deliverable.iterations" id="row" requestURI="editDeliverable.action">
			
				<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
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
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="iterationId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{deleteLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Delete</ww:a>
				</display:column>
				
			</display:table>
		</p>
	</div>
	</c:if>
	<div id="subItemHeader">
		Backlog items 
		<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
			<ww:param name="backlogId" value="${deliverable.id}"/>
		</ww:url>
		<ww:a href="%{createBacklogItemLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Create new &raquo;</ww:a>		
	</div>	

	<c:if test="${!empty deliverable.backlogItems}">
	<div id="subItemContent">
		<%@ include file="./inc/_backlogList.jsp" %>	
	</div>
	</c:if>
</div>
</td></tr></table>

</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp" %>