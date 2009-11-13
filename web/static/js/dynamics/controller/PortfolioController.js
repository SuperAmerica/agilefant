var PortfolioController = function PortfolioController(options) {
  this.projectListElement = options.projectListElement;
  this.timelineElement = options.timelineElement;
  
  this.initProjectListConfig();
  this.initTimeline();
  var start = new Date();
  var end = new Date();
  end.setDate(start.getDate() + 5);
  var event = new Timeline.DefaultEventSource.Event({
    start: start,
    end: end,
    instant: true,
    text: "Test"
  });
  this.eventSource.clear();
  this.eventSource.add(event);
};
PortfolioController.prototype = new CommonController();

PortfolioController.prototype.initTimeline = function() {
  var eventSource = new Timeline.DefaultEventSource();
  this.eventSource = eventSource;
  var bandInfos = [
      Timeline.createBandInfo({
        width: "100%",
        date: new Date(),
        intervalUnit: Timeline.DateTime.DAY,
        intervalPixels: 50,
        eventSource: eventSource
      })
  ];
  this.timeline = Timeline.create($("div#timeline")[0], bandInfos);
};
PortfolioController.prototype.initProjectListConfig = function() {
  
};