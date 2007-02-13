<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct deliverableId="${deliverableId}"/>
<c:set var="did" value="1336" scope="page"/>
<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"/> 
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
		
		
		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield name="iteration.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="iteration.description" /></td>	
		</tr>
		<tr>
		<td>Start date</td>
		<td>*</td>
		<td><ww:textfield name="iteration.startDate"/></td>	
		</tr>
		<tr>
		<td>End date</td>
		<td>*</td>
		<td><ww:textfield name="iteration.endDate"/></td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>
			<ww:submit value="Store"/>
    		<ww:submit name="action:contextView" value="Cancel"/>
			
			</td>	
		</tr>
	</table>
	</ww:form>	


		<c:if test="${iteration.id > 0}">

<table><tr><td>
		<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">
	</c:if>



		<c:if test="${iteration.id > 0}">


		<p>Backlog items 
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}">Create new &raquo;</ww:a>		
		</p>
</c:if>

	<c:if test="${!empty iteration.backlogItems}">

		<p>
			<display:table class="listTable" name="iteration.backlogItems" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Name" property="name"/>

				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 


							<c:set var="did" value="${did + 1}" scope="page"/>
							
							
							<a href="javascript:toggleDiv(${did});" title="Click to expand">${fn:length(row.tasks)} tasks, summary etc...</a>
							<div id="${did}" style="display:none;">
							<c:forEach items="${row.tasks}" var="task">
							${aef:out(task.name)} - ${task.status}<br/>
							</c:forEach>
							</div>
							
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
                    <ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
                            <ww:param name="backlogItemId" value="${row.id}"/> 
                    </ww:url> 
                    <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a> 					
				</display:column>
			  <display:footer>
			  	<tr>
			  		<td>Total:</td>
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

		<p>Iteration goals
			<ww:url id="createIterationGoalLink" action="createIterationGoal" includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createIterationGoalLink}">Create new &raquo;</ww:a>		
		</p>
</c:if>
	
	<c:if test="${!empty iteration.iterationGoals}">

		<p>
			<display:table class="listTable"  name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
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
	
	
	
		<c:if test="${iteration.id > 0}">

</div>
</div>
</td></tr></table>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
