<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct productId="${productId}"/>
<aef:menu navi="1"  pageHierarchy="${pageHierarchy}"  /> 
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit product</h2>
	<ww:form action="storeProduct">
		<ww:hidden name="productId" value="${product.id}"/>
		<p>		
			Name: <ww:textfield name="product.name"/>
		</p>
		<p>
			Description: <ww:textarea name="product.description" cols="40" rows="6" />
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
		
		<c:if test="${product.id > 0}">
			<h3>Deliverables</h3>
			<p>
 				<ww:url id="createLink" action="createDeliverable" includeParams="none">
					<ww:param name="productId" value="${product.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>
			</p>

			<display:table name="product.deliverables" id="row" requestURI="editProduct.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of iterations">
					${fn:length(row.iterations)}
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editDeliverable" includeParams="none">
						<ww:param name="productId" value="${product.id}"/>
						<ww:param name="deliverableId" value="${row.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="productId" value="${product.id}"/>
						<ww:param name="deliverableId" value="${row.id}"/>						
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>
		<h3>
			Has backlog items:
		</h3>

		<p>
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${product.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}">Create new</ww:a>		
		</p>
		</c:if>		
	</ww:form>


	
	<c:if test="${!empty product.backlogItems}">

			<display:table name="product.backlogItems" id="row" requestURI="editProduct.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of tasks">
					${fn:length(row.tasks)}
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
<%@ include file="./inc/_footer.jsp" %>