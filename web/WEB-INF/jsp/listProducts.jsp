<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="4" /> 


	
<p>Products found:</p>
<p>
<aef:productList/>

<p>
	<ww:url id="createLink" action="createProduct" includeParams="none"/>
	<ww:a href="%{createLink}">Create new product</ww:a>
</p>


			<display:table name="${ productList }" id="row" requestURI="listProducts.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of deliverables">
					${fn:length(row.deliverables)}
				</display:column>
				<display:column sortable="false" title="Action">
					<ww:url id="editLink" action="editProduct" includeParams="none">
						<ww:param name="productId" value="${row.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteProduct" includeParams="none">
						<ww:param name="productId" value="${row.id}"/>						
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>


<%@ include file="./inc/_footer.jsp" %>
