<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:choose>
	<c:when test="${!empty project.id}">
		<c:set var="currentProjectId" value="${project.id}" scope="page" />
		<c:if test="${project.id != previousProjectId}">
			<c:set var="previousProjectId" value="${project.id}" scope="session" />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="currentProjectId" value="${previousProjectId}"
			scope="page" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${project.id == 0}">
		<aef:bct productId="${productId}" />
	</c:when>
	<c:otherwise>
		<aef:bct projectId="${projectId}" />
	</c:otherwise>
</c:choose>

<c:set var="divId" value="1336" scope="page" />
<aef:menu navi="backlog" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />

<ww:date name="%{new java.util.Date()}" id="start"
	format="%{getText('webwork.shortDateTime.format')}" />
<ww:date name="%{new java.util.Date()}" id="end"
	format="%{getText('webwork.shortDateTime.format')}" />

<c:if test="${project.id > 0}">
	<ww:date name="%{project.startDate}" id="start"
		format="%{getText('webwork.shortDateTime.format')}" />
	<ww:date name="%{project.endDate}" id="end"
		format="%{getText('webwork.shortDateTime.format')}" />
</c:if>

<%--  TODO: fiksumpi virheenkäsittely --%>
<c:choose>
	<c:when test="${empty projectTypes}">
		<ww:url id="workTypeLink" action="projectPortfolio"
			includeParams="none" />	
				No project types available. <ww:a href="%{workTypeLink}">Add project types</ww:a>
	</c:when>
	<c:otherwise>
		<aef:productList />
		<c:choose>
			<c:when test="${projectId == 0}">
				<h2>Create project</h2>
			</c:when>
			<c:otherwise>
				<h2>Edit project</h2>
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${projectId == 0}">
				<c:set var="new" value="New" scope="page" />
			</c:when>
			<c:otherwise>
				<c:set var="new" value="" scope="page" />
			</c:otherwise>
		</c:choose>

		<ww:form action="store${new}Project">
			<ww:hidden name="projectId" value="${project.id}" />

			<table class="formTable">
				<tr>
					<td>Name</td>
					<td>*</td>
					<td colspan="2"><ww:textfield size="60" name="project.name" /></td>
				</tr>
				<tr>
					<td>Description</td>
					<td></td>
					<td colspan="2"><ww:textarea cols="70" rows="10"
						name="project.description" /></td>
				</tr>
				<tr>
					<td>Product</td>
					<td>*</td>
					<td colspan="2"><select name="productId">
						<option class="inactive" value="">(select product)</option>
						<c:forEach items="${productList}" var="product">
							<c:choose>
								<c:when test="${product.id == currentProductId}">
									<option selected="selected" value="${product.id}"
										title="${product.name}">${aef:out(product.name)}</option>
								</c:when>
								<c:otherwise>
									<option value="${product.id}" title="${product.name}">${aef:out(product.name)}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td>Project type</td>
					<td></td>
					<td colspan="2"><ww:select name="projectTypeId"
						list="#attr.projectTypes" listKey="id" listValue="name"
						value="${project.projectType.id}" /></td>
				</tr>
				<tr>
					<td>Start date</td>
					<td>*</td>
					<td colspan="2"><ww:datepicker value="%{#start}" size="15"
						showstime="true" format="%{getText('webwork.datepicker.format')}"
						name="startDate" /></td>
				</tr>
				<tr>
					<td>End date</td>
					<td>*</td>
					<td colspan="2"><ww:datepicker value="%{#end}" size="15" showstime="true"
						format="%{getText('webwork.datepicker.format')}" name="endDate" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td></td>
					<c:choose>
						<c:when test="${projectId == 0}">
							<td><ww:submit value="Create" /></td>
						</c:when>
						<c:otherwise>
							<td><ww:submit value="Save" /></td>
							<td class="deleteButton"> <ww:submit onclick="return confirmDelete()"
								action="deleteProject" value="Delete" /> </td>
						</c:otherwise>
					</c:choose>
				</tr>
			</table>
		</ww:form>
		<table>
			<tr>
				<td><c:if test="${project.id > 0}">
					<div id="subItems">

					<div id="subItemHeader">Iterations <ww:url id="createLink"
						action="createIteration" includeParams="none">
						<ww:param name="projectId" value="${project.id}" />
					</ww:url> <ww:a
						href="%{createLink}&contextViewName=editProject&contextObjectId=${project.id}">Create new &raquo;</ww:a>
					</div>
					<c:if test="${!empty project.iterations}">
						<div id="subItemContent">
						<p><display:table class="listTable" name="project.iterations"
							id="row" requestURI="editProject.action">

							<display:column sortable="true" sortProperty="name" title="Name"
								class="shortNameColumn">
								<ww:url id="editLink" action="editIteration"
									includeParams="none">
									<ww:param name="iterationId" value="${row.id}" />
								</ww:url>
								<ww:a
									href="%{editLink}&contextViewName=editProject&contextObjectId=${project.id}">
						${aef:html(row.name)}
					</ww:a>
							</display:column>

							<display:column sortable="true" title="Items">
								${fn:length(row.backlogItems)}
							</display:column>
							<%-- REFACTOR THIS --%>
							<display:column sortable="true" title="Effort left"
								sortProperty="bliEffortLeftSum.time">
								${row.totalEffortLeftSum}
							</display:column>

							<display:column sortable="true" title="Original estimate"
								sortProperty="bliOriginalEstimateSum.time">
								${row.totalOriginalEstimateSum}
							</display:column>


							<display:column sortable="true" title="Start date">
								<ww:date name="#attr.row.startDate" />
							</display:column>

							<display:column sortable="true" title="End date">
								<ww:date name="#attr.row.endDate" />
							</display:column>

							<display:column sortable="false" title="Actions">
								<ww:url id="deleteLink" action="deleteIteration"
									includeParams="none">
									<ww:param name="projectId" value="${project.id}" />
									<ww:param name="iterationId" value="${row.id}" />
								</ww:url>
								<ww:a href="%{deleteLink}&contextViewName=editProject&contextObjectId=${project.id}"
									onclick="return confirmDelete()">Delete</ww:a>
							</display:column>

						</display:table></p>
						</div>
					</c:if>
					<div id="subItemHeader">Backlog items <ww:url
						id="createBacklogItemLink" action="createBacklogItem"
						includeParams="none">
						<ww:param name="backlogId" value="${project.id}" />
					</ww:url> <ww:a
						href="%{createBacklogItemLink}&contextViewName=editProject&contextObjectId=${project.id}">Create new &raquo;</ww:a>
					</div>

					<c:if test="${!empty project.backlogItems}">
						<div id="subItemContent"><%@ include
							file="./inc/_backlogList.jsp"%></div>
					</c:if></div>
					<c:if test="${!empty project.backlogItems}">
					    <c:if test="${empty project.iterations}">
				            <p><img src="drawProjectChart.action?projectId=${project.id}" /></p>
			            </c:if>
			        </c:if>
				</c:if></td>
			</tr>
		</table>
	</c:otherwise>
</c:choose>

<%@ include file="./inc/_footer.jsp"%>