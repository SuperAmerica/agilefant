<%@include file="../inc/_taglibs.jsp" %>

<c:forEach items="${stories}" var="item">
  <aef:storyTreeNode node="${item}" />
</c:forEach>
