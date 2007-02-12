<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct iterationId="${iterationId}"/>
<aef:menu navi="${contextName }"  pageHierarchy="${pageHierarchy}"/> 

	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit iteration goal</h2>
	<ww:form action="storeIterationGoal">
		<ww:hidden name="iterationId"/>
		<ww:hidden name="iterationGoalId" value="${iterationGoal.id}"/>
		<table class="formTable">
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield name="iterationGoal.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="iterationGoal.description" /></td>	
		</tr>
		<tr>
		<td>Priority</td>
		<td></td>
		<td><ww:textfield name="iterationGoal.priority" value="${iterationGoal.priority }"/></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td><ww:submit value="Store"/><ww:submit value="Cancel" action="editIteration"/></td>	
		</tr>
		</table>


	</ww:form>

					<aef:productList/>

<c:if test="${iterationGoal.id > 0}">
		<ww:form action="moveIterationGoal">
		<ww:hidden name="iterationGoalId"/>
			<p>
			Move to iteration:
				<select name="iterationId">
					<c:forEach items="${productList}" var="product">
						<c:forEach items="${product.deliverables}" var="deliverable">
							<c:forEach items="${deliverable.iterations}" var="iter">
								<c:choose>
									<c:when test="${iterationId == iter.id}">
										<option selected="selected" value="${iter.id}">${iter.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${iter.id}">${iter.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:forEach>						
					</c:forEach>				
			<ww:submit value="Move"/>
				</select>
			</p>			
		</ww:form>
				

</c:if>
	<c:if test="${!empty iterationGoal.backlogItems}">
		<p>
			Backlog items:
		</p>
		<p>
			<display:table class="listTable" name="iterationGoal.backlogItems" id="row" requestURI="editIterationGoal.action">
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
