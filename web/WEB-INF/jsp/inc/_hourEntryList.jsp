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
				  deleteaction: 'deleteHourEntry.action',
                  submitParam: 'hourEntryId',
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
<aef:hourEntries target="${backlog}" id="hourEntries" />

<div class="subItems" style="margin-left: 3px;" id="subItems_hourEntryListItems">
	<div class="subItemHeader">
     	<table cellpadding="0" cellspacing="0">
     	<tr>
     	<td class="header">
     	Logged effort
        <ww:url id="createLink" action="ajaxCreateHourEntry" includeParams="none">
		<ww:param name="backlogId" value="${backlog.id}" />
		</ww:url>
		<ww:a cssClass="openCreateDialog openHourEntryDialog" href="%{createLink}" onclick="return false;">Log effort &raquo;</ww:a>
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
							<img src="static/img/edit.png" class="table_edit_edit" alt="Edit" title="Edit" />															
							<img src="static/img/delete_18.png" alt="Delete" title="Delete" class="table_edit_delete" style="cursor: pointer;"/>								
						</display:column>
						</display:table>
					<input type="submit" value="Save" style="display: none;" id="saveSpentEffort-${myAction}" />
					</ww:form>
			</div>
		</c:if> <%-- No tasks --%>
	</div>

	
				