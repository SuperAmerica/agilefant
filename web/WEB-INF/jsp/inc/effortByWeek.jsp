<%@ include file="./_taglibs.jsp"%>
<display:table class="listTable"
	name="${effortEntries}" id="row" defaultsort="1"
	defaultorder="descending">
	<display:column sortable="false" title="Date"
		style="white-space:nowrap;">
		<ww:date name="#attr.row.date" format="yyyy-MM-dd HH:mm" />
	</display:column>

	<display:column sortable="false" title="User">
		<span style="display: none;">${row.user.id}</span>
							${aef:html(row.user.fullName)}
						</display:column>

	<display:column sortable="false" title="Spent effort"
		sortProperty="timeSpent">
							${aef:html(row.timeSpent)}
						</display:column>

	<display:column sortable="false" title="Comment">
		<c:out value="${row.description}" />
	</display:column>
	<display:column sortable="false" title="Context">
    TODO
	</display:column>
</display:table>