<%@ include file="_taglibs.jsp"%>
<div class="businessThemeTabsDiv">
<ul class="businessThemeTabs">

	<li><a href="#businessThemeEditTab-${businessThemeId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit theme</span></a></li>
	<li><a href="#businessThemeBLITab-${businessThemeId}"><span><img src="static/img/backlog.png" alt="Backlog items" /> Backlog items</span></a></li>
</ul>
<div id="businessThemeEditTab-${businessThemeId}" class="businessThemeNaviTab">
<ww:form action="ajaxStoreBusinessTheme" method="post">
	<ww:hidden name="businessThemeId" value="${businessTheme.id}" />
	<ww:hidden name="productId" value="${businessTheme.product.id}" />
	<ww:hidden name="businessTheme.active" value="${businessTheme.active}" />
	<table class="formTable">
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="20" name="businessTheme.name" maxlength="20" /></td>
		</tr>		
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="50" rows="7"
				name="businessTheme.description"/></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Save" /></td>
			<td class="deleteButton"><ww:reset value="Cancel" onclick="openEditThemeTabs('businessThemeTabContainer-${businessThemeId}', ${businessThemeId});"/></td>
		</tr>
	</table>
</ww:form>
</div>

<div id="businessThemeBLITab-${businessThemeId}" class="businessThemeNaviTab">

<display:table class="listTable" name="businessTheme.backlogItems" id="row" style="width:710px">

	<!-- Display name -->
	<display:column title="Name">
		<c:out value="${row.name}" />										
	</display:column>
	
	<!-- Display state -->
	<display:column title="State" sortable="false" class="taskColumn">
		<%@ include file="./_backlogItemStatusBar.jsp"%>					
	</display:column>
	
	<!-- Display context -->
	<display:column sortable="false" title="Context" class="contextColumn">
		<div><c:forEach items="${row.parentBacklogs}" var="parent">
			<c:choose>
				<c:when test="${aef:isIteration(parent)}">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<ww:url id="parentActionUrl" action="editIteration"
						includeParams="none">
						<ww:param name="iterationId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:when test="${aef:isProject(parent)}">					
					<ww:url id="parentActionUrl" action="editProject"
						includeParams="none">
						<ww:param name="projectId" value="${parent.id}" />
					</ww:url>
				</c:when>
				<c:otherwise>
					<ww:url id="parentActionUrl" action="editProduct"
						includeParams="none">
						<ww:param name="productId" value="${parent.id}" />
					</ww:url>
				</c:otherwise>
			</c:choose>
			<ww:a href="%{parentActionUrl}&contextViewName=dailyWork">
				<c:out value="${parent.name}" />
			</ww:a>
			<c:if test="${aef:isProject(parent)}">

			(<c:out value="${parent.projectType.name}" />)
			</c:if>
			<br />
		</c:forEach></div>
	</display:column>
	
</display:table>

</div>
</div>
