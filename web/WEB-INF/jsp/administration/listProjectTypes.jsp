<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>

<!-- Author:	aptoivon
	 Version:	1.3.1
-->

<aef:menu navi="administration" subnavi="projectTypes" />
<ww:actionerror />
<ww:actionmessage />


<h2>Project types</h2>
<p><ww:url id="createProjectTypeLink" action="createProjectType" />
<ww:a href="%{createProjectTypeLink}">Create new &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${empty projectTypes}">
				No project types were found.
			</c:when>
	<c:otherwise>
		<display:table class="listTable" name="${projectTypes}" id="row"
			requestURI="listProjectTypes.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<ww:url id="editLink" action="editProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Action">
				<ww:url id="deleteLink" action="deleteProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
					<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
				</ww:a>
			</display:column>
		</display:table>

	</c:otherwise>
</c:choose></p>


<%@ include file="../inc/_footer.jsp"%>