<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Menu" %>

   <%@attribute name="navi"%>
   <%@attribute name="subnavi"%>
   <%@attribute type="java.lang.Object" name="bct"%>


	<div id="menuwrap${navi}">
	<div id="submenuwrap${subnavi}">
<ul id="menu">
  <li id="nav1"><a href="/agilefant/listProducts.action">Home</a></li>
  <li id="nav2"><a href="/agilefant/myTasks.action">My tasks</a></li>
  <li id="nav3"><a href="/agilefant/listUsers.action">Manage users</a></li>
  <li id="nav4"><a href="/agilefant/testlayout.action">Test menu</a>  
    <ul id="submenu4">
      <li id="subnav1"><a href="#">Tännekin</a></li>
      <li id="subnav2"><a href="#">Vois</a></li>
      <li id="subnav3"><a href="#">Laittaa</a></li>
      <li id="subnav4"><a href="#">Jotain</a></li>
    </ul>
  </li>
</ul>
</div>
</div>

<div id="bct">
<aef:bct page="${bct}"/>



<c:set var="size" value="${fn:length(pageHierarchy)}" scope="page" />
<c:forEach var="i" end="${size}" begin="0" step="1">

<!-- kludge, hierarkia on jostain syystä aina käänteisessä järjestyksessä?? -->
    <c:set var="page" value="${pageHierarchy[size-i]}" scope="page" />
	<c:choose>
		<c:when test="${aef:isProduct(page)}">
			<ww:url id="prodLink" action="listProducts" includeParams="none"/>
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
		
	</c:choose>

</c:forEach>
		<c:if test="${aef:isUser(bct)}">
			<ww:url id="userLink" action="listUsers" includeParams="none"/>
			<ww:a href="%{userLink}">User list</ww:a>		
		</c:if>

&nbsp;</div>
<div id="main">
