<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>

<!-- Author:	aptoivon
	 Version:	1.3.1
-->

<aef:menu navi="administration" subnavi="themes" />
<ww:actionerror />
<ww:actionmessage />

<h2>Themes</h2>
<p><ww:url id="createBusinessThemeLink" action="createBusinessTheme" />
<ww:a href="%{createBusinessThemeLink}" onclick="return false;">Create new &raquo;</ww:a></p>

<p><c:choose>
	<c:when test="${empty businessThemes}">
				No themes were found.
			</c:when>
	<c:otherwise>
		<display:table class="listTable" name="${businessThemes}" id="row"
			requestURI="listThemes.action" defaultsort="1">
			<display:column sortable="true" title="Name" sortProperty="name">
				<ww:url id="editLink" action="editBusinessTheme">
					<ww:param name="businessThemeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>
			<display:column sortable="false" title="Action">
				<ww:url id="deleteLink" action="deleteBusinessTheme">
					<ww:param name="businessThemeId" value="${row.id}" />
				</ww:url>
				<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
					<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
				</ww:a>
			</display:column>
		</display:table>

	</c:otherwise>
</c:choose></p>

<%@ include file="../inc/_footer.jsp"%>