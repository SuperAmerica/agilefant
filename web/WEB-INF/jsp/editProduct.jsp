<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:menu navi="1"  /> 
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
	</ww:form>
	
	<c:if test="${!empty product.backlogItems}">
		<p>
			Has backlog items:
		</p>
		<br/>
		<ul>
		<c:forEach items="${product.backlogItems}" var="item">
			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<li>
				${item.name} (${fn:length(item.tasks)} tasks) - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</li>
		</c:forEach>
		</ul>

	</c:if>
<%@ include file="./inc/_footer.jsp" %>