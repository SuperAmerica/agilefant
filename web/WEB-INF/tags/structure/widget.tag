<%@ include file="../../jsp/inc/_taglibs.jsp" %>
<%@tag description="Widget box" %>

<%@attribute name="name" fragment="false" required="true" %>
<%@attribute name="widgetId" fragment="false" required="true" %>
<%@attribute name="backlogId" fragment="false" required="false" %>


<input type="hidden" name="widgetId" value="${widgetId}" />

<div class="widgetHeader">
  <ul>
    <li class="closeWidget">X</li>
    <li class="maximizeWidget" style="display: none;">+</li>
    <li class="minimizeWidget" style="display: none;">-</li>
  </ul>
  <div>
  	<c:choose>
  		<%-- widgetHeaders are used in many places; we 
  			 want the header text to become a link only when
  			 there's something (i.e. a backlogId) to point to.
  			 So far, this is used only in Product Leaf stories tab --%>
  		<c:when test="${backlogId != null}">
  		  <a href="editBacklog.action?backlogId=${backlogId}" class="widgetBacklogLink">${name}</a>
  		</c:when>
  		<c:otherwise>
  			${name}
  		</c:otherwise>
  	</c:choose>
</div>
</div>

<div class="widgetContent">
  <jsp:doBody />
</div>

