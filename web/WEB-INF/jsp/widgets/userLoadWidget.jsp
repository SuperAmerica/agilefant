<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>
<struct:widget name="Workload of: ${user.fullName}" widgetId="${widgetId}">
  <div class="widget-top-info">
    <a href="dailyWork.action?userId=${user.id}">View daily work of ${user.fullName}</a>
  </div>
  <div style="padding: 1em;">
    <div style="height: 100px;" id="userLoadWidget_${widgetId}"></div>
  </div>
  <script type="text/javascript">
  $(document).ready(function() {
  
    new UserLoadPlotWidget(${objectId},{ 
      total:{
            element: $("#userLoadWidget_${widgetId}")
        }
    }, 3);
  });
  </script>
</struct:widget>