
var TaskController = function(parentView, model, parentController, effortCell) {
  var me = this;
  this.model = model;
  this.parentView = parentView;
  this.effortCell = effortCell;
  this.parentController = parentController;
  this.initialized = false;
  if(agilefantUtils.isTimesheetsEnabled()) {
    this.effortCell.getElement().dblclick(function() {
      me.initialize();
      me.showTab(2);
      me.createEffortEntry();
    }).attr("title","Double-click to log effort.");
  }
};

var ReleaseStoryTabsController = function(element, model, parentController) {
  this.model = model;
  this.element = element;
  this.parentController = parentController;
  this.initialized = false;
};




/** PROJECT TASK TABS CONTROLLER **/
ReleaseStoryTabsController.prototype = {
  initialize: function() {
    if (this.initialized) {
      return;
    }
    var story = this.model;
    var taskListTabs = new ReleaseStoryTabs(story, this.element);
    var taskListTable = taskListTabs.addTab("Tasks").genericTable({noHeader: true, colCss: {}, colWidths: [{minwidth: 40, auto:true},{minwidth: 80, auto: true}]});
    
    // Header
    var headerRow = taskListTable.createRow();
    headerRow.getElement().addClass('dynamictable-header');
    headerRow.createCell().setValue('Name');
    headerRow.createCell().setValue('Description');
    
    for (var i = 0; i < story.tasks.length; i++) {
      var task = story.tasks[i];
      var newRow = taskListTable.createRow();
      var nameCell = newRow.createCell();
      var descCell = newRow.createCell();
      nameCell.setValue(task.getName());
      descCell.setValue(task.getDescription());
      
      nameCell.getElement().addClass('dynamictable-tasklist-cell');
      descCell.getElement().addClass('dynamictable-tasklist-cell');
    }
    
    this.initialized = true;
  },
  show: function() {
    this.initialize();
    this.element.show();
  },
  hide: function() {
    this.element.hide();
  }
};


/** TASK CONTROLLER **/

TaskController.prototype = {
  initialize: function() {
    if(this.initialized) {
      return;
    }
    var tabs = new TaskTabs(this.model,this.parentView.getElement());
    this.infoTable = tabs.addTab("Info").genericTable({noHeader: true, colCss: {}, colWidths: [{minwidth: 10, auto:true},{minwidth: 90, auto: true}]});
    if(agilefantUtils.isTimesheetsEnabled()) {
      var effView = tabs.addTab("Spent effort");
      this.spentEffortView = effView.spentEffortTable(); 
      this.spentEffortView.addCaptionAction("logEffort", {
        text: commonView.buttonWithIcon("effort", "Log effort"),
        callback: function() {
        me.createEffortEntry();
  
      }
      });
    }
    var me = this;
    var onShow = function(index) { me.showTab(index); };
    tabs.setOnShow(onShow);
    this.renderInfo();
    this.tabsLoaded = {};
    this.initialized = true;
  },
  showTab: function(index) {
    if(!this.tabsLoaded[index]) {
      if(index === 1) {
        this.renderSpentEffort();
        this.tabsLoaded[2] = true;
      }
    }
  },
  renderInfo: function() {
    var descRow = this.infoTable.createRow(this.model);
    descRow.createCell().setValue("Description");
    var me = this.model;
    descRow.createCell({
      get: function() { return me.getDescription(); },
      set: function(val) { me.setDescription(val); },
      type: "wysiwyg"
    });
    var creatorRow = this.infoTable.createRow();
    creatorRow.createCell().setValue("Creator");
    creatorRow.createCell({
      get: function() { return me.getCreator(); }
    });
    var created = this.infoTable.createRow();
    created.createCell().setValue("Created");
    created.createCell({
      get: function() { return me.getCreated(); },
      decorator: agilefantUtils.dateToString
    });
    this.infoTable.render();
  },
  renderSpentEffort: function() {
    var entries = this.model.getHourEntries();
    for(var i = 0; i < entries.length; i++) {
      this.addEffortEntry(entries[i]);
    }
    this.spentEffortView.render();
  },
  addEffortEntry: function(entry) {
    if(this.spentEffortView.isInTable(entry)) {
      return;
    }
    var row = this.spentEffortView.createRow(entry);
    row.createCell({
      get: function() { return entry.getDate();},
      decorator: agilefantUtils.dateToString,
      set: function(val) { entry.setDate(val); },
      type: "date"
    });
    row.createCell({
      get: function() { return entry.getUser().fullName; },
      getEdit: function() { return entry.getUser().id;},
      type: "select",
      items: function() { return agilefantUtils.getAllUsersAsObject(); },
      set: function(val) { entry.setUser(val); }
    });
    row.createCell({
      get: function() { return entry.getMinutesSpent();},
      decorator: agilefantParsers.hourEntryToString,
      type: "effort",
      set: function(val) { entry.setMinutesSpent(val); }
    });
    row.createCell({
      get: function() { return entry.getComment();},
      type: "text",
      set: function(val) { entry.setComment(val); }
    });
    var saveCb = function() {
      if(!row.saveEdit()) {
        return;
      }
      entry.commit(true);
      return false;
    };
    row.setSaveCallback(saveCb);
    var buttons = row.createCell({ type: "empty",
      get: function() { return ""; },
      buttons: {
        save: {text: "Save", action: saveCb},
      cancel: {text: "Cancel", action: function() {
        entry.rollBack();
        entry.cancelEdit();
        return false;
      }}
      }
    });
    buttons.setActionCell({items: [ 
                                   {
                                     text: "Edit",
                                     callback: function(row) {
                                     entry.beginTransaction();
                                     row.openEdit();
                                   }
                                   }, {
                                     text: "Delete",
                                     callback: function() {
                                     entry.remove();
                                   }
                                   }
                                   ]});
  
  },
  createEffortEntry: function() {
    var me = this;
    var parent = $("<div />").appendTo(document.body);
    parent.load("newHourEntry.action", {storyId: this.model.getId()}, function() { 
      var form = parent.find("form");
      var saveEffort = function() {
        if(!form.valid()) {
          return;
        }
        parent.dialog('destroy');
        var minutesSpent = form.find("input[name='hourEntry.minutesSpent']").val();
        var description = form.find("input[name='hourEntry.description']").val();
        var date = form.find("input[name=date]").val();
        var users = form.find("input[name='userIds']");
        parent.remove();
        var entry = new TaskHourEntryModel(me.model, null);
        entry.beginTransaction();
        entry.setComment(description);
        entry.setDate(date);
        entry.setMinutesSpent(minutesSpent);
        var userIds = [];
        if(users.length == 1) {
          userIds.push(users.val());
        } else if(users.length > 1) {
          users.each(function() {
            userIds.push($(this).val());
          });
        }
        entry.setUsers(userIds);
        entry.commit(function() {
          me.model.reloadData();
          me.renderSpentEffort();
        });
      };
      //hack: for some unknown reason form.submit refuses to work
      form.find("input:visible").keypress(function(event) {
        if(event.keyCode === 13) {
          saveEffort();
        }
      });
      addFormValidators(parent,saveEffort);
      parent.dialog({
        resizable: true,
        close: function() { parent.dialog('destroy'); parent.remove(); },
        minHeight:250,
        minWidth: 720,
        width: 720,
        modal: true,
        title: "Log effort",
        buttons: {
          'Save': function() {
            return saveEffort();
          },
          Cancel: function() {
            $(this).dialog('destroy');
            parent.remove();
          }
        }
      }); 
    });
  }
};
