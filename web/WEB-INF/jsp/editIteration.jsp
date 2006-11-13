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
	<c:choose>
		<c:when test="${iteration.id == 0}">
			<h2>Create new iteration</h2>
		</c:when>
		<c:otherwise>
			<h2>Edit iteration: ${iteration.id}</h2>
		</c:otherwise>
	</c:choose>
	<ww:form action="storeIteration">
		<ww:hidden name="iterationId" value="${iteration.id}"/>
		<ww:hidden name="deliverableId"/> 

<%--<ww:date name="%{new java.util.Date()}" format="dd-MM-yyyy" id="date"/>
<p>

			Startdate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="iteration.startDate"/> 
		</p>
		<p>		
			Enddate: <ww:datepicker value="%{#date}" showstime="%{true}" format="%d-%m-%Y" name="iteration.endDate"/> 
		</p>--%>
    	<p>		
			Name: <ww:textfield name="iteration.name"/>
		</p>
		<p>
			Description: <ww:richtexteditor name="iteration.description" width="600px" toolbarStartExpanded="false"/>
		</p>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>	

	<c:if test="${!empty iteration.backlogItems}">
		<p>
			Has backlog items:
		</p>
		<p>
		<ul>
		<c:forEach items="${iteration.backlogItems}" var="item">
			<ww:url id="editLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<ww:url id="deleteLink" action="deleteBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${item.id}"/>
			</ww:url>
			<li>
				${item.name} (${fn:length(item.tasks)} tasks) - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
			</li>
		</c:forEach>
		</ul>
		</p>
	</c:if>
<%@ include file="./inc/_footer.jsp" %>
