var DetailsView = function DetailsViewLink(controller, model, parentView) {
    this.controller = controller;
    this.model = model;
    this.parentView = parentView;
    this.dialogOpen = false;

    this.initialize();
};
    
DetailsView.prototype = new ViewPart();

/**
 * @private
 */
DetailsView.prototype.initialize = function() {
    var me = this;

    if (this.model.getContext) {
        var contextString = this.model.getContext().name;
    }
    else {
        var contextString = this.model.getBacklog().getName();
    }
    this.link = $('<a href="#" class="daily-work-task-context">' + contextString + ' ▼</a>');
    this.link.appendTo(this.parentView.getElement());
    
    this.toggleViewListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        } else {
            me.open();
        }

        event.stopPropagation();
        return false;
    };
    
    this.closeViewListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        };
        event.stopPropagation();
        return false;
    };
    
    this.propagatingCloseViewListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        };
        event.stopPropagation();
        return true;
    };
    
    this.closeViewWithDblclickListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        };
    };

    this.link.click(this.toggleViewListener);
    this.element = this.parentView.getElement();
};

/**
 * Display the menu
 */
DetailsView.prototype.open = function() {
    var me = this;

    this.dialogOpen = true;
    this.dialog = $('<div class="daily-work-details-view"></div>').appendTo(document.body);
    this.innerView = $('<div class="daily-work-details-inner">Loading...</div>').appendTo(this.dialog);
    
    $(window).click(this.closeViewListener);
    this.link.dblclick(this.closeViewWithDblclickListener);
    this.element.dblclick(this.closeViewWithDblclickListener);
    this.dialog.click(this.propagatingCloseViewListener);
    
    var off = this.parentView.getElement().offset();
    var width = this.parentView.getElement().width();
    var viewWidth = 400;
    
    var viewCss = {
        "position" : "absolute",
        "overflow" : "visible",
        "z-index" : "100",
        "top" : off.top + 18,
        "left" : off.left + (width - viewWidth) / 2,
        "width": viewWidth,
        "min-height": 200,
    };
    
    var me = this;
    this.model.retrieveDetails(function(data) {
        me.innerView.html(data);
    });
    
    this.dialog.css(viewCss);
};

/**
 * Close the menu
 */
DetailsView.prototype.close = function() {
    $(window).unbind('click', this.closeViewListener);
    this.dialog.unbind('click', this.propagatingCloseViewListener);
    this.link.unbind('dblclick', this.closeViewWithDblclickListener);
    this.element.unbind('dblclick', this.closeViewWithDblclickListener);

    this.dialog.remove();
    this.dialogOpen = false;
};
