<%@ include file="_taglibs.jsp"%>
<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">

	<li><a href="#businessThemeEditTab-${businessThemeId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit theme</span></a></li>
	<li><a href="#businessThemeBLITab-${businessThemeId}"><span><img src="static/img/bli2.png" alt="Backlog items" /> Backlog items</span></a></li>
	<li><a href="#businessThemeBLTab-${businessThemeId}"><span><img src="static/img/backlog.png" alt="Backlogs" /> Backlogs</span></a></li>
</ul>
<div id="businessThemeEditTab-${businessThemeId}" class="businessThemeNaviTab validateWrapper validateTheme">

<ww:form action="ajaxStoreBusinessTheme" method="post">
	<ww:hidden name="businessThemeId" value="${businessTheme.id}" />
	<c:choose>
	   <c:when test="${businessTheme.product == null}">
	       <ww:hidden name="productId" value="-1" />
	   </c:when>
	   <c:otherwise>
	       <ww:hidden name="productId" value="${businessTheme.product.id}" />
	   </c:otherwise>
	</c:choose>
	<ww:hidden name="businessTheme.active" value="${businessTheme.active}" />
	<table class="formTable">
		<tr>
			<td><ww:text name="general.uniqueId"/></td>
			<td></td>
			<td><aef:quickReference item="${businessTheme}" /></td>
		</tr>
		<tr>
			<td>Name</td>
			<td>*</td>
			<td colspan="2"><ww:textfield size="20" name="businessTheme.name" maxlength="20" /></td>
		</tr>		
		<tr>
			<td>Description</td>
			<td></td>
			<td colspan="2"><ww:textarea cols="70" rows="7" cssClass="useWysiwyg" 
				name="businessTheme.description" value="${aef:nl2br(businessTheme.description)}"/></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
			<td><ww:submit value="Save" /></td>
			<td class="deleteButton"><ww:submit value="Delete" action="deleteBusinessTheme" /><ww:reset value="Cancel"/></td>
		</tr>
	</table>
</ww:form>
</div>

<div id="businessThemeBLITab-${businessThemeId}" class="businessThemeNaviTab">

<c:choose>
<c:when test="${!(empty businessTheme.backlogItems)}" >
<display:table class="listTable" name="businessTheme.backlogItems" id="row" style="width:700px">

	<!-- Display name -->
	<display:column title="Name" style="width:355px">
			${aef:html(row.name)}
	</display:column>
	
	<!-- Display progress -->
	<display:column title="Progress" sortable="false" class="todoColumn" style="width:115px">
		<aef:backlogItemProgressBar backlogItem="${row}" bliListContext="${bliListContext}" dialogContext="${dialogContext}" hasLink="${false}"/>
	</display:column>
	
	<!-- Display context -->
	<display:column sortable="false" title="Context" >
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
					&nbsp;&nbsp;&nbsp;					
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
			<ww:a href="%{parentActionUrl}&contextViewName=${currentAction}">
				<c:out value="${parent.name}" />
			</ww:a>
			<c:if test="${aef:isProject(parent)}">

			(<c:out value="${parent.projectType.name}" />)
			</c:if>
			<br />
		</c:forEach></div>
	</display:column>
	
</display:table>
</c:when>
<c:otherwise>
No stories have been tagged with this theme.
</c:otherwise>
</c:choose>

</div>
<div id="businessThemeBLTab-${businessThemeId}" class="businessThemeNaviTab">
<display:table class="listTable" name="businessTheme.backlogBindings" id="row" style="width:700px">
	<!-- Display name -->
	<display:column title="Name" style="width:355px">
		<c:if test="${aef:isIteration(row.backlog)}">
			<ww:url id="editProjLink" action="editBacklog" includeParams="none">
				<ww:param name="backlogId" value="${row.backlog.project.id}" />
			</ww:url>
			<ww:a href="%{editProjLink}">						
				<c:out value="${row.backlog.project.name}"/>
			</ww:a>&nbsp;-&nbsp;
		</c:if>
		<ww:url id="editLink" action="editBacklog" includeParams="none">
			<ww:param name="backlogId" value="${row.backlog.id}" />
		</ww:url>
		<ww:a href="%{editLink}">						
			<c:out value="${row.backlog.name}"/>
		</ww:a>										
	</display:column>
	<display:column title="Allocation" style="width:100px">
	<c:choose>
		<c:when test="${row.relativeBinding == true}">
			<span style="display:none;">${row.percentage}</span>
			<c:out value="${row.boundEffort}"/>
			(<c:out value="${row.percentage}"/>%)
		</c:when>
		<c:otherwise><c:out value="${row.fixedSize}"/></c:otherwise>
	</c:choose>
	</display:column>
</display:table>

</div>
</div>
