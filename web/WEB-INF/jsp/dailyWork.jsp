<%@ include file="./inc/_taglibs.jsp" %>

<struct:htmlWrapper navi="dailyWork">

<link rel="stylesheet" href="static/css/dailywork.css" type="text/css"/>

<!-- User selector -->
<ww:form method="get">
<h2>The daily work of <ww:select list="enabledUsers"
    listKey="id" listValue="fullName" name="userId" value="%{user.id}"
    onchange="this.form.submit();" /></h2>
</ww:form>

<%@ include file="./inc/_userLoad.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
  var controller = new DailyWorkController({
    userId:                   ${user.id},
    workQueueElement:         $('#work-queue'),
    assignedStoriesElement:   $('#story-list'),
    tasksWithoutStoryElement: $('#task-list')
  });
});
</script>



<!-- Work queue -->
<form onsubmit="return false;"><div id="work-queue" class="structure-main-block"></div></form>

<!-- Assigned stories -->
<form onsubmit="return false;"><div id="story-list" class="structure-main-block"></div></form>

<!-- Tasks without story -->
<form onsubmit="return false;"><div id="task-list" class="structure-main-block"></div></form>


</struct:htmlWrapper>