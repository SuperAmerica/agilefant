<%@ include file="./inc/_taglibs.jsp" %>
<%@ include file="./inc/_taglibs.jsp" %>
<html>
<head>  <link rel="stylesheet" href="/agilefant/static/css/aef07.css" type="text/css">
</head>
<%@ include file="./inc/_header.jsp" %>
<%@ include file="./inc/_navi_left.jsp" %>
    <div id="upmenu">

      <li class="normal"><a>Help</a>

      </li>
    </div>
	<ww:actionerror/>
	<ww:actionmessage/>
	<h2>Edit Deliverable</h2>
	<ww:form action="storeDeliverable">
		<ww:hidden name="deliverableId" value="${deliverable.id}"/>
		<ww:hidden name="productId"/>
 		<p>		
			Name: <ww:textfield name="deliverable.name"/>
		</p>
		<p>
			Description: <ww:richtexteditor name="deliverable.description" width="600px" toolbarStartExpanded="false"/>
		</p>
		<c:if test="${deliverable.id > 0}">
			<h3>Iterations</h3>
			<p>
				<!-- todo: make consistent with new terminology? iteration -> iteration -->
				<c:forEach items="${deliverable.iterations}" var="iteration">
				<p>
					<ww:url id="editLink" action="editIteration" includeParams="none">
						<ww:param name="iterationId" value="${iteration.id}"/>
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
					</ww:url>
					<ww:url id="deleteLink" action="deleteIteration" includeParams="none">
						<ww:param name="deliverableId" value="${deliverable.id}"/>						
						<ww:param name="iterationId" value="${iteration.id}"/>
					</ww:url>
					${iteration.name} - <ww:a href="%{editLink}">Edit</ww:a>|<ww:a href="%{deleteLink}">Delete</ww:a>
					</p>
				</c:forEach>
			</p>
			<p>
 				<ww:url id="createLink" action="createIteration" includeParams="none">
					<ww:param name="deliverableId" value="${deliverable.id}"/>
				</ww:url>
				<ww:a href="%{createLink}">Create new</ww:a>
			</p>
		</c:if>
		<p>
			<ww:submit value="Store"/>
		</p>
	</ww:form>
	<c:if test="${!empty deliverable.backlogItems}">
		<p>
			Has backlog items:
		</p>
		<p>
		<ul>
		<c:forEach items="${deliverable.backlogItems}" var="item">
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
