<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct managementPageId="0"/>
<aef:menu navi="6"  pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
			<h2>View iteration: ${iteration.id}</h2>

		<c:if test="${iteration.id > 0}">
		<p>	
			<img src="drawChart.action?iterationId=${iteration.id}"/>
		</p>

		<p>
			<ww:url id="editIterationLink" action="editIteration" includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{editIterationLink}">Edit iteration</ww:a>		
		</p>
</c:if>

	<c:if test="${!empty iteration.backlogItems}"> 


		<p>
			Backlog items:
		</p>
		<p>
			<display:table name="iteration.backlogItems" id="row" requestURI="viewIteration.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="# of tasks">
					${fn:length(row.tasks)}
				</display:column>
				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

					<ww:form action="editTask">
						<ww:select name="taskId" list="#attr.row.tasks" listKey="id" listValue="name"/>					
						<ww:submit value="Go"/>
				    </ww:form>
				    </c:if>

				</display:column>
<%-- 
				<display:column sortable="true" title="Effort estimate" sortProperty="remainingEffortEstimate.time">
					${row.remainingEffortEstimate}
				</display:column>
				<display:column sortable="true" title="Effort in tasks" sortProperty="taskEffortLeft.time">
					${row.taskEffortLeft}
				</display:column>
--%>
				<display:column sortable="false" title="Assignee" >
					n/a
				</display:column>
				<display:column sortable="false" title="Status" >
					n/a
				</display:column>
				<display:column sortable="false" title="Priority" >
					n/a
				</display:column>
				<display:column sortable="false" title="Practices done" >
					n/a
				</display:column>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>
				</display:column>
			</display:table>
		</p>
	</c:if>

	<c:if test="${!empty iteration.iterationGoals}">
		<p>
			Iteration goals:
		</p>
		<p>
			<display:table name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="Description" property="description"/>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>
				</display:column>
			</display:table>
		</p>
	</c:if>

<%@ include file="./inc/_footer.jsp" %>
