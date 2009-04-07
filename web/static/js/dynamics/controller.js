var iterationController = function(iterationId, element) {
 this.iterationId = iterationId;
 this.element = element;
 var me = this;
 ModelFactory.getIteration(this.iterationId, function(data) { me.render(data); });
}
iterationController.prototype = {
    render: function(data) {
      this.view = jQuery(this.element).iterationGoalTable();
      var goals = data.getIterationGoals();
      var me = this;
      jQuery.each(goals, function(index, goal){
        var row = me.view.createRow(goal);
        var name = row.createCell({get: function() { return goal.getName();}});
        var sums = row.createCell({get: function() { 
        	return agilefantUtils.aftimeToString(goal.getEffortLeft()) + " / " +  
        		agilefantUtils.aftimeToString(goal.getOriginalEstimate()) + " / " +
        		agilefantUtils.aftimeToString(goal.getEffortSpent());
        	}});
        var tasks = row.createCell({get: function() { 
        	return goal.getDoneTasks() + " / " + goal.getTotalTasks();
        	}});
        var buttons = row.createCell({get: function() { return "Napit" }});
        var desc = row.createCell({get: function() { return goal.description; }});
      });
      this.view.render();
    }
}