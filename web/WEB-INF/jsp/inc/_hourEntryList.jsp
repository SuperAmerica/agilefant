<%@ include file="./_taglibs.jsp"%>
<%-- Usage information: Remember to set the myAction variable before including this. --%>
<%-- Hour reporting, here be dragons --%>
<c:choose>
	<c:when test="${myAction == 'editBacklogItem'}">
		<aef:hourEntries id="hourEntries" target="${backlogItem}" />
	</c:when>
	<c:otherwise>
		<aef:hourEntries target="${backlog}" id="hourEntries" />
	</c:otherwise>
</c:choose>

<div class="subItems" style="margin-left: 3px;">
	<div class="subItemHeader" style="padding: 3px !important;">
     	Logged effort
        <ww:url id="createLink" action="createHourEntry" includeParams="none">
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
				<ww:a cssClass="openModalWindow" href="%{createLink}&contextViewName=${myAction}&contextObjectId=${backlogItemId}">Create new &raquo;</ww:a>	
			</c:when>
			<c:otherwise>
				<ww:a cssClass="openModalWindow" href="%{createLink}&contextViewName=${myAction}&contextObjectId=${backlog.id}">Create new &raquo;</ww:a>
			</c:otherwise>
		</c:choose>
	</div>						
	<c:if test="${!empty hourEntries}">
		<div id="subItemContent">		
			<p>
				<display:table name="${hourEntries}" id="row" defaultsort="1" defaultorder="descending" requestURI="${myAction}.action">
					
					<display:column sortable="true" title="Date">
						<ww:date name="#attr.row.date" format="yyyy-MM-dd HH:mm" />
					</display:column>
					
					<display:column sortable="true" title="User">
						${aef:html(row.user.fullName)}
					</display:column>
					
					<display:column sortable="false" title="Spent effort">
						${aef:html(row.timeSpent)}
					</display:column>
					
					<display:column sortable="false" title="Comment">
						<c:out value="${row.description}"/>
					</display:column>

					<%-- Not implemented yet --%>
						<display:column sortable="false" title="Action">
							<ww:url id="editLink" action="editHourEntry" includeParams="none">
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
							<c:choose>	
								<c:when test="${myAction == 'editBacklogItem'}">		
									<ww:a cssClass="openModalWindow" href="%{editLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}">
										<img src="static/img/edit.png" alt="Edit" title="Edit" />
									</ww:a>
									<ww:a href="%{deleteLink}&contextViewName=editBacklogItem&contextObjectId=${backlogItemId}" onclick="return confirmDeleteHour()">
										<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
									</ww:a>
								</c:when>
								<c:otherwise>
									<ww:a cssClass="openModalWindow" href="%{editLink}&contextViewName=${myAction}&contextObjectId=${backlog.id}">
										<img src="static/img/edit.png" alt="Edit" title="Edit" />
									</ww:a>
									<ww:a href="%{deleteLink}&contextViewName=${myAction}&contextObjectId=${backlog.id}" onclick="return confirmDeleteHour()">
										<img src="static/img/delete_18.png" alt="Delete" title="Delete" />
									</ww:a>
								</c:otherwise>
							</c:choose>
						</display:column>
					</display:table>
				</p>
			</div>
		</c:if> <%-- No tasks --%>
	</div>
<aef:modalAjaxWindow/>
