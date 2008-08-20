<%@ include file="./_taglibs.jsp"%>
<%-- Usage information: Remember to set the myAction variable before including this. --%>
<%-- Hour reporting, here be dragons --%>
<script type="text/javascript">
$(document).ready(function() {
	var allUsers = function() {
		var users = jsonDataCache.get("allUsers");
		var ret = {};
		jQuery.each(users,function() {if(this.enabled) {ret[this.id] = this.fullName; } });
		return ret;
	};
	$('#spentEffort-${myAction}').inlineTableEdit({
				  submit: '#saveSpentEffort-${myAction}',
				  useId: true,
				  fields: {
				  	efforts: {cell: 2, type: 'text'},
				  	dates: {cell: 0, type: 'date'},
				  	userIdss: {cell: 1, type: 'select', data: allUsers},
				  	descriptions: {cell: 3, type: 'text'},
				  	reset: {cell: 4, type: 'reset'}
				  	}
	});

});
</script>
<c:choose>
	<c:when test="${myAction == 'editBacklogItem'}">
		<aef:hourEntries id="hourEntries" target="${backlogItem}" />
	</c:when>
	<c:otherwise>
		<aef:hourEntries target="${backlog}" id="hourEntries" />
	</c:otherwise>
</c:choose>

<div class="subItems" style="margin-left: 3px;">
	<div class="subItemHeader">
     	<table cellpadding="0" cellspacing="0">
     	<tr>
     	<td class="header">
     	Logged effort
        <ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
        	<c:choose>
    	    	<c:when test="${myAction == 'editBacklogItem'}">
					<ww:param name="backlogItemId" value="${backlogItemId}" />
				</c:when>
				<c:otherwise>
					<ww:param name="backlogId" value="${backlog.id}" />
				</c:otherwise>
			</c:choose>
		</ww:url>
		<c:choose>
			<c:when test="${myAction == 'editBacklogItem'}">
				<ww:a cssClass="openCreateDialog openHourEntryDialog" href="%{createLink}&contextViewName=${myAction}&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>	
			</c:when>
			<c:otherwise>
				<ww:a cssClass="openCreateDialog openHourEntryDialog" href="%{createLink}&contextViewName=${myAction}&contextObjectId=${backlog.id}">Create new &raquo;</ww:a>
			</c:otherwise>
		</c:choose>
		</td>
		</tr>
		</table>
	</div>						
	<c:if test="${!empty hourEntries}">
		<div class="subItemContent validateWrapper validateEmpty">		
				<ww:form action="updateMultipleHourEntries.action" method="post">		
					<display:table class="listTable" htmlId="spentEffort-${myAction}" name="${hourEntries}" id="row" defaultsort="1" defaultorder="descending" requestURI="${myAction}.action">						
						<display:column sortable="false" title="Date" style="white-space:nowrap;">
							<ww:date name="#attr.row.date" format="yyyy-MM-dd HH:mm" />
						</display:column>
						
						<display:column sortable="false" title="User">
							<span style="display: none;">${row.user.id}</span>
							${aef:html(row.user.fullName)}
						</display:column>
						
						<display:column sortable="false" title="Spent effort" sortProperty="timeSpent">
							${aef:html(row.timeSpent)}
						</display:column>
						
						<display:column sortable="false" title="Comment">
							<c:out value="${row.description}"/>
						</display:column>
						
						<display:column sortable="false" title="Action">	
							<span class="uniqueId" style="display: none;">${row.id}</span>
							<ww:url id="deleteLink" action="deleteHourEntry" includeParams="none">
								<c:choose>
									<c:when test="${myAction == 'editBacklogItem'}">
										<ww:param name="backlogItemId" value="${backlogItem.id}" />
									</c:when>
									<c:otherwise>
										<ww:param name="backlogId" value="${backlog.id}" />
									</c:otherwise>
								</c:choose>
								<ww:param name="hourEntryId" value="${row.id}" />
							</ww:url>								
							<img src="static/img/edit.png" class="table_edit_edit" alt="Edit" title="Edit" />															
							<ww:a href="%{deleteLink}&contextViewName=${currentAction}&contextObjectId=${backlog.id}" onclick="return confirmDeleteHour()">
								<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
							</ww:a>								
						</display:column>
						</display:table>
					<input type="submit" value="Save" style="display: none;" id="saveSpentEffort-${myAction}" />
					</ww:form>
			</div>
		</c:if> <%-- No tasks --%>
	</div>

	
				