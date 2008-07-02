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
        <c:set var="new" value="New" scope="page" />
    </c:when>
    <c:otherwise>
        <c:set var="new" value="" scope="page" />
    </c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${productId == 0}">
		<h2>Create product</h2>
		
		<div id="editProductForm">
		  <ww:form
                            action="store${new}Product">
                            <ww:hidden name="productId" value="${product.id}" />

                            <table class="formTable">
                                <tr>
                                    <td>Name</td>
                                    <td>*</td>
                                    <td colspan="2"><ww:textfield size="60"
                                        name="product.name" /></td>
                                </tr>
                                <tr>
                                    <td>Description</td>
                                    <td></td>
                                    <td colspan="2"><ww:textarea name="product.description" cssClass="useWysiwyg" 
                                        cols="70" rows="10" /></td>
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
                                            <td class="deleteButton"><ww:submit
                                                onclick="return confirmDelete()" action="deleteProduct"
                                                value="Delete" /></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </table>

                        </ww:form></div>
	</c:when>
	<c:otherwise>
	
		<h2><c:out value="${product.name}" /></h2>
	
	<table>
            <table>
                <tbody>
                    <tr>
                        <td>
                        <div id="subItems" style="margin-top: 0">
                        <div id="subItemHeader"><script type="text/javascript">
                function expandDescription() {
                    document.getElementById('descriptionDiv').style.maxHeight = "1000em";
                    document.getElementById('descriptionDiv').style.overflow = "visible";
                }
                function collapseDescription() {
                    document.getElementById('descriptionDiv').style.maxHeight = "12em";
                    document.getElementById('descriptionDiv').style.overflow = "hidden";
                }
                function editProduct() {
                	toggleDiv('editProductForm'); toggleDiv('descriptionDiv'); showWysiwyg('productDescription'); return false;
                }
                </script>


                        <table cellspacing="0" cellpadding="0">
                            <tr>
                                <td class="header">Details <a href=""
                                    onclick="return editProduct();">Edit
                                &raquo;</a></td>
                                <td class="icons">
                                <a href=""
                                    onclick="expandDescription(); return false;"> <img
                                    src="static/img/plus.png" width="18" height="18" alt="Expand"
                                    title="Expand" /> </a> <a href=""
                                    onclick="collapseDescription(); return false;"> <img
                                    src="static/img/minus.png" width="18" height="18"
                                    alt="Collapse" title="Collapse" /> </a></td>
                            </tr>
                        </table>
                        </div>
                        <div id="subItemContent">
						<div id="descriptionDiv" class="descriptionDiv"
							style="display: block;">
						<table class="infoTable" cellpadding="0" cellspacing="0">
							
							<tr>
								<td colspan="2" class="description" onclick="return editProduct();" >${product.description}</td>
								<td class="info4">&nbsp;</td>
							</tr>

						</table>
						</div>
						
						<div id="editProductForm" style="display: none;"><ww:form
							action="store${new}Product">
							<ww:hidden name="productId" value="${product.id}" />

							<table class="formTable">
								<tr>
									<td>Name</td>
									<td>*</td>
									<td colspan="2"><ww:textfield size="60"
										name="product.name" /></td>
								</tr>
								<tr>
									<td>Description</td>
									<td></td>
									<td colspan="2"><ww:textarea name="product.description" id="productDescription" 
										cols="70" rows="10" value="${aef:nl2br(product.description)}" /></td>
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
											<td class="deleteButton"><ww:submit
												onclick="return confirmDelete()" action="deleteProduct"
												value="Delete" /></td>
										</c:otherwise>
									</c:choose>
								</tr>
							</table>

						</ww:form></div></td>
            </tr>
            </tbody>
            </table>
		

                
                
	</c:otherwise>
</c:choose>





<table>
	<tr>
		<td><c:if test="${product.id > 0}">
			<div id="subItems">
			<div id="subItemHeader">
			<table cellspacing="0" cellpadding="0">
                <tr>
                    <td class="header">
                    Projects <ww:url id="createLink"
				action="createProject" includeParams="none">
				<ww:param name="productId" value="${product.id}" />
			</ww:url> <ww:a
				href="%{createLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
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
						sortProperty="totalEffortLeftSum.time"
						defaultorder="descending">
						<c:out value="${effLeftSums[row]}" />
					</display:column>

					<display:column sortable="true" title="Original estimate"
						sortProperty="totalOriginalEstimateSum.time"
						defaultorder="descending">
						<c:out value="${origEstSums[row]}" />
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

			<div id="subItemHeader">
			<table cellspacing="0" cellpadding="0">
                <tr>
                    <td class="header">
                    Backlog items <ww:url
				id="createBacklogItemLink" action="createBacklogItem"
				includeParams="none">
				<ww:param name="backlogId" value="${product.id}" />
			</ww:url> <ww:a
				href="%{createBacklogItemLink}&contextViewName=editProduct&contextObjectId=${product.id}">Create new &raquo;</ww:a>
				</td>
				</tr>
				</table>
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