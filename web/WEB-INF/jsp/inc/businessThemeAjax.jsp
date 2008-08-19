<%@ include file="_taglibs.jsp"%>
<div class="ajaxWindowTabsDiv">
<ul class="ajaxWindowTabs">

	<li><a href="#businessThemeEditTab-${businessThemeId}"><span><img src="static/img/edit.png" alt="Edit" /> Edit theme</span></a></li>
	<li><a href="#businessThemeBLITab-${businessThemeId}"><span><img src="static/img/bli.png" alt="Backlog items" /> Backlog items</span></a></li>
	<li><a href="#businessThemeBLTab-${businessThemeId}"><span><img src="static/img/backlog.png" alt="Backlogs" /> Backlogs</span></a></li>
</ul>
<div id="businessThemeEditTab-${businessThemeId}" class="businessThemeNaviTab validateWrapper validateTheme">

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
			<td class="deleteButton"><ww:reset value="Cancel" onclick="closeTabs('themes', 'businessThemeTabContainer-${businessThemeId}', ${businessThemeId});"/></td>
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
		<ww:url id="editLink" action="editBacklogItem" includeParams="none">
			<ww:param name="backlogItemId" value="${row.id}" />
		</ww:url>						
		<ww:a href="%{editLink}&contextObjectId=${businessTheme.product.id}&contextViewName=${currentAction}">
			${aef:html(row.name)}
		</ww:a>												
	</display:column>
	
	<!-- Display state -->
	<display:column title="State" sortable="false" class="taskColumn" style="width:115px">
		<c:choose>
	<c:when test="${!(empty row.tasks)}">		
			${fn:length(row.tasks)} tasks,
			<aef:percentDone backlogItemId="${row.id}" />% done<br />
	</c:when>
	<c:otherwise>		
			<ww:text name="backlogItem.state.${row.state}"/><br />
	</c:otherwise>
	</c:choose>														
			<c:choose>
				<c:when test="${row.state == 'NOT_STARTED'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="notStarted" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
				<c:when test="${row.state == 'STARTED'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="started" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
				<c:when test="${row.state == 'PENDING'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="pending" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
				<c:when test="${row.state == 'BLOCKED'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="blocked" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
				<c:when test="${row.state == 'IMPLEMENTED'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="implemented" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
					<c:when test="${row.state == 'DONE'}" >
					<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
					<ww:param name="done" value="1" /> </ww:url> 
					<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
				</c:when>
			</c:choose>
							
			<c:choose>
				<c:when test="${!(empty row.tasks)}">					
						<aef:stateList backlogItemId="${row.id}" id="tsl" /> 
						<ww:url
							id="imgUrl" action="drawExtendedBarChart" includeParams="none">
								<ww:param name="notStarted" value="${tsl['notStarted']}" />
								<ww:param name="started" value="${tsl['started']}" />
								<ww:param name="pending" value="${tsl['pending']}" />
								<ww:param name="blocked" value="${tsl['blocked']}" />
								<ww:param name="implemented" value="${tsl['implemented']}" />
								<ww:param name="done" value="${tsl['done']}" />
						</ww:url> 
						<div style="margin:0px auto;background-image:url(${imgUrl}); background-position: -16px -4px; height:8px; width:82px; background-repeat:no-repeat;border-right:1px solid #BFBFBF; "></div>
					
					
					
				</c:when>
			</c:choose>					
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
No backlog items have been tagged with this theme.
</c:otherwise>
</c:choose>

</div>
<div id="businessThemeBLTab-${businessThemeId}" class="businessThemeNaviTab">
<display:table class="listTable" name="businessTheme.backlogBindings" id="row" style="width:700px">
	<!-- Display name -->
	<display:column title="Name" style="width:355px">
		<c:out value="${row.backlog.name}" />											
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
