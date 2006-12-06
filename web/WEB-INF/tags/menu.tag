<%@ include file="../jsp/inc/_taglibs.jsp" %>

   <%@tag description = "Menu" %>

   <%@attribute name="navi"%>
   <%@attribute name="subnavi"%>
   <%@attribute type="java.util.Collection" name="pageHierarchy"%>

	<div id="menuwrap${navi}">
	<div id="submenuwrap${subnavi}">
<ul id="menu">
  <li id="nav1"><a href="/agilefant/listProducts.action">Home</a></li>
  <li id="nav2"><a href="/agilefant/myTasks.action">My tasks</a></li>
  <li id="nav3"><a href="/agilefant/listUsers.action">Manage users</a></li>
  <li id="nav4"><a href="/agilefant/hourReport.action">Report hours</a>
<!-- 
    <ul id="submenu4">
      <li id="subnav1"><a href="#">Tännekin</a></li>
      <li id="subnav2"><a href="#">Vois</a></li>
      <li id="subnav3"><a href="#">Laittaa</a></li>
      <li id="subnav4"><a href="#">Jotain</a></li>
    </ul>
     -->
  </li>
  <li id="nav5">
  	<a href="listActivityTypes.action">Manage activity & work types</a>
  </li>  
  <li id="nav6">
  	<a href="managementView.action">Portfolio hierarchy</a>
  </li>  
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
		
	</c:choose>

</c:forEach>

&nbsp;</div>
<div id="main">
