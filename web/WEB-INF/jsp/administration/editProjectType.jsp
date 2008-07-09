<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="projectTypes" pageHierarchy="${pageHierarchy}" />
<ww:actionerror />
<ww:actionmessage />
<c:choose>
	<c:when test="${projectType.id == 0}">
		<h2>Create project type</h2>
	</c:when>
	<c:otherwise>
		<h2>Edit project type</h2>
	</c:otherwise>
</c:choose>
<ww:form action="storeProjectType" method="post">
	<ww:hidden name="projectTypeId" value="${projectType.id}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="60" name="projectType.name" /></td>
		</tr>
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="10"
				name="projectType.description" cssClass="useWysiwyg" /></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<c:choose>
				<c:when test="${projectType.id == 0}">
					<td><ww:submit value="Create" /></td>
				</c:when>
				<c:otherwise>
					<td><ww:submit value="Save" /></td>
					<td class="deleteButton"> <ww:submit onclick="return confirmDelete()"
						action="deleteProjectType" value="Delete" /> </td>
				</c:otherwise>
			</c:choose>
		</tr>
	</table>
</ww:form>
<c:if test="${projectType.id > 0}">
	<table>
		<tr>
			<td>
			<div id="subItems">
			<div id="subItemHeader">Work types <ww:url id="createLink"
				action="createWorkType" includeParams="none">
				<ww:param name="projectTypeId" value="${projectType.id}" />
			</ww:url> <ww:a href="%{createLink}">Create new &raquo;</ww:a></div>
			<c:if test="${!empty projectType.workTypes}">
				<div id="subItemContent">
					<display:table class="listTable" defaultsort="1"
					name="${projectType.workTypes}" id="row"
					requestURI="editProjectType.action?projectTypeId=${projectType.id}">
					<display:column sortable="true" title="Name" sortProperty="name">
						<ww:url id="editLink" action="editWorkType" includeParams="none">
							<ww:param name="workTypeId" value="${row.id}" />
							<ww:param name="projectTypeId" value="${projectType.id}" />
						</ww:url>
						<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
					</display:column>
					<display:column sortable="false" title="Actions">
						<ww:url id="deleteLink" action="deleteWorkType"
							includeParams="none">
							<ww:param name="workTypeId" value="${row.id}" />
							<ww:param name="projectTypeId" value="${projectType.id}" />
						</ww:url>
						<!--<ww:a href="%{editLink}">Edit</ww:a>|-->
						<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
					</display:column>
				</display:table></div>
			</c:if></div>
			</td>
		</tr>
	</table>
</c:if>
<%@ include file="../inc/_footer.jsp"%>