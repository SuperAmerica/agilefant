<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>Access rights</h2>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new AccessListController({
    element: $('#accessListElement'),
    iterationElement: $('#accessIterationListElement')
  });
});
</script>


<c:choose>
<c:when test="${currentUser.admin}">
  <div id="accessListElement" style="min-width: 750px"> </div>
  <div id="accessIterationListElement" style="min-width: 750px"> </div>
</c:when>
<c:otherwise>
  <h3>You are not an administrator, therefore you do not have permission to set access rights.</h3>
</c:otherwise>
</c:choose>

</jsp:body>
</struct:htmlWrapper>                                                                            
