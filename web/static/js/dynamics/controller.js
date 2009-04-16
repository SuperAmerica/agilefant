var iterationController = function(iterationId, element) {
 this.iterationId = iterationId;
 this.element = element;
 var me = this;
 ModelFactory.getIteration(this.iterationId, function(data) { me.render(data); });
}
iterationController.prototype = {
    changeIterationGoalPriority: function(ev, el) {
      var position = 0;
      var model = el.item.data("model");
      var cur = el.item.prev();
      while(cur.length == 1 && !cur.hasClass("dynamictable-notsortable")) {
        position++;
        cur = cur.prev();
      }
      model.setPriority(position);
      //all goals must be updated
      this.model.reloadGoalData();
    },
    deleteGoal: function(goal) {
      var parent = $("<div />").appendTo(document.body).text("Are you sure you wish to delete this iteration goal?");
      var me = this;
      parent.dialog({
        resizable: false,
        height:140,
        modal: true,
        overlay: {
          backgroundColor: '#000000',
          opacity: 0.5
        },
        buttons: {
          'Yes': function() {
            $(this).dialog('close');
            parent.remove();
            me.model.removeGoal(goal);
          },
          Cancel: function() {
            $(this).dialog('close');
            parent.remove();
          }
        }
      });
    },
    render: function(data) {
      var me = this;
      this.view = jQuery(this.element).iterationGoalTable();
      
      this.view.activateSortable({update: function(ev,el) { me.changeIterationGoalPriority(ev,el);}});
      
      var goals = data.getIterationGoals();
      this.model = data;
      jQuery.each(goals, function(index, goal){
        var row = me.view.createRow(goal);
        var prio = row.createCell({
          get: function() { return goal.priority; }
        });
        var name = row.createCell({
          type: "text", 
          get: function() { return goal.getName();}, 
          set: function(val){ goal.setName(val);}});
        name.activateSortHandle();
        var elsum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getEffortLeft()); }});
        var oesum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getOriginalEstimate()); }});
        var essum = row.createCell({
          get: function() { return agilefantUtils.aftimeToString(goal.getEffortSpent()); }});
        var tasks = row.createCell({
          get: function() { 
        	  return goal.getDoneTasks() + " / " + goal.getTotalTasks();
        	}});
        var buttons = row.createCell();
        buttons.setActionCell({items: [
                                       {
                                         text: "Edit",
                                         callback: function(row) {
                                           row.openEdit();
                                         }
                                       }, {
                                         text: "Delete",
                                         callback: function() {
                                           me.deleteGoal(goal);
                                         }
                                       }
                                       ]});
        var desc = row.createCell({
          type: "wysiwyg", 
          get: function() { return goal.description; }, 
          set: function(val) { goal.setDescription(val);},
          buttons: {
            save: {text: "Save", action: function() {
              goal.beginTransaction();
              row.saveEdit();
              goal.commit();
            }},
            cancel: {text: "Cancel", action: function() {
              row.cancelEdit();
            }}
          }});
      });
      var row = me.view.createRow();
      row.createCell();
      var name = row.createCell().setValue("Items without goal.");
      var elsum = row.createCell();
      var oesum = row.createCell();
      var essum = row.createCell();
      var tasks = row.createCell();
      row.setNotSortable();
      this.view.render();

    }
}