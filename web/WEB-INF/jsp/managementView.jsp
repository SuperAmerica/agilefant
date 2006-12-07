<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="6" /> 


	
<p>Portfolio hierarchy:</p>

<aef:productList/>
<c:if test="${empty productList}">
No products
</c:if>
<ul>
<c:forEach items="${productList}" var="product">
	<ww:url id="productLink" action="managementView" includeParams="none"/>
	<li>
		<ww:a href="%{productLink}">${ product.name }</ww:a>
	</li>
	<ul>
		<c:forEach items="${product.deliverables}" var="deliverable">
			<ww:url id="deliverableLink" action="managementView" includeParams="none"/>
			<li>
				<ww:a href="%{deliverableLink}">${ deliverable.name }</ww:a>
			</li>
			<ul>
				<c:forEach items="${deliverable.iterations}" var="iteration">
					<ww:url id="iterationLink" action="managementView" includeParams="none"/>
					<li>
						<ww:a href="%{iterationLink}">${ iteration.name }</ww:a>
					</li>
				</c:forEach>
			</ul>
		</c:forEach>

	</ul>
</c:forEach>
</ul>


<%@ include file="./inc/_footer.jsp" %>
