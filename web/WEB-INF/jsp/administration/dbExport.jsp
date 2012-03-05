<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>Database export</h2>


<c:choose>
<c:when test="${currentUser.admin}"> 

<div id="databaseExportDiv" class="structure-main-block">
<div class="dynamictable ui-widget-content ui-corner-all">
  
  <div class="dynamictable-caption dynamictable-caption-block ui-widget-header ui-corner-all">
    Database export
  </div> 
  
  <div class="warning-note">
    You can create a zipped SQL dump of the database your Agilefant
	instance uses to save it on your computer. The database dump contains
	a history of all the changes made, so it can be used e.g. for research
	and learning purposes, such as exploring the evolution of a particular
	product backlog over time. You can also use this to e.g. create manual
	backups of your database.
  </div>
  
  <form action="generateDbExport.action">
  	<input type="submit" value="Export database" class="dynamics-button" />
  </form>  
  <form action="generateAnonymousDbExport.action">
  	<input type="submit" value="Export anonymous database" class="dynamics-button" />
  </form> 
  
</div>
</div> 

</c:when>
<c:otherwise>
  <h3>You are not an administrator, therefore you do not have permission to perform database export.</h3>
</c:otherwise>
</c:choose>

</jsp:body>
</struct:htmlWrapper>