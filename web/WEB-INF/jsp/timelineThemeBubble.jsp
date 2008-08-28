<%@ include file="./inc/_taglibs.jsp"%>
<div class="timelineBubble">
<div class="bubbleTitle" style="white-space: normal; !important"><c:out value="${businessTheme.name}" /> - <c:out value="${businessTheme.description}" /></div>
<display:table class="listTable" name="businessTheme.backlogBindings" id="row" style="width:350px">
	<!-- Display name -->
	<display:column title="Name" style="width:355px">
		<c:if test="${aef:isIteration(row.backlog)}">
			<ww:url id="editProjLink" action="editProject" includeParams="none">
				<ww:param name="projectId" value="${row.backlog.parent.id}" />
			</ww:url>
			<ww:a href="%{editProjLink}">						
				<c:out value="${row.backlog.parent.name}"/>
			</ww:a>&nbsp;-&nbsp;
		</c:if>
		<ww:url id="editLink" action="editIteration" includeParams="none">
			<ww:param name="iterationId" value="${row.backlog.id}" />
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