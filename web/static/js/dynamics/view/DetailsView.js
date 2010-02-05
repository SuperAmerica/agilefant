var DetailsView = function DetailsViewLink(controller, model, parentView) {
    this.controller = controller;
    this.model = model;
    this.parentView = parentView;
    this.dialogOpen = false;

    this.initialize();
};

DetailsView.prototype = new ViewPart();

DetailsView.prototype.renderAlways = function() {
    return true;
};

DetailsView.prototype.initialize = function() {
    var me = this;

    this.element = this.parentView.getElement();
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
        }
        event.stopPropagation();
        return false;
    };
    
    this.propagatingCloseViewListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        }
        event.stopPropagation();
        return true;
    };
    
    this.closeViewWithDblclickListener = function(event) {
        if (me.dialogOpen) {
            me.close();
        }
    };

    this.render();
};

/**
 * @private
 */
DetailsView.prototype.render = function() {
    var contextString;
    if (this.model.getContext) {
        contextString = this.model.getContext().name;
    }
    else {
        contextString = this.model.getBacklog().getName();
    }
    
    $("span", this.element).remove();
    var noProperContext = ! this.model.getId();
    if (! contextString) {
        contextString = "(not set)";
        noProperContext = true;
    }
    
    if (noProperContext) {
        this.element.append($("<span>" + contextString + "</span>"));
        return;
    }

    if (! this.link) {
        this.link = $('<a href="#" class="daily-work-task-context"></a>');
        this.link.appendTo(this.element);
    
        this.link.click(this.toggleViewListener);
    }
    
    this.link.text(contextString + ' ∇');
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
    
    var top = this.link.offset().top + this.link.height();
    var viewWidth = 400;
    
    var viewCss = {
        "position" : "absolute",
        "overflow" : "visible",
        "z-index" : "100",
        "top" : top,
        "left" : off.left + (width - viewWidth) / 2,
        "width": viewWidth,
        "min-height": 200
    };
    
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
