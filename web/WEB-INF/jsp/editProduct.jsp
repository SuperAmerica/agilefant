<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:if test="${product.id > 0}">
	<aef:bct productId="${productId}" />
</c:if>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

<c:choose>
	<c:when test="${productId == 0}">
		<h2>Create product</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit product</h2>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${productId == 0}">
		<c:set var="new" value="New" scope="page" />
	</c:when>
	<c:otherwise>
		<c:set var="new" value="" scope="page" />
	</c:otherwise>
</c:choose>

<ww:form action="store${new}Product">
	<ww:hidden name="productId" value="${product.id}" />

	<table class="formTable">
		<tr>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td><ww:textfield size="60" name="product.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td><ww:textarea name="product.description" cols="70" rows="10" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><c:choose>
				<c:when test="${productId == 0}">
					<ww:submit value="Create" />
				</c:when>
				<c:otherwise>
					<ww:submit value="Save" />
					<span class="deleteButton"> <ww:submit
						action="deleteProduct" value="Delete" /> </span>
				</c:otherwise>
			</c:choose></td>
		</tr>
	</table>

</ww:form>

<table>
	<tr>
		<td><c:if test="${product.id > 0}">
			<div id="subItems">
			<div id="subItemHeader">Projects <ww:url id="createLink"
				action="createProject" includeParams="none">
				<ww:param name="productId" value="${product.id}" />
			</ww:url> <ww:a
				href="%{createLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
			</div>

			<c:if test="${!empty product.projects}">
				<div id="subItemContent">
				<p><display:table class="listTable" name="product.projects"
					id="row" requestURI="editProduct.action">
					<display:column sortable="true" sortProperty="name" title="Name">
						<ww:url id="editLink" action="editProject" includeParams="none">
							<ww:param name="productId" value="${product.id}" />
							<ww:param name="projectId" value="${row.id}" />
						</ww:url>

						<ww:a
							href="%{editLink}&contextViewName=editProduct&contextObjectId=${product.id}">
							${aef:html(row.name)}
						</ww:a>
					</display:column>

					<display:column sortable="true" title="# of iterations">
						${fn:length(row.iterations)}
					</display:column>

					<display:column sortable="true" sortProperty="projectType.name"
						title="Activity" property="projectType.name" />

					<display:column sortable="true" title="Effort left"
						sortProperty="effortLeft.time">
						${row.totalEffortLeftSum}
						
					</display:column>
					
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteProject"
							includeParams="none">
							<ww:param name="productId" value="${product.id}" />
							<ww:param name="projectId" value="${row.id}" />
						</ww:url>
						<ww:a
							href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}">Delete</ww:a>
					</display:column>
				</display:table></p>
				</div>
			</c:if>

			<div id="subItemHeader">Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${product.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
			</div>

			<c:if test="${!empty product.backlogItems}">
				<div id="subItemContent">
				<p><%@ include file="./inc/_backlogList.jsp"%>
				</p>
				</div>
			</c:if></div>
		</c:if></td>
	</tr>
</table>

<%@ include file="./inc/_footer.jsp"%>