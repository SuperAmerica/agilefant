<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:if test="${product.id > 0}">
	<aef:bct productId="${productId}"/>
</c:if>

<c:set var="divId" value="1336" scope="page"/>
<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"  /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Product</h2>
	
	<c:choose>
		<c:when test="${productId == 0}">
			<c:set var="new" value="New" scope="page"/>
		</c:when>
		<c:otherwise>
			<c:set var="new" value="" scope="page"/>
		</c:otherwise>
	</c:choose>
	
	<ww:form action="store${new}Product">
		<ww:hidden name="productId" value="${product.id}"/>
		
		<table class="formTable">
			<tr>
				<td></td>
				<td></td>
				<td></td>	
			</tr>
			<tr>
				<td>Name</td>
				<td>*</td>
				<td><ww:textfield size="60" name="product.name"/></td>	
			</tr>
			<tr>
				<td>Description</td>
				<td></td>
				<td><ww:textarea name="product.description" cols="70" rows="10" /></td>	
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>			
					<ww:submit value="Store"/>
    			<ww:submit name="action:contextView" value="Back"/>
				</td>	
			</tr>
	  </table>
	
	</ww:form>
			
<table><tr><td>
		<div id="subItems">
		<div id="subItemHeader">
			Projects		
 			<ww:url id="createLink" action="createDeliverable" includeParams="none">
				<ww:param name="productId" value="${product.id}"/>
			</ww:url>
			<ww:a href="%{createLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
		</div>

		<c:if test="${product.id > 0}">
		<div id="subItemContent">
			<p>
				<display:table class="listTable" name="product.deliverables" id="row" requestURI="editProduct.action">
					<display:column sortable="true" sortProperty="name" title="Name">
						<ww:url id="editLink" action="editDeliverable" includeParams="none">
							<ww:param name="productId" value="${product.id}"/>
							<ww:param name="deliverableId" value="${row.id}"/>
						</ww:url>	
									
						<ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">
							${aef:html(row.name)}
						</ww:a>
					</display:column>		
					
					<display:column sortable="true" title="# of iterations">
						${fn:length(row.iterations)}
					</display:column>
					
					<display:column sortable="true" sortProperty="activityType.name" title="Activity" property="activityType.name"/>
					
					<display:column sortable="true"	title="Effort left" sortProperty="effortEstimate.time">
						${row.effortEstimate}
					</display:column>
					
					<display:column sortable="true" title="Effort done" sortProperty="performedEffort.time">
						${row.performedEffort}
					</display:column>
					
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteDeliverable" includeParams="none">
							<ww:param name="productId" value="${product.id}"/>
							<ww:param name="deliverableId" value="${row.id}"/>						
						</ww:url>
						<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}">Delete</ww:a>
					</display:column>
					
				</display:table>
			</p>
		</div>
		</c:if>
		
		<div id="subItemHeader">
			Backlog items
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${product.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>		
		</div>
	
		<c:if test="${!empty product.backlogItems}">
		<div id="subItemContent">
			<p>
				<display:table name="product.backlogItems" id="row2" requestURI="editProduct.action">

					<display:column sortable="true" sortProperty="name" title="Name" class="shortNameColumn">
						<ww:url id="editLink" action="editBacklogItem" includeParams="none">
							<ww:param name="backlogItemId" value="${row2.id}"/>
						</ww:url>
						<ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">
							${aef:html(row2.name)}
						</ww:a>				
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
							<aef:tasklist tasks="${row2.tasks}"   contextViewName="editProduct"  contextObjectId="${product.id}" divId="${divId}"/>
						</c:if>
					</display:column>
					
					<display:column sortable="true" sortProperty="assignee.fullName" title="Responsible" >
					${aef:html(row2.assignee.fullName)}
				</display:column>
	
				<display:column sortable="true" defaultorder="descending" title="Priority" >
					<ww:text name="backlogItem.priority.${row2.priority}"/>
				</display:column>
	
				<display:column sortable="true" title="Effort done">
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
						<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}">Delete</ww:a>
					</display:column>
					
				</display:table>
			</p>
		</div>
		</c:if>
	</div>	
</td></tr></table>

<%@ include file="./inc/_footer.jsp" %>