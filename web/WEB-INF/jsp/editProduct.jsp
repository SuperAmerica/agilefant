<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>

<c:if test="${product.id > 0}">
	<aef:bct productId="${productId}"/>
</c:if>

<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"  /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit product</h2>
	<ww:form action="storeProduct">
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
		<td><ww:textfield size="53" name="product.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea name="product.description" cols="40" rows="6" /></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>			
			<ww:submit value="Store"/>
    	<ww:submit name="action:contextView" value="Cancel"/>

		</td>	
		</tr>
	  </table>

<!-- TODO -->
		<c:if test="${product.id > 0}">
	
<table><tr><td>
		<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">
	</c:if>
		<c:if test="${product.id > 0}">
			<p>Projects		
 				<ww:url id="createLink" action="createDeliverable" includeParams="none">
					<ww:param name="productId" value="${product.id}"/>
				</ww:url>
				<ww:a href="%{createLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
			</p>

			<display:table class="listTable" name="product.deliverables" id="row" requestURI="editProduct.action">
				<display:column sortable="true" title="Id" property="id"/>
		
				<display:column sortable="true" title="Name">
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
				<display:column sortable="true" title="Activity" property="activityType.name"/>
				<display:column sortable="true"	title="Effort left" sortProperty="effortEstimate.time">
					${row.effortEstimate}
				</display:column>
				<display:column sortable="true" title="Effort done" sortProperty="performedEffort.time">
					${row.performedEffort}
				</display:column>
				<display:column sortable="false" title="Actions">
					<!-- <ww:url id="editLink" action="editDeliverable" includeParams="none">
						<ww:param name="productId" value="${product.id}"/>
						<ww:param name="deliverableId" value="${row.id}"/>						
					</ww:url> -->
					<ww:url id="deleteLink" action="deleteDeliverable" includeParams="none">
						<ww:param name="productId" value="${product.id}"/>
						<ww:param name="deliverableId" value="${row.id}"/>						
					</ww:url>
					<!-- <ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">Edit</ww:a>| -->
					<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}">Delete</ww:a>
				</display:column>
			</display:table>

		<p>
			Backlog items
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${product.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>		
		</p>
		</c:if>		
	</ww:form>
	
	<c:if test="${!empty product.backlogItems}">

			<display:table name="product.backlogItems" id="row" requestURI="editProduct.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">
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
				<!-- <ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url> -->
					<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>
					<!-- <ww:a href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">Edit</ww:a>|-->
					<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}">Delete</ww:a>
				</display:column>
			</display:table>
	</c:if>
	
		<c:if test="${product.id > 0}">
				
		</div>
				
		</div>
</td></tr></table>

</c:if>
<%@ include file="./inc/_footer.jsp" %>