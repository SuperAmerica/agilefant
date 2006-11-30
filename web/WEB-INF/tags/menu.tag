   <%@tag description = "Menu" %>

   <%@attribute name="navi"%>
   <%@attribute name="subnavi"%>


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


<div id="bct">&nbsp;</div>
<div id="main">
