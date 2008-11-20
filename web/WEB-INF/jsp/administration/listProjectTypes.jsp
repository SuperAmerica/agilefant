<%@ include file="../inc/_taglibs.jsp"%>
<%@ include file="../inc/_header.jsp"%>
<aef:menu navi="administration" subnavi="projectTypes" title="Project Types"/>
<aef:openDialogs context="projectType" id="openProjectTypeTabs" />

<script type="text/javascript">
$(document).ready(function() {        
    <c:forEach items="${openProjectTypeTabs}" var="openProjectType">
        handleTabEvent("projectTypeTabContainer-${openProjectType[0]}", "projectType", ${openProjectType[0]}, ${openProjectType[1]});
    </c:forEach>
});
</script>

<ww:actionerror />
<ww:actionmessage />

<h2>Project types</h2>

<div class="subItems" style="width: 545px;" id="subItems_projectType">
<div class="subItemHeader">
<table cellspacing="0" cellpadding="0">
	<tr>
		<td class="header">Project types					
			<a href="ajaxCreateProjectType.action" class="openCreateDialog openProjectTypeDialog" title="Create a project type" onclick="return false;">Create new &raquo;</a>
		</td>
	</tr>
</table>
</div>
<div class="subItemContent">
<c:if test="${(!empty projectTypes)}">
<display:table class="listTable" name="${projectTypes}" id="row"
	requestURI="listProjectTypes.action" defaultsort="1">
	<display:column sortable="true" title="Name" sortProperty="name" class="projectTypeNameColumn">
	   <div style="width: 400px;">
		<a class="nameLink"
			onclick="handleTabEvent('projectTypeTabContainer-${row.id}', 'projectType', ${row.id}, 0); return false;">
			${aef:html(row.name)}
		</a>
		<div id="projectTypeTabContainer-${row.id}" style="overflow: visible; white-space: nowrap; width: 0px;"></div>
	   </div>						
	</display:column>
	
	<display:column sortable="false" title="Action" style="width: 50px;">
		<img src="static/img/edit.png" alt="Edit" title="Edit" style="cursor: pointer;" onclick="handleTabEvent('projectTypeTabContainer-${row.id}', 'projectType', ${row.id}, 0); return false;" />
		<ww:url id="deleteLink" action="deleteProjectType">
			<ww:param name="projectTypeId" value="${row.id}" />
		</ww:url>
		<ww:a href="%{deleteLink}" onclick="return confirmDelete()">
			<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
		</ww:a>
	</display:column>
</display:table>
</c:if>
</div>
</div>

<%@ include file="../inc/_footer.jsp"%>