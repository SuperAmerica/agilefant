<%@ include file="./inc/_taglibs.jsp"%>

<struct:htmlWrapper navi="portfolio">

<link rel="stylesheet" href="static/css/timeplot.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/timeline.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/ether.css" type="text/css"/>
<link rel="stylesheet" href="static/css/timeline/event.css" type="text/css"/>

<script type="text/javascript" src="static/js/excanvas.js"></script>
<script type="text/javascript" src="static/js/simile-widgets.js"></script>
<script type="text/javascript" src="static/js/simile/extensions/portfolio-eventsource.js"></script>

<script type="text/javascript">
window.Timeline.DateTime = window.SimileAjax.DateTime;
Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
$(document).ready(function() {
  var controller = new PortfolioController({
    timelineElement: $("#timeline"),
    rankedProjectsElement: $("#rankedProjects"),
    unrankedProjectsElement: $("#unrankedProjects")
  });
});
</script>

<h2>Project portfolio</h2>

	<div class="structure-main-block">	
		<div class="dynamictable ui-widget ui-widget-content ui-corner-all">
			<div class="dynamictable-caption dynamictable-caption-block ui-widget-header ui-corner-all">Portfolio timeline</div>
			<div id="timeline">
			</div>
		</div>
	</div>
	<div id="rankedProjects" class="structure-main-block">
	</div>
	<div id="unrankedProjects" class="structure-main-block">
	</div>

</struct:htmlWrapper>
