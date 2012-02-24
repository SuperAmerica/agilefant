<%@ include file="../inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="settings">

<jsp:body>

<h2>Access rights</h2>

<c:choose>
<c:when test="${true == currentUser.admin}">
  <h3>Welcome Administrator</h3>
  <div>Matrix info to go here...</div>
</c:when>
<c:otherwise>
  <h3>You are not an administrator therefore you do not have permission to set access rights.</h3>
</c:otherwise>
</c:choose>

</jsp:body>
</struct:htmlWrapper>                                                                            
