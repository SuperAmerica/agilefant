/* Initialize the Timeline object */
window.Timeline = new Object();
window.Timeline.DateTime = window.SimileAjax.DateTime;
window.Timeline.clientLocale = "en";
window.Timeline.serverLocale = "en";
window.Timeline.urlPrefix = "static/";
window.SimileAjax.urlPrefix = "static/";
var productTimeLine;
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
        intervalPixels: 200
    })];
    var them = new Timeline.AgilefantTheme();
    bandInfos[0]["eventPainter"] = new Timeline.AgilefantEventPainter({showText: true, theme: them});
  
  productTimeLine = Timeline.create(document.getElementById("productTimeline"), bandInfos, Timeline.HORIZONTAL);
  /* Get the JSON data */
  var timelineActionURL = "timelineData.action?productId=" + productId;
  jQuery.getJSON(timelineActionURL,{},function(data,status) {
  	eventSource.loadJSON(data);
  	productTimeLine.hideLoadingMessage();
  });
    productTimeLine.showLoadingMessage();
});
function updateTimelinePeriod(sender) {
	var mode = $(sender).val();
	var band = productTimeLine.getBand(0);
	var ether = band.getEther();
	var painter = band.getEventPainter();
	var modeToPix = {"1":200,"2":110,"3":50,"4":230};
	if(mode == 3) {
		painter.setProjectPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
		painter._unit = Timeline.DateTime.MONTH;
	} else if(mode == 4) {
		painter.setProjectPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.YEAR];
		painter._unit = Timeline.DateTime.YEAR;
	} else {
		painter.setFullPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
		painter._unit = Timeline.DateTime.MONTH;

	}
	ether._pixelsPerInterval = modeToPix[mode];
	productTimeLine.paint();

}

