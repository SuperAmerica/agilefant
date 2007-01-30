<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct iterationId="${iterationId}"/>
<aef:menu navi="1"  pageHierarchy="${pageHierarchy}"/> 

	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit iteration goal</h2>
	<ww:form action="storeIterationGoal">
		<ww:hidden name="iterationId"/>
		<ww:hidden name="iterationGoalId" value="${iterationGoal.id}"/>
		<p>		
			Name: <ww:textfield name="iterationGoal.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="iterationGoal.description" />
		</p>
		<p>		
			Priority: <ww:textfield name="iterationGoal.priority" value="${iterationGoal.priority }"/>
		</p>
		<p>
			<ww:submit value="Store"/><ww:submit value="Cancel" action="editIteration"/>
		</p>
	</ww:form>

	<c:if test="${!empty iterationGoal.backlogItems}">
		<p>
			Backlog items:
		</p>
		<p>
			<display:table name="iterationGoal.backlogItems" id="row" requestURI="editIterationGoal.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>

				<display:column sortable="true" title="Watched by me" >
					<c:choose>
						<c:when test="${empty row.watchers[currentUser.id]}">
							Yes
						</c:when>
						<c:otherwise>
							No
						</c:otherwise>
					</c:choose>
				</display:column>



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

				<display:column sortable="false" title="Assignee" >
					${row.assignee.fullName}
				</display:column>
				<display:column sortable="false" title="Priority" >
					${row.priority}
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${row.iterationGoal.name}
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
<%@ include file="./inc/_footer.jsp" %>
