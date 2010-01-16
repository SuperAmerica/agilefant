var UserSpentEffortWidget = function UserSpentEffortWidget(element, userId) {
  this.element = element;
  this.userId = userId;
  var me = this;
  this.element.load("weeklySpentEffort.action",{userId: this.userId}, function() { 
    me._registerSpentEffortEvents(); 
  });
};
UserSpentEffortWidget.prototype._parseDateValues = function(strValue) {
  var parts = strValue.split("-");
  if(parts.length != 2) {
    return {};
  }
  return {week: parts[1], year: parts[0]};
};
UserSpentEffortWidget.prototype._registerSpentEffortEvents = function() {
  var me = this;
  //next, prev and current week
  this.element.find("a:not(.detailLink)").click(function() {
    me.element.load(this.href, function() { 
      me._registerSpentEffortEvents();
    });
    return false;
  });
  
  //week select
  this.element.find("select").change(function() {
    var data = me._parseDateValues($(this).val());
    data.userId = me.userId;
    me.element.load("weeklySpentEffort.action",data, function(data) { 
      me._registerSpentEffortEvents();
    });
  });
  
  //detailed spent effort for day
  this.element.find('a.detailLink').click(function() {
    $('a.detailLink',me.element).removeClass("detailedEffort");
    $(this).addClass("detailedEffort");
    $('.details',me.element).load(this.href);
    return false;
  });
};