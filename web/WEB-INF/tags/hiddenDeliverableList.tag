<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Deliverable list" %>

   <%@attribute type="java.util.Collection" name="deliverables"%>
   <%@attribute name="divId"%>
   <%@attribute name="contextViewName"%>
   <%@attribute name="contextObjectId"%>


<div id="${divId}" style="display:none;">

	<ul  class="tasklist">
	<c:forEach items="${deliverables}" var="deliverable">
		<ww:url id="editLink" action="editDeliverable" includeParams="none">
			<ww:param name="deliverableId" value="${deliverable.id}"/>
		</ww:url>
		<li class="tasklistItem">
		<ww:a href="%{editLink}&contextViewName=${contextViewName}&contextObjectId=${contextObjectId}" title="${deliverable.name}">
			${aef:subString(deliverable.name, 30)}
		</ww:a>
		</li>								
	</c:forEach>
	</ul>
</div>
	
	