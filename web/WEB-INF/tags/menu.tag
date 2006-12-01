<%@taglib uri="/WEB-INF/tlds/aef.tld" prefix="aef" %><%@ 
taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@
taglib uri="/webwork" prefix="ww" %>

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
 				<ww:url id="prodLink" action="listProducts" includeParams="none"/>

<!-- deliverable -->				
	<c:if test="${aef:isDeliverable(bct)}">
				<ww:a href="%{prodLink}">${bct.product.name}</ww:a>		
	</c:if>
<!-- iteration -->				
	<c:if test="${aef:isIteration(bct)}">
				<ww:a href="%{prodLink}">${bct.deliverable.product.name}</ww:a>	&gt;	
 				<ww:url id="delivLink" action="editDeliverable" includeParams="none">
 					<ww:param name="deliverableId" value="${bct.deliverable.id}"/>
 				</ww:url>
				<ww:a href="%{delivLink}">${bct.deliverable.name}</ww:a>		
	</c:if>

&nbsp;</div>
<div id="main">
