<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1" /> 
	
<p>Products found:</p>
<p>
<aef:productList/>
<c:forEach items="${productList}" var="product">
	<ww:url id="editLink" action="editProduct" includeParams="none">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="deleteLink" action="deleteProduct" includeParams="none">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="createDeliverableLink" action="createDeliverable" includeParams="none">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
		<ww:param name="backlogId" value="${product.id}"/>
	</ww:url>
	${product.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>|<ww:a href="%{createBacklogItemLink}">Add backlog item</ww:a>
	<ul>
		<c:forEach items="${product.deliverables}" var="deliverable">
			<ww:url id="editDeliverableLink" action="editDeliverable" includeParams="none">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<ww:url id="deleteDeliverableLink" action="deleteDeliverable" includeParams="none">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${deliverable.id}"/>
			</ww:url>
			<li>
				${deliverable.name} - <ww:a href="%{editDeliverableLink}">Edit</ww:a>|<ww:a href="%{deleteDeliverableLink}">Delete</ww:a>|<ww:a href="%{createBacklogItemLink}">Add backlog item</ww:a>
			</li>
		</c:forEach>
		<li>
			<ww:a href="%{createDeliverableLink}">Create new deliverable</ww:a>
		</li>
	</ul>
</c:forEach>
</p>
<p>
	<ww:url id="createLink" action="createProduct" includeParams="none"/>
	<ww:a href="%{createLink}">Create new product</ww:a>
</p>

<%@ include file="./inc/_footer.jsp" %>
