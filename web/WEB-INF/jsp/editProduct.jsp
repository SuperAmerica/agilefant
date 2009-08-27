<%@ include file="./inc/_taglibs.jsp"%>
<%@ include file="./inc/_header.jsp"%>

<c:set var="divId" value="1336" scope="page" />
<aef:currentBacklog backlogId="${product.id}"/>

<aef:menu navi="backlog" title="${product.name}" menuContextId="${product.id}"/>

<ww:actionerror />
<ww:actionmessage />


<div class="backlogInfo" id="backlogInfo">
<ul class="backlogTabs">
  <li class=""><a href="#backlogDetails"><span><img
    alt="Edit" src="static/img/info.png" /> Info</span></a></li>
  <c:if test="${settings.hourReportingEnabled}">
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> Spent effort</span></a></li>
  </c:if>
  <li class=""><a href="#backlogSpentEffort"><span><img
    alt="Edit" src="static/img/timesheets.png" /> History</span></a></li>
</ul>

<div class="details" id="backlogDetails" style="overflow: auto;"></div>
<div class="details" id="backlogSpentEffort"></div>

</div>


<script type="text/javascript">
$(document).ready(function() {
  $("#backlogInfo").tabs();
  var controller = new ProductController({
    id: ${product.id},
    productDetailsElement: $("#backlogDetails"),
    projectListElement: $("#projects"),
    storyListElement: $('#stories')
  });
});
</script>

<%@include file="./inc/includeDynamics.jsp" %>

<form onsubmit="return false;"><div id="projects" style="min-width: 800px; width: 98%;">&nbsp;</div></form>

<form onsubmit="return false;"><div id="stories" style="min-width: 800px; width: 98%;">&nbsp;</div></form>




<%@ include file="./inc/_footer.jsp"%>