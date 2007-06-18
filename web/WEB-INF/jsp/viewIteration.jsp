<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>			
<c:set var="divId" value="1336" scope="page"/>

		<c:choose>
			<c:when test="${!empty iteration.id}">
				<c:set var="currentIterationId" value="${iteration.id}" scope="page"/>
				<c:if test="${iteration.id != previousIterationId}">
					<c:set var="previousIterationId" value="${iteration.id}" scope="session"/>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:set var="currentIterationId" value="${previousIterationId}" scope="page"/>
			</c:otherwise>
		</c:choose>			

<aef:bct deliverableId="${deliverableId}"/>

<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>
		<p>
			<ww:url id="editIterationLink" action="editIteration" includeParams="none">
				<ww:param name="iterationId" value="${iteration.id}"/>
			</ww:url>
			View | <ww:a href="%{editIterationLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Edit </ww:a>		

		</p>
<aef:iterationmenu iterationId="${iteration.id}"/>
			

			
<c:if test="${!empty iteration}">
			
		<c:if test="${iteration.id > 0}">
			<h2>Iteration ${iteration.name}  <ww:date name="iteration.startDate" /> -  <ww:date name="iteration.endDate" /></h2>
			
<table><tr><td>

			<div id="subItems">
		<div id="subItemHeader">
			Subitems
		</div>
		<div id="subItemContent">
	</c:if>

	<p>Backlog items
		<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
			<ww:param name="backlogId" value="${iteration.id}"/>
		</ww:url>
		<ww:a href="%{createBacklogItemLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>
	</p>

	<c:if test="${!empty iteration.backlogItems}"> 

		<aef:currentUser/>

		<p>
			<display:table name="iteration.backlogItems" id="row" requestURI="viewIteration.action">
			<display:column sortable="true" title="Name">
				<ww:url id="editLink" action="editBacklogItem" includeParams="none">
					<ww:param name="backlogItemId" value="${row.id}"/>
				</ww:url>				
				<ww:a href="%{editLink}">
					${aef:html(row.name)}
				</ww:a>
			</display:column>


				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 

							<c:set var="divId" value="${divId + 1}" scope="page"/>
							
							<ww:a href="javascript:toggleDiv(${divId});" title="Click to expand">
								${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % done<br/>
   								<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
								<ww:url id="imgUrl" action="drawExtendedBarChart" includeParams="none">
									<ww:param name="notStarted"  value="${tsl['notStarted']}"/>
									<ww:param name="started"     value="${tsl['started']}"/>
									<ww:param name="blocked"     value="${tsl['blocked']}"/>
									<ww:param name="implemented" value="${tsl['implemented']}"/>
									<ww:param name="done"        value="${tsl['done']}"/>
								</ww:url>
			 					<img src="${imgUrl}"/> 
							</ww:a>
							<aef:tasklist tasks="${row.tasks}"  contextViewName="viewIteration"  contextObjectId="${iteration.id}" divId="${divId}"/>

							


				    </c:if>

				</display:column>
				<display:column sortable="true" title="Responsible" >
					${aef:html(row.assignee.fullName)}
				</display:column>
				<display:column sortable="true" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>

				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${aef:html(row.iterationGoal.name)}
				</display:column>
				<display:column sortable="true" title="Effort done">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Estimate">
					<c:choose>
						<c:when test="${!empty row.effortEstimate}">
							${row.effortEstimate}
						</c:when>
						<c:otherwise>
							${row.allocatedEffort}
						</c:otherwise>
					</c:choose>
				</display:column>
				

				<display:column sortable="false" title="Actions">
					<%--ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url--%>
                    <ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
                            <ww:param name="backlogItemId" value="${row.id}"/> 
                    </ww:url> 
                    <%--ww:a href="%{editLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Edit</ww:a>|--%>
                    <ww:a href="%{deleteLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Delete</ww:a> 					
					
				</display:column>
			  <display:footer>
			  	<tr>
			  		<td>Total:</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td>&nbsp;</td>
			  		<td><c:out value="${iteration.performedEffort}" /></td>
			  		<td><c:out value="${iteration.totalEstimate}" /></td>
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
				<ww:a href="%{createIterationGoalLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>		
			</p>
	</c:if>
	
	<c:if test="${!empty iteration.iterationGoals}">
		<p>
			<display:table name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Name">
					<ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>							
					<ww:a href="%{editLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">
						${aef:html(row.name)}
					</ww:a>
				</display:column>
				<%--display:column sortable="true" title="Status" >
					${row.status}
				</display:column--%>
				
				<display:column sortable="true" title="Description">
					${aef:html(row.description)}
				</display:column>
					
				<display:column sortable="true" title="Priority" property="priority"/>
				<display:column sortable="false" title="Actions">
					<%-- <ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>--%>
					<ww:url id="deleteLink" action="deleteIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
						<ww:param name="iterationId" value="${iteration.id}"/>
						<ww:param name="contextViewName" value="viewIteration"/>
					</ww:url>
					
					<ww:a href="%{deleteLink}">Delete</ww:a>
					<%--<ww:a href="%{editLink}&contextViewName=viewIteration&contextObjectId=${iteration.id}">Edit</ww:a>--%>
				</display:column>
			</display:table>
		</p>
	</c:if>

</c:if>


	
		<c:if test="${iteration.id > 0}">

</div>
</div>
</td></tr></table>

		<p>	
			<img src="drawChart.action?iterationId=${iteration.id}"/>
		</p>

	</c:if>

<%@ include file="./inc/_footer.jsp" %>
