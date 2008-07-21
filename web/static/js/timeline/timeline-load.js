/* Initialize the Timeline object */
window.Timeline = new Object();
window.Timeline.DateTime = window.SimileAjax.DateTime;
window.Timeline.clientLocale = "en";
window.Timeline.serverLocale = "en";
window.Timeline.urlPrefix = "static/";
window.SimileAjax.urlPrefix = "static/";

$(document).ready(function() {
    /* Set the month names */
    Timeline.GregorianDateLabeller.monthNames["en"] = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ];
    /* Create the datasource */
    var eventSource = new Timeline.AgilefantEventSource();
    /* Set the band properties */
    var bandInfos = [
    Timeline.createBandInfo({
        showEventText:  true,
        eventSource:    eventSource, 
        width:          "100%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 170
    })];
    var them = new Timeline.AgilefantTheme();
    bandInfos[0]["eventPainter"] = new Timeline.AgilefantEventPainter({showText: true, theme: them});
    /*
    Timeline.createBandInfo({
        showEventText:  false,
        eventSource:    eventSource2,
        width:          "50%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 100
    })
  ];
  bandInfos[1].syncWith = 0;
  bandInfos[1].highlight = false;*/
  
  tl = Timeline.create(document.getElementById("productTimeline"), bandInfos);
  /* Get the JSON data */
  var timelineActionURL = "timelineData.action?productId=" + productId;
  //Timeline.loadJSON(timelineActionURL, function(json) {  });
  jQuery.getJSON(timelineActionURL,{},function(data,status) {
  	eventSource.loadJSON(data);
  	tl.hideLoadingMessage();
  });
    tl.showLoadingMessage();
});


