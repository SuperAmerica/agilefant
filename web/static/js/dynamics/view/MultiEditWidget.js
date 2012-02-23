
/**
 * Multiediting for story trees.
 */
var MultiEditWidget = function MultiEditWidget(storyTreeController) {
  this.storyTreeController = storyTreeController;

  this.element = $('<div class="multiEditContainer"/>').hide().appendTo(document.body);
  
  this.init();
};
MultiEditWidget.prototype = new ViewPart();



/**
 * Initialize the content
 */
MultiEditWidget.prototype.init = function() {
  var me = this;
  this.content = $('<ul/>');

  $('<li>State:</li>').appendTo(this.content);
  var stateElement = $('<li><select name="state" /></li>').appendTo(this.content);
  this.stateSelect = stateElement.find('select');

  var states = {'NOT_STARTED':'Not started','STARTED':'Started','PENDING':'Pending','BLOCKED':'Blocked','IMPLEMENTED':'Ready','DONE':'Done', 'DEFERRED':'Deferred' };
  
  $.each(states, jQuery.proxy(function(k,v) {
    this.stateSelect.append('<option value='+k+'>'+v+'</option>');
  }, this));
  
  $('<li>Labels:</li>').appendTo(this.content);
  this.labelElement = $('<li></li>').appendTo(this.content);
  this.labelsView = new AutoSuggest("ajax/lookupLabels.action", {
    startText: "Enter labels here.",
    queryParam: "labelName",
    searchObj: "name",
    selectedItem: "displayName",
    disableButtons: true,
    cancelCallback: function() {
    },
    successCallback: function(data) {
    },
    retrieveComplete: function(data) {
      var newData = [];
      for (var i = 0, len = data.length; i < len; i++) {
        var oneLabel = {
            value: data[i].displayName,
            name: data[i].name,
            displayName: data[i].displayName
        };
        newData[i] = oneLabel;
      }
      return newData;
    },
    minChars: 1
  }, this.labelElement);
  
  // storyIds, labelNames, ajax/editMultiple.action
  var buttonLi = $('<li/>').appendTo(this.content);
  $('<button class="dynamics-button">Save</button>').click(jQuery.proxy(function() {

	/* Confirms setting tasks to done */
	if (this.stateSelect.val() === "DONE") {
		for (var i=0; i<this.getSelected().length; i++) { // for each story selected
			var storyID = this.getSelected()[i];
			var storyTree = this.storyTreeController;
			
			storyTree._getStoryForId(storyID, function(object) {
				MultiEditWidget.prototype.confirmTasksAndChildrenToDone(object, storyTree, true);
			});
		}
	}
	
    jQuery.ajax({
      type: 'post',
      async:  'true',
      url: 'ajax/editMultipleStories.action',
      dataType: 'text',
      data: { storyIds: this.getSelected(), state: this.stateSelect.val(), labelNames: this.getLabels() },
      success: jQuery.proxy(function(data,status) {
        this.storyTreeController.refresh();
      }, this)
    });
    
    this.close();
  }, this)).appendTo(buttonLi);
  
  buttonLi = $('<li/>').appendTo(this.content);
  $('<button class="dynamics-button">Cancel</button>').click(jQuery.proxy(function() {
    this.storyTreeController.clearSelectedIds();
    this.close();
  }, this)).appendTo(buttonLi);
  
  this.content.appendTo(this.element);
};


MultiEditWidget.prototype.confirmTasksAndChildrenToDone = function(model, storyTree, isTopStory) {
	var tasks = model.getTasks();
	var children = model.getChildren();
	var nonDoneChildren = false;
	var nonDoneTasks = false;
	if (children.length > 0) {
		for (var i = 0; i < children.length; i++) {
		  if (children[i].getState() !== "DONE") {
			nonDoneChildren = true;
		  }
		}
	}
	if (tasks.length > 0) {
		for (var i = 0; i < tasks.length; i++) {
		  if (tasks[i].getState() !== "DONE") {
			nonDoneTasks = true;
		  }
		}
	}
	if (nonDoneChildren || nonDoneTasks) {
	  if (isTopStory) {
		  var msg = new DynamicsConfirmationDialog(
			  "Set all tasks' and stories' states to done?",
			  "The '" + model.getName() + "' story has undone child tasks/stories! Do you want to set them Done as well?",
			  function() {
			    if (nonDoneChildren) {
					for (var i = 0; i < children.length; i++) {
					  if (children[i].getState() !== "DONE") {
						 children[i].setState("DONE");
						 children[i].commit();
						 storyTree._getStoryForId(children[i].getId(), function(object) {
							MultiEditWidget.prototype.confirmTasksAndChildrenToDone(object, storyTree, false);
						});
						 storyTree.refresh();					
					  }
					}
				} else {
					if (nonDoneTasks)
						model.currentData.tasksToDone = true;
					model.commit();
					storyTree.refresh();
				}
			  },
			  function() {
				model.commit();
				storyTree.refresh();
			  }
			);
		} else {
			if (nonDoneChildren) {
				for (var i = 0; i < children.length; i++) {
					if (children[i].getState() !== "DONE") {
						children[i].setState("DONE");
						children[i].commit();
						storyTree._getStoryForId(children[i].getId(), function(object) {
							MultiEditWidget.prototype.confirmTasksAndChildrenToDone(object, storyTree, false);
						});
						storyTree.refresh();
					}

				}
			} else {
				if (nonDoneTasks)
					model.currentData.tasksToDone = true;
				model.commit();
				storyTree.refresh();
			}
		}
	} else {
	  model.commit();
	  storyTree.refresh();
	}
};

MultiEditWidget.prototype.getTree = function() {
  return this.storyTreeController;
};
MultiEditWidget.prototype.getSelected = function() {
  return this.storyTreeController.getSelectedIds();
};
MultiEditWidget.prototype.getLabels = function() {
  return this.labelsView.getValues();
};

MultiEditWidget.prototype.open = function() {
  this.element.show('fast',jQuery.proxy(function() {
    this.element.animate({'max-height':'80px'},500,'easeInOutCubic');
  }, this));
};
MultiEditWidget.prototype.close = function() {
  this.element.animate( { 'max-height' : '0' }, 500, 'easeInOutCubic', jQuery.proxy(function() {
    this.stateSelect.val('NOT_STARTED');
    this.labelsView.empty();
    this.element.hide();
  }, this));
};