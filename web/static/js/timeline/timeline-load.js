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
    var eventSourceThemes = new Timeline.AgilefantEventSource();
    /* Set the band properties */
    var bandInfos = [
    Timeline.createBandInfo({
        showEventText:  true,
        eventSource:    eventSource, 
        width:          "70%", 
        intervalUnit:   Timeline.DateTime.WEEK, 
        intervalPixels: 42
    }),
	Timeline.createBandInfo({
        showEventText:  true,
        eventSource:    eventSourceThemes, 
        width:          "30%", 
        intervalUnit:   Timeline.DateTime.WEEK, 
        intervalPixels: 42
    })];
    var them = new Timeline.AgilefantTheme();
    var themeBtheme = new Timeline.AgilefantThemeT();
    bandInfos[0].eventPainter = new Timeline.AgilefantEventPainter({showText: true, theme: them});
    bandInfos[1].eventPainter = new Timeline.AgilefantEventPainter({showText: true, theme: themeBtheme});
  	bandInfos[1].syncWith = 0;
  
  productTimeLine = Timeline.create(document.getElementById("productTimeline"), bandInfos, Timeline.HORIZONTAL);
  jQuery.getJSON("timelineData.action",{productId: productId},function(data,status) {
  	eventSource.loadJSON(data);
  	if($("#productTimelinePeriod").val() > 1) {
  		updateTimelinePeriod("#productTimelinePeriod");
  		productTimeLine.reDistributeWidths();
  		productTimeLine.hideLoadingMessage();
  	}
  });
  
  jQuery.getJSON("timelineThemeData.action",{productId: productId},function(data,status) {
  	eventSourceThemes.loadThemes(data);
  	productTimeLine.hideLoadingMessage();
  	productTimeLine.reDistributeWidths();
  });
  
    productTimeLine.showLoadingMessage();
});
function updateTimelinePeriod(sender) {
	var band = productTimeLine.getBand(0);
	var mode = $(sender).val();
	var maxDate = band.getMinVisibleDate();
	var minDate = band.getMaxVisibleDate();
	var centerSec = minDate.getTime() + ((maxDate.getTime() - minDate.getTime())/2);
	var center = new Date();
	center.setTime(centerSec);
	for(var i = 0; i < 2; i++) { //2 bands
		band = productTimeLine.getBand(i);
		var ether = band.getEther();
		var painter = band.getEventPainter();
		var modeToPix = {"1":200,"2":110,"3":50,"4":230};

		//adjust units and sizes
		if(mode == 3) {
			painter.setProjectPaintMode();
			ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
			band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.MONTH;
		} else if(mode == 4) {
			painter.setProjectPaintMode();
			ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.YEAR];
			band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.YEAR;
		} else if(mode == 2) {
			painter.setFullPaintMode();
			ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
			band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.MONTH;	
		} else if(mode == 1) {
			painter.setFullPaintMode();
			ether._interval = SimileAjax.DateTime.gregorianUnitLengths[Timeline.DateTime.MONTH];
			band.getEtherPainter()._unit = painter._unit = Timeline.DateTime.WEEK;
		}
		//alert(center);
		band.setCenterVisibleDate(center);
		ether._tracks = [];
		ether._pixelsPerInterval = modeToPix[mode];
		band.paint();
	}
	productTimeLine.getBand(0)._onChanging();
	var distributeLimit = false;
	//if(mode == 3 || mode == 4) { distributeLimit = 1; }
	productTimeLine.reDistributeWidths(distributeLimit);

}

