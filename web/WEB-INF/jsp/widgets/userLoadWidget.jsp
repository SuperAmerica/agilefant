<%@include file="/WEB-INF/jsp/inc/_taglibs.jsp" %>

<struct:widget name="Workload of: ${user.fullName}">
<div style="position: relative; padding: 0em 1em;">
        <div style="float: left; width: 76%; height: 100px; margin-top: 10px; position: relative;" id="userLoadWidget_${widgetId}"></div>
      </div>
      <div class="load-legends" style="width: 10%;" id="userLoadLegends_${widgetId}">
        <div class="legend-box load-maximum"><ww:text name="load.threshold.maximum" /></div>
        <div class="legend-box load-critical"><ww:text name="load.threshold.critical" /></div>
        <div class="legend-box load-optimal-high"><ww:text name="load.threshold.optimalHigh" /></div>
        <div class="legend-box load-optimal-low"><ww:text name="load.threshold.optimalLow" /></div>
        <div class="legend-box load-low"><ww:text name="load.threshold.low" /></div>
</div>
<script type="text/javascript">
$(document).ready(function() {

  new UserLoadPlotWidget(${objectId},{ 
    total:{
          element: $("#userLoadWidget_${widgetId}"),
          legend: $("#userLoadLegends_${widgetId}")
      }
  }, 3);
});
</script>

</struct:widget>