<%@ include file="./_taglibs.jsp"%>
<display:table class="listTable" name="${effortEntries}" id="row"
	defaultsort="1" defaultorder="ascending" style="width: 760px !important;">
	<display:column sortable="false" title="Date" sortProperty="date"
		style="white-space:nowrap; width: 105px;">
		<ww:date name="#attr.row.date" format="yyyy-MM-dd HH:mm" />
	</display:column>

	<display:column sortable="false" title="User" style="width: 40px;">
							${aef:html(row.user.initials)}
	</display:column>

	<display:column sortable="false" title="Spent effort" style="width: 70px;" 
		sortProperty="timeSpent">
							${aef:html(row.timeSpent)}
						</display:column>
	<display:column sortable="false" title="Comment" style="width: 250px;">
		<div class="spentEffortText"><c:out value="${row.description}" /></div>
	</display:column>
	<display:column sortable="false" title="Context" style="width: 220px;">
	<div style="white-space: nowrap; overflow: hidden; width: 220px;" class="spentEffortText">
		<c:if test="${row.backlogEffortEntry}">
			<c:set var="backlog" value="${row.backlog}" />
		</c:if>
		<c:if test="${row.taskEffortEntry}">
			<c:set var="backlog" value="${row.backlogItem.backlog}" />
			<c:out value="${row.backlogItem.name}" />
			<br />
		</c:if>
		<c:choose>
			<c:when test="${aef:isIteration(backlog)}">
        <c:out value="${backlog.project.product.name}" /> - 
        <c:out value="${backlog.project.name}" /> - 
				<c:out value="${backlog.name}" />
			</c:when>
			<c:when test="${aef:isProject(backlog)}">
        <c:out value="${backlog.product.name}" /> - 
				<c:out value="${backlog.name}" />
			</c:when>
			<c:when test="${aef:isProduct(backlog)}">
				<c:out value="${backlog.name}" />
			</c:when>
		</c:choose>
		</div>
	</display:column>
</display:table>