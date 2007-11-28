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
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" /> 
<ww:actionerror/>
<ww:actionmessage/>

<ww:date name="%{new java.util.Date()}" id="start" format="%{getText('webwork.shortDateTime.format')}"/>	
<ww:date name="%{new java.util.Date()}" id="end" format="%{getText('webwork.shortDateTime.format')}"/>
		
<c:if test="${deliverable.id > 0}">
		<ww:date name="%{deliverable.startDate}" id="start" format="%{getText('webwork.shortDateTime.format')}"/>
		<ww:date name="%{deliverable.endDate}" id="end" format="%{getText('webwork.shortDateTime.format')}"/>
</c:if>



<%--  TODO: fiksumpi virheekäsittely --%>
<c:choose>
	<c:when test="${empty activityTypes}">
				<ww:url id="workTypeLink" action="listActivityTypes" includeParams="none"/>
				
				No project types avalable. <ww:a href="%{workTypeLink}">Add project types</ww:a>			
	</c:when>
	<c:otherwise>

<aef:productList/>

	<c:choose>
		<c:when test="${deliverableId == 0}">
			<h2>Create project</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit project</h2>
		</c:otherwise>
	</c:choose>

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
		<td>Product</td>
		<td>*</td>
		<td>
			
			<select name="productId">	
				<option class="inactive" value="">(select product)</option>
				<c:forEach items="${productList}" var="product">
					<c:choose>
						<c:when test="${product.id == currentProductId}">
							<option selected="selected" value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
						</c:when>
						<c:otherwise>
							<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>	
			</select>
		</td>
		</tr>	
			
		<tr>
		<td>Project type</td>
		<td></td>
		<td><ww:select name="activityTypeId" list="#attr.activityTypes" listKey="id" listValue="name" value="${deliverable.activityType.id}"/></td>	
		</tr>
			
		<tr>
		<td>Start date</td>
		<td>*</td>
		<td>
				<ww:datepicker value="%{#start}" size="15" showstime="true"  format="%{getText('webwork.datepicker.format')}" name="startDate" /> 		
		</td>	
		</tr>
			
		<tr>
		<td>End date</td>
		<td>*</td>
		<td>
			  <ww:datepicker value="%{#end}" size="15" showstime="true"  format="%{getText('webwork.datepicker.format')}" name="endDate" />
		</td>	
		</tr>
			
		<tr>
		<td></td>
		<td></td>
		<td>
			<c:choose>
				<c:when test="${deliverableId == 0}">
					<ww:submit value="Create"/>
				</c:when>
				<c:otherwise>
				  <ww:submit value="Save"/>
 					<span class="deleteButton">
 						<ww:submit action="deleteDeliverable" value="Delete"/>
 					</span>
				</c:otherwise>
			</c:choose>
		</td>	
		</tr>
	</table>
			
			
			
	</ww:form>		
	
<table><tr><td>
	<c:if test="${deliverable.id > 0}">
	<div id="subItems">
	
		<div id="subItemHeader">
			Iterations 
 			<ww:url id="createLink" action="createIteration" includeParams="none">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<ww:a href="%{createLink}&contextViewName=editDeliverable&contextObjectId=${deliverable.id}">Create new &raquo;</ww:a>
		</div>
		
		<c:if test="${!empty deliverable.iterations}">
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
</c:if>
</td></tr></table>

</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp" %>