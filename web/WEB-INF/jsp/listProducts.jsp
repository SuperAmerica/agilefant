<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="${contextName}" /> 
<c:set var="divId" value="1336" scope="page"/>


	
<h2>Product</h2>

<aef:productList/>

<p>
	<ww:url id="createLink" action="createProduct" includeParams="none"/>
	<ww:a href="%{createLink}">Create new product  &raquo;</ww:a>
</p>


			<display:table name="${ productList }" id="row" requestURI="listProducts.action">
				<display:column sortable="true" title="Name">
					<ww:url id="editLink" action="editProduct" includeParams="none">
						<ww:param name="productId" value="${row.id}"/>						
					</ww:url>
					<ww:a href="%{editLink}" title="${row.name}">${aef:out(row.name)}</ww:a>
				</display:column>
				<display:column title="Description">
					${aef:html(row.description)}
				</display:column>
				<display:column sortable="true" title="Deliverables">
					<c:if test="${!empty row.deliverables}"> 
				
							<c:set var="divId" value="${divId + 1}" scope="page"/>							
							<ww:a href="javascript:toggleDiv(${divId});" title="Click to expand">
								${fn:length(row.deliverables)} deliverables
							</ww:a>
							<aef:hiddenDeliverableList deliverables="${row.deliverables}"  contextViewName="listProducts" divId="${divId}"/>
					</c:if>
				</display:column>
				<display:column sortable="false" title="Action">
					<ww:url id="deleteLink" action="deleteProduct" includeParams="none">
						<ww:param name="productId" value="${row.id}"/>						
					</ww:url>
					<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>


<%@ include file="./inc/_footer.jsp" %>
