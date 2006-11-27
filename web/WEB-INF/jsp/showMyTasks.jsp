<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>  <link rel="stylesheet" href="/agilefant/static/css/aef07.css" type="text/css">
<ww:head/>

</head>
<%@ include file="./inc/_header.jsp" %>
<%@ include file="./inc/_navi_left.jsp" %>
    <div id="upmenu">

      <li class="normal"><a>Help</a>

      </li>
    </div>
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Simple table displaying all tasks assigned to logged in user</h2>
   	<p>
   		<aef:currentUser/>   		
		<display:table name="${currentUser.assignments}" id="row" requestURI="myTasks.action">
			<display:column sortable="true" title="Id" property="id"/>
			<display:column sortable="true" title="Name" property="name"/>
			<display:column sortable="true" title="Effort left" sortProperty="effortEstimate.time">
				${row.effortEstimate}
			</display:column>
			<display:column sortable="true" title="Work performed" sortProperty="performedEffort.time">
				${row.performedEffort}
			</display:column>
			<display:column sortable="true" title="Created" property="created"/>
			<display:column sortable="true" title="Assignee">
				${row.assignee.fullName}
			</display:column>
			<display:column sortable="true" title="Creator">
				${row.creator.fullName}
			</display:column>
			<display:column sortable="false" title="Actions">
				<ww:url id="editLink" action="editTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:url id="deleteLink" action="deleteTask" includeParams="none">
					<ww:param name="taskId" value="${row.id}"/>
				</ww:url>
				<ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</display:column>
		</display:table>
	</p>
<%@ include file="./inc/_footer.jsp" %>
