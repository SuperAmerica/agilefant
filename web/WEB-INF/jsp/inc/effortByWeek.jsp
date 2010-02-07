<%@ include file="./_taglibs.jsp"%>
<display:table class="listTable" name="${effortEntries}" id="row"
	defaultsort="1" defaultorder="ascending" style="width: 100%;">
	<display:column sortable="false" title="Date" sortProperty="date"
		style="width: 16ex;">
		<joda:format value="${row.date}" pattern="yyyy-MM-dd HH:mm" />
	</display:column>

	<display:column sortable="false" title="User" style="min-width: 8ex; width: auto;">
							${aef:html(row.user.initials)}
	</display:column>

	<display:column sortable="false" title="Spent effort" style="min-width: 10ex; width: auto;" 
		sortProperty="timeSpent">
							${aef:minutesToString(row.minutesSpent)}
						</display:column>
	<display:column sortable="false" title="Comment" style="width: auto;">
		<div class="spentEffortText"><c:out value="${row.description}" /></div>
	</display:column>
	<display:column sortable="false" title="Context" style="width: auto;">
	<div style="width: 220px;" class="spentEffortText">
		
		<c:if test="${row.class.name == 'fi.hut.soberit.agilefant.model.StoryHourEntry'}">
			<c:out value="${row.story.name}" /> - 
			<c:out value="${row.story.backlog.name}" />
		</c:if>
		<c:if test="${row.class.name == 'fi.hut.soberit.agilefant.model.TaskHourEntry'}">
		  <c:out value="${row.task.name}" /> -
      <c:choose>
        <c:when test="${row.task.story != null}">
          <c:out value="${row.task.story.name}" />
          <c:out value="${row.task.story.backlog.name}" />
        </c:when>
        <c:when test="${row.task.iteration != null}">
          <c:out value="${row.task.iteration.name}" />
        </c:when>
      </c:choose>
    </c:if>
    <c:if test="${row.class.name == 'fi.hut.soberit.agilefant.model.BacklogHourEntry'}">
      <c:out value="${row.backlog.name}" />
    </c:if>
		</div>
	</display:column>
</display:table>