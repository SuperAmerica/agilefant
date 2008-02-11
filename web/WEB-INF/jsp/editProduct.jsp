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
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="product.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea name="product.description" cols="70" rows="10" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${productId == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" />
					<td class="deleteButton"> <ww:submit onclick="return confirmDelete()"
						action="deleteProduct" value="Delete" /> </td>
				</c:otherwise>
			</c:choose>
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

					<display:column sortable="true" sortProperty="projectType.name"
						title="Project type" property="projectType.name" />

					<display:column sortable="true" title="Iterations">
						${fn:length(row.iterations)}
					</display:column>

					<display:column sortable="true" title="Items">
						${fn:length(row.backlogItems)}
					</display:column>

					<display:column sortable="true" title="Effort left"
						sortProperty="effortLeft.time">
						${effLeftSums[row]}
					</display:column>

					<display:column sortable="true" title="Original estimate"
						sortProperty="originalEstimate.time">
						${origEstSums[row]}
					</display:column>

					<display:column sortable="true" title="Start date">
						<ww:date name="#attr.row.startDate" />
					</display:column>

					<display:column sortable="true" title="End date">
						<ww:date name="#attr.row.endDate" />
					</display:column>
												
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteProject"
							includeParams="none">
							<ww:param name="productId" value="${product.id}" />
							<ww:param name="projectId" value="${row.id}" />
						</ww:url>
						<ww:a href="%{deleteLink}&contextViewName=editProduct&contextObjectId=${product.id}"
							onclick="return confirmDelete()">Delete</ww:a>
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