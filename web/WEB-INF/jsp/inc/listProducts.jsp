<%@ include file="_pageInit.jsp" %>
<p>Products found:</p>
<p>
<c:forEach items="${products}" var="product">
	<ww:url id="editLink" action="editProduct">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="deleteLink" action="deleteProduct">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="createDeliverableLink" action="createDeliverable">
		<ww:param name="productId" value="${product.id}"/>
	</ww:url>
	<ww:url id="createBacklogItemLink" action="createBacklogItem">
		<ww:param name="backlogId" value="${product.id}"/>
	</ww:url>
	${product.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>|<ww:a href="%{createBacklogItemLink}">Add backlog item</ww:a>
	<ul>
		<c:forEach items="${product.deliverables}" var="deliverable">
			<ww:url id="editDeliverableLink" action="editDeliverable">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<ww:url id="deleteDeliverableLink" action="deleteDeliverable">
				<ww:param name="deliverableId" value="${deliverable.id}"/>
			</ww:url>
			<li>
				${deliverable.name} - <ww:a href="%{editDeliverableLink}">Edit</ww:a>|<ww:a href="%{deleteDeliverableLink}">Delete</ww:a>
			</li>
		</c:forEach>
		<li>
			<ww:a href="%{createDeliverableLink}">Create new deliverable</ww:a>
		</li>
	</ul>
</p>
<p>
	<ww:url id="createLink" action="createProduct"/>
	<ww:a href="%{createLink}">Create new product</ww:a>
</p>
<%@ include file="./inc/_footer.jsp %>
</body>
</html>