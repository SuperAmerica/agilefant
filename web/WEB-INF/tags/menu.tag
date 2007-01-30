<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Menu" %>

   <%@attribute name="navi"%>
   <%@attribute name="subnavi"%>
   <%@attribute type="java.util.Collection" name="pageHierarchy"%>

	<div id="menuwrap${navi}">
	<div id="submenuwrap${subnavi}">
<ul id="menu">
  <li id="nav1"><a href="/agilefant/myTasks.action">Heartbeat view</a></li>
  <li id="nav2"><a href="/agilefant/viewIteration.action">Iteration view</a></li>
  <li id="nav3"><a href="#">Project view?</a></li>
  <li id="nav4"><a href="/agilefant/listProducts.action">Product view</a></li>
  <li id="nav5"><a href="managementView.action">Development portfolio</a></li>  
  <li id="nav6"><a href="listActivityTypes.action">Activities & work types</a></li>  
  <li id="nav7"><a href="/agilefant/listUsers.action">Users</a></li>
</ul>
</div>
</div>

<div id="bct">

<c:forEach var="page" items="${pageHierarchy}" >
	<c:choose>
		<c:when test="${aef:isProduct(page)}">
			&gt;
			<ww:url id="prodLink" action="editProduct" includeParams="none">
				<ww:param name="productId" value="${page.id}"/>
			</ww:url>
			<ww:a href="%{prodLink}">${page.name}</ww:a>		
		</c:when>
		<c:when test="${aef:isDeliverable(page)}">
			&gt;
			<ww:url id="delivLink" action="editDeliverable" includeParams="none">
				<ww:param name="deliverableId" value="${page.id}"/>
			</ww:url>
			<ww:a href="%{delivLink}">${page.name}</ww:a>		
		</c:when >
		<c:when test="${aef:isIteration(page)}">
			&gt;
			<ww:url id="iterLink" action="editIteration" includeParams="none">
				<ww:param name="iterationId" value="${page.id}"/>
			</ww:url>
			<ww:a href="%{iterLink}">${page.name}</ww:a>		
		</c:when >
		<c:when test="${aef:isBacklogItem(page)}">
			&gt;
			<ww:url id="bliLink" action="editBacklogItem" includeParams="none">
				<ww:param name="backlogItemId" value="${page.id}"/>
			</ww:url>
			<ww:a href="%{bliLink}">${page.name}</ww:a>		
		</c:when >
		<c:when test="${aef:isTask(page)}">
			&gt;
			<ww:url id="taskLink" action="editTask" includeParams="none">
				<ww:param name="taskId" value="${page.id}"/>
			</ww:url>
			<ww:a href="%{taskLink}">${page.name}</ww:a>		
		</c:when >
		<c:when test="${aef:isUser(page)}">
			<ww:url id="userLink" action="listUsers" includeParams="none"/>
			<ww:a href="%{userLink}">User list</ww:a>		
		</c:when >
		<c:when test="${aef:isPortfolio(page)}">
			<ww:url id="homeLink" action="listProducts" includeParams="none"/>
			<ww:a href="%{homeLink}">Home</ww:a>		
		</c:when >
		<c:when test="${aef:isManagementPage(page)}">
			<ww:url id="homeLink" action="managementView" includeParams="none"/>
			<ww:a href="%{homeLink}">Home</ww:a>		
		</c:when >
		
	</c:choose>

</c:forEach>

&nbsp;</div>
<div id="main">
