<%@ include file="./inc/_taglibs.jsp" %>
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
	<h2>Edit task</h2>
	<ww:form action="storeTask">
		<ww:hidden name="backlogItemId"/>
		<ww:hidden name="taskId" value="${task.id}"/>
		<p>		
			Name: <ww:textfield name="task.name"/>
		</p>
		<p>
			Description: <ww:richtexteditor name="task.description" width="600px" toolbarStartExpanded="false"/>
		</p>
		<p>		
			Effort left: <ww:textfield name="task.effortEstimate"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
<%@ include file="./inc/_footer.jsp" %>