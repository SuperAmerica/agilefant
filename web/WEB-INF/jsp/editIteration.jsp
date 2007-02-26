<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_header.jsp" %>
<aef:bct deliverableId="${deliverableId}"/>
<c:set var="divId" value="1336" scope="page"/>
<aef:menu navi="${contextName}"  pageHierarchy="${pageHierarchy}"/> 
	<ww:actionerror/>
	<ww:actionmessage/>

					<ww:url id="viewIterationLink" action="viewIteration" includeParams="none">
						<ww:param name="iterationId" value="${iteration.id}"/>												
					</ww:url>
						<ww:a href="%{viewIterationLink}">View</ww:a> | Edit

<aef:iterationmenu iterationId="${iteration.id}"/>
				
	<ww:form action="storeIteration">
		<ww:hidden name="iterationId" value="${iteration.id}"/>
		<ww:hidden name="deliverableId"/> 

<ww:date name="%{new java.util.Date()}" id="start"/>
<ww:date name="%{new java.util.Date()}" id="end"/>
		
<c:if test="${iteration.id > 0}">
<ww:date name="%{iteration.startDate}" id="start"/>
<ww:date name="%{iteration.endDate}" id="end"/>

</c:if>

<h2>Iteration</h2>

		
		<table class="formTable">
		<tr>
		<td></td>
		<td></td>
		<td></td>	
		</tr>
		<tr>
		<td>Name</td>
		<td>*</td>
		<td><ww:textfield size="53" name="iteration.name"/></td>	
		</tr>
		<tr>
		<td>Description</td>
		<td></td>
		<td><ww:textarea cols="40" rows="6" name="iteration.description" /></td>	
		</tr>
		<tr>
		<td>Start date</td>
		<td>*</td>
		<td>
			    <ww:datepicker value="%{#start}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="iteration.startDate" /> 
		
		</td>	
		</tr>
		<tr>
		<td>End date</td>
		<td>*</td>
		<td>
			    <ww:datepicker value="%{#end}" size="10" showstime="%{true}"  format="%{getText('webwork.datepicker.format')}" name="iteration.endDate" /> 
		
		</td>	
		</tr>
		<tr>
		<td></td>
		<td></td>
		<td>
			<ww:submit value="Store"/>
    		<ww:submit name="action:popContext" value="Cancel"/>
			
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

		<p>Backlog items 
			<ww:url id="createBacklogItemLink" action="createBacklogItem" includeParams="none">
				<ww:param name="backlogId" value="${iteration.id}"/>
			</ww:url>
			<ww:a href="%{createBacklogItemLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>		
		</p>
</c:if>

	<c:if test="${!empty iteration.backlogItems}">

		<p>
			<display:table class="listTable" name="iteration.backlogItems" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Name">
					${aef:outTitle(row.name)}
				</display:column>

				<display:column title="Tasks" sortable="false">
				<c:if test="${!empty row.tasks}"> 


							<c:set var="divId" value="${divId + 1}" scope="page"/>
							<a href="javascript:toggleDiv(${divId});" title="Click to expand">
								${fn:length(row.tasks)} tasks, <aef:percentDone backlogItemId="${row.id}"/> % complete<br/>
   								<aef:taskStatusList backlogItemId="${row.id}" id="tsl"/>							   
 	<img src="drawExtendedBarChart.action?notStarted=${tsl['notStarted']}&started=${tsl['started']}&blocked=${tsl['started']}&implemented=${tsl['implemented']}&done=${tsl['done']}"/> 
							</a>
							
							<aef:tasklist tasks="${row.tasks}"   contextViewName="editIteration"  contextObjectId="${iteration.id}" divId="${divId}"/>
							
							
							</c:if>

				</display:column>
				<display:column sortable="false" title="Assignee" >
					${aef:out(row.assignee.fullName)}
				</display:column>
				<display:column sortable="false" title="Priority" >
				<ww:text name="backlogItem.priority.${row.priority}"/>
				</display:column>
				<display:column sortable="true" title="Iteration Goal">
					${aef:out(row.iterationGoal.name)}
				</display:column>
				<display:column sortable="true" title="Effort done">
					${row.performedEffort}
				</display:column>
				<display:column sortable="true" title="Estimate">
					<c:choose>
						<c:when test="!empty ${row.effortEstimate}">
							${row.effortEstimate}
						</c:when>
						<c:otherwise>
							${row.allocatedEffort}
						</c:otherwise>
					</c:choose>
				</display:column>
				

				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editBacklogItem" includeParams="none">
						<ww:param name="backlogItemId" value="${row.id}"/>
					</ww:url>
                    <ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none"> 
                            <ww:param name="backlogItemId" value="${row.id}"/> 
                    </ww:url> 
                    <ww:a href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Edit</ww:a>|<ww:a href="%{deleteLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Delete</ww:a> 					
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
			<ww:a href="%{createIterationGoalLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Create new &raquo;</ww:a>		
		</p>
</c:if>
	
	<c:if test="${!empty iteration.iterationGoals}">

		<p>
			<display:table class="listTable"  name="iteration.iterationGoals" id="row" requestURI="editIteration.action">
				<display:column sortable="true" title="Name">
					${aef:outTitle(row.name)}
				</display:column>
					
				<display:column sortable="true" title="Description" >
					${aef:out(row.description)}
				</display:column>
					
				<display:column sortable="true" title="Priority" property="priority"/>
				<display:column sortable="false" title="Actions">
					<ww:url id="editLink" action="editIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
					</ww:url>
					<ww:url id="deleteLink" action="deleteIterationGoal" includeParams="none">
						<ww:param name="iterationGoalId" value="${row.id}"/>
						<ww:param name="iterationId" value="${iteration.id}"/>
					</ww:url>
					<ww:a href="%{editLink}&contextViewName=editIteration&contextObjectId=${iteration.id}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
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
