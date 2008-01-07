<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<!-- Author:	aptoivon
	 Version:	1.3.1
-->

<aef:menu navi="portfolio" />
<ww:actionerror />
<ww:actionmessage />

<h2>Edit Project Rank</h2>

<h4>Ranked Projects</h4>
<p><display:table name="${ongoingRankedProjects}" id="row">
	<display:column title="Rank">
		<c:out value="${row_rowNum}" />
	</display:column>
	<display:column title="Project Name">
		<ww:a
			href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
			<c:out value="${row.product.name}: ${row.name}" />
		</ww:a>
	</display:column>
	<display:column title="Action">
		<ww:url id="moveTopLink" action="moveProjectTop">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveTopLink}"><img src="static/img/arrow_top.png" alt="Send to top" title="Send to top" /></ww:a>
		
		<ww:url id="moveUpLink" action="moveProjectUp">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveUpLink}"><img src="static/img/arrow_up.png" alt="Move up" title="Move up" /></ww:a>

		<ww:url id="moveDownLink" action="moveProjectDown">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveDownLink}"><img src="static/img/arrow_down.png" alt="Move down" title="Move down" /></ww:a>

		<ww:url id="moveBottomLink" action="moveProjectBottom">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{moveBottomLink}"><img src="static/img/arrow_bottom.png" alt="Send to bottom" title="Send to bottom" /></ww:a>
		
		<ww:url id="unrankLink" action="unrankProject">
			<ww:param name="projectId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{unrankLink}"><img src="static/img/unrank.png" alt="Unrank" title="Unrank" /></ww:a>
	</display:column>
</display:table></p>

<c:if test="${!empty ongoingUnrankedProjects}">
	<h4>Unranked Projects</h4>
	<p><display:table name="${ongoingUnrankedProjects}" id="row">
		<display:column title="Project Name">
			<ww:a
				href="contextView.action?contextObjectId=${row.id}&resetContextView=true&contextName=project">
				<c:out value="${row.product.name}: ${row.name}" />
			</ww:a>
		</display:column>
		<display:column title="Action">
			<ww:url id="moveTopLink" action="moveProjectTop">
				<ww:param name="projectId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{moveTopLink}"><img src="static/img/rank.png" alt="Rank to top" title="Rank to top" /></ww:a>
	
			<ww:url id="moveBottomLink" action="moveProjectBottom">
				<ww:param name="projectId" value="${row.id}" />
			</ww:url>
			<ww:a href="%{moveBottomLink}"><img src="static/img/unrank.png" alt="Rank to bottom" title="Rank to bottom" /></ww:a>
		</display:column>
	</display:table></p>
</c:if>

<h2>Project types</h2>
<p><ww:url id="createProjectTypeLink" action="createProjectType" />
<ww:a href="%{createProjectTypeLink}">Create new &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${empty projectTypes}">
				No project types were found.
			</c:when>
	<c:otherwise>
		<display:table class="listTable" name="${projectTypes}" id="row"
			requestURI="projectPortfolio.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<ww:url id="editLink" action="editProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Actions">
				<ww:url id="deleteLink" action="deleteProjectType">
					<ww:param name="projectTypeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">Delete</ww:a>
			</display:column>
		</display:table>

	</c:otherwise>
</c:choose></p>

<%@ include file="./inc/_footer.jsp"%>