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
        intervalUnit:   Timeline.DateTime.WEEK, 
        intervalPixels: 42
    })];
    var them = new Timeline.AgilefantTheme();
    bandInfos[0]["eventPainter"] = new Timeline.AgilefantEventPainter({showText: true, theme: them});
  
  productTimeLine = Timeline.create(document.getElementById("productTimeline"), bandInfos, Timeline.HORIZONTAL);
  /* Get the JSON data */
  var timelineActionURL = "timelineData.action?productId=" + productId;
  jQuery.getJSON(timelineActionURL,{},function(data,status) {
  	eventSource.loadJSON(data);
  	productTimeLine.hideLoadingMessage();
  	if($("#productTimelinePeriod").val() > 1) {
  		updateTimelinePeriod("#productTimelinePeriod");
  	}
  });
    productTimeLine.showLoadingMessage();
});
function updateTimelinePeriod(sender) {
	var mode = $(sender).val();
	var band = productTimeLine.getBand(0);
	var ether = band.getEther();
	var painter = band.getEventPainter();
	var modeToPix = {"1":200,"2":110,"3":50,"4":230};
	//calculate band center point
	var maxDate = band.getMinVisibleDate();
	var minDate = band.getMaxVisibleDate();
	var centerSec = minDate.getTime() + ((maxDate.getTime() - minDate.getTime())/2);
	var length;
	
	//adjust units and sizes
	if(mode == 3) {
		painter.setProjectPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
		band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.MONTH;
		length = 3600*24*30*15.2*1000;
	} else if(mode == 4) {
		painter.setProjectPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.YEAR];
		band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.YEAR;
		length = 3600*24*30*38*1000;
	} else if(mode == 2) {
		painter.setFullPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
		band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.MONTH;	
		length = 3600*24*30*6*1000;
	} else if(mode == 1) {
		painter.setFullPaintMode();
		ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
		band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.WEEK;
		length = 3600*24*120*1000;
	}
	//new start point for the band
	var startSec = Math.round(centerSec - length/2);
	var nStart = new Date();
	nStart.setTime(startSec);
	band.setMinVisibleDate(nStart);
	ether._pixelsPerInterval = modeToPix[mode];
	productTimeLine.paint();

}

