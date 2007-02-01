<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct deliverableId="${deliverableId}"/>
<aef:menu navi="2"  pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>

					<ww:url id="viewIterationLink" action="viewIteration" includeParams="none">
						<ww:param name="iterationId" value="${iteration.id}"/>												
					</ww:url>
						<ww:a href="%{viewIterationLink}">View</ww:a> | Edit


					<aef:productList/>
		<ww:form action="editIteration">
			<p>
				<select name="iterationId">
					<c:forEach items="${productList}" var="product">
						<c:forEach items="${product.deliverables}" var="deliverable">
							<c:forEach items="${deliverable.iterations}" var="iter">
								<c:choose>
									<c:when test="${iteration.id == iter.id}">
										<option selected="selected" value="${iter.id}">${iter.name}</option>
									</c:when>
									<c:otherwise>
										<option value="${iter.id}">${iter.name}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:forEach>						
					</c:forEach>				
			<ww:submit value="Select iteration"/>
				</select>
			</p>			
		</ww:form>
				
	<ww:form action="storeIteration">
		<ww:hidden name="iterationId" value="${iteration.id}"/>
		<ww:hidden name="deliverableId"/> 
<%--

<ww:date name="%{new java.util.Date()}" format="dd-MM-yyyy" id="date"/>
<p>

			Startdate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="iteration.startDate"/> 
		</p>
		<p>		
			Enddate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="iteration.endDate"/> 
		</p>
		--%>
		<p>
			Start date: <ww:textfield name="iteration.startDate"/>
		</p>
		<p>
			End date: <ww:textfield name="iteration.endDate"/>
		</p>
    	<p>		
			Name: <ww:textfield name="iteration.name"/>
		</p>
		<p>
			Description: <ww:textarea cols="40" rows="6" name="iteration.description" />
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>


	</ww:form>	


		<c:if test="${iteration.id > 0}">


		<p>
			Backlog items:
		</p>
		<p>
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}">Add backlog item</ww:a>		
		</p>
</c:if>

	<c:if test="${!empty iteration.backlogItems}">

		<p>
			<display:table name="iteration.backlogItems" id="row" requestURI="editIteration.action">
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
<%-- 
				<display:column sortable="true" title="Effort estimate" sortProperty="remainingEffortEstimate.time">
					${row.remainingEffortEstimate}
				</display:column>
				<display:column sortable="true" title="Effort in tasks" sortProperty="taskEffortLeft.time">
					${row.taskEffortLeft}
				</display:column>
--%>
				<display:column sortable="false" title="Assignee" >
					${row.assignee.fullName}
				</display:column>
				<display:column sortable="false" title="Priority" >
					${row.priority}
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${row.iterationGoal.name}
				</display:column>
				<display:column sortable="true" title="Effort">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Estimate">
					${row.effortEstimate}
				</display:column>
				

				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>
				</display:column>
			  <display:footer>
			  	<tr>
			  		<td>Total:</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td><c:out value="${iteration.performedEffort}" /></td>
			  		<td><c:out value="${iteration.effortEstimate}" /></td>
			  	<tr>
			  </display:footer>				


				
			</display:table>
		</p>
	</c:if>
	

<c:if test="${iteration.id > 0}">


		<p>
			Iteration goals:
		</p>
		<p>
			<ww:url id="createIterationGoalLink" action="createIterationGoal" includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createIterationGoalLink}">Add iteration goal</ww:a>		
		</p>
</c:if>
	
	<c:if test="${!empty iteration.iterationGoals}">

		<p>
			<display:table name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Id" property="id"/>
				<display:column sortable="true" title="Name" property="name"/>
				<display:column sortable="true" title="Description" property="description"/>
				<display:column sortable="true" title="Priority" property="priority"/>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
						<ww:param name="iterationId" value="${iteration.id}"/>
					</ww:url>
					<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
				</display:column>
			</display:table>
		</p>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
